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
        return userRepository.existsByEmail(email);
    }

    // 1. தற்காலிகமாக செஷனில் வைத்து, OTP அனுப்புதல் (டேட்டாபேஸில் சேமிக்கப்படாது)
    public String prepareTempRegistration(User user, HttpSession session) {
        String otp = String.format("%06d", new Random().nextInt(1000000));

        // கடவுச்சொல்லை என்க்ரிப்ட் செய்து செஷனில் சேமிப்போம்
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(UserRole.ROLE_USER);
        user.setVerified(false);

        // செஷனில் யூசர் மற்றும் OTP-ஐ தற்காலிகமாக ஒதுக்குதல்
        session.setAttribute("tempUser", user);
        session.setAttribute("tempOtp", otp);
        session.setAttribute("tempEmail", user.getEmail());

        try {
            emailService.sendWelcomeEmail(user.getEmail(), otp);
        } catch (Exception e) {
            System.err.println("!!! மின்னஞ்சல் அனுப்ப முடியவில்லை: " + e.getMessage());
        }

        return otp;
    }

    // 2. OTP சரிபார்த்து, சரியாக இருந்தால் மட்டும் டேட்டாபேஸில் சேமித்தல்
    public boolean verifyOtpAndSaveUser(String email, String inputOtp, HttpSession session) {
        String sessionEmail = (String) session.getAttribute("tempEmail");
        String sessionOtp = (String) session.getAttribute("tempOtp");
        User tempUser = (User) session.getAttribute("tempUser");

        if (sessionEmail != null && sessionEmail.equals(email) &&
                sessionOtp != null && sessionOtp.equals(inputOtp) && tempUser != null) {

            // யூசரை முழுமையாக வெரிஃபை செய்து டேட்டாபேஸில் சேமிக்கிறோம்
            tempUser.setVerified(true);
            userRepository.save(tempUser); // உண்மையான UserRepository save

            // செஷனில் உள்ள தற்காலிகத் தரவுகளை நீக்கிவிடுதல்
            session.removeAttribute("tempUser");
            session.removeAttribute("tempOtp");
            session.removeAttribute("tempEmail");

            return true;
        }
        return false;
    }

    @Transactional
    public void createTheatreAdminWithTheatre(User user, String theatreName) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists!");
        }

        Theatre theatre = Theatre.builder()
                .name(theatreName)
                .location("Not Specified")
                .adminEmail(user.getEmail())
                .build();

        theatreRepository.save(theatre);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(UserRole.ROLE_THEATRE_ADMIN);
        user.setTheatre(theatre);

        userRepository.save(user);
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }
}