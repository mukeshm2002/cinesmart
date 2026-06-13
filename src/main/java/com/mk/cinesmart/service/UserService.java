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

    // UserService.java-வில் இந்த மாற்றத்தைச் செய்யவும்
    public String prepareTempRegistration(User user, HttpSession session) {
        String normalizedEmail = user.getEmail().toLowerCase();
        user.setEmail(normalizedEmail);

        // session.invalidate();  <-- இதை நீக்கிவிடுங்கள் (இதுதான் பிரச்சனை)

        // செஷனில் இருக்கும் பழைய தரவுகளை நீக்கிவிட்டு புதியவற்றைச் சேர்க்கவும்
        session.removeAttribute("tempUser");
        session.removeAttribute("tempOtp");
        session.removeAttribute("tempEmail");

        String otp = String.format("%06d", new Random().nextInt(1000000));

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(UserRole.ROLE_USER);
        user.setVerified(false);

        session.setMaxInactiveInterval(5 * 60); // 5 நிமிடம்
        session.setAttribute("tempUser", user);
        session.setAttribute("tempOtp", otp);
        session.setAttribute("tempEmail", normalizedEmail);

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

    // இந்த மெத்தட் தான் உங்கள் கன்ட்ரோலரில் missing ஆக இருக்கிறது
    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }
}