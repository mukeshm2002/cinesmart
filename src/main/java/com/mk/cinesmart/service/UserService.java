package com.mk.cinesmart.service;

import com.mk.cinesmart.model.Theatre;
import com.mk.cinesmart.model.User;
import com.mk.cinesmart.model.UserRole;
import com.mk.cinesmart.repository.TheatreRepository;
import com.mk.cinesmart.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Random;

@Service
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private EmailService emailService;
    @Autowired private TheatreRepository theatreRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public boolean emailExists(String email) {
        return email != null && userRepository.existsByEmail(email.toLowerCase());
    }

    public String prepareTempRegistration(User user, HttpSession session) {
        String normalizedEmail = user.getEmail().toLowerCase();
        user.setEmail(normalizedEmail);

        // ஏற்கனவே செஷனில் யூசர் இருந்தால், அதை லாக் செய்துவிடலாம் (Security)
        session.invalidate();
        HttpSession newSession = session; // புதிய செஷன் உருவாக்கி தொடரவும்

        String otp = String.format("%06d", new Random().nextInt(1000000));

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(UserRole.ROLE_USER);
        user.setVerified(false);

        newSession.setMaxInactiveInterval(5 * 60);
        newSession.setAttribute("tempUser", user);
        newSession.setAttribute("tempOtp", otp);
        newSession.setAttribute("tempEmail", normalizedEmail);

        try {
            emailService.sendWelcomeEmail(normalizedEmail, otp);
        } catch (Exception e) {
            System.err.println("!!! மின்னஞ்சல் அனுப்ப முடியவில்லை: " + e.getMessage());
        }

        return otp;
    }

    public boolean verifyOtpAndSaveUser(String email, String inputOtp, HttpSession session) {
        String sessionEmail = (String) session.getAttribute("tempEmail");
        String sessionOtp = (String) session.getAttribute("tempOtp");
        User tempUser = (User) session.getAttribute("tempUser");

        // Null Check மற்றும் மின்னஞ்சல் சரிபார்ப்பு
        if (sessionEmail == null || !sessionEmail.equals(email.toLowerCase())) {
            return false; // மின்னஞ்சல் மாறுகிறது
        }

        if (sessionOtp != null && sessionOtp.equals(inputOtp) && tempUser != null) {
            tempUser.setVerified(true);
            userRepository.save(tempUser);
            session.invalidate();
            return true;
        }
        return false; // OTP தவறு
    }

    @Transactional
    public void createTheatreAdminWithTheatre(User user, String theatreName) {
        String email = user.getEmail().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("இந்த மின்னஞ்சல் ஏற்கனவே பதிவு செய்யப்பட்டுள்ளது!");
        }

        Theatre theatre = Theatre.builder()
                .name(theatreName)
                .location("Not Specified")
                .adminEmail(email)
                .build();

        theatreRepository.save(theatre);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEmail(email);
        user.setRole(UserRole.ROLE_THEATRE_ADMIN);
        user.setTheatre(theatre);
        user.setVerified(true);

        userRepository.save(user);
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new RuntimeException("பயனர் காணப்படவில்லை!"));
    }
}