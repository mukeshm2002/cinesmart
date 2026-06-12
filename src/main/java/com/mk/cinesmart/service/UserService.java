package com.mk.cinesmart.service;

import com.mk.cinesmart.model.User;
import com.mk.cinesmart.model.UserRole;
import com.mk.cinesmart.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Random;

@Service
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private EmailService emailService; // ஏற்கனவே உருவாக்கிய EmailService

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 1. USER REGISTRATION WITH OTP
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email ID already registered!");
        }

        // OTP உருவாக்கம் (6 இலக்கம்)
        String otp = String.format("%06d", new Random().nextInt(1000000));
        user.setOtp(otp);
        user.setVerified(false); // முதலில் வெரிஃபை ஆகவில்லை

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(UserRole.ROLE_USER);

        User savedUser = userRepository.save(user);

        // Welcome Email & OTP அனுப்புதல்
        emailService.sendWelcomeEmail(savedUser.getEmail(), otp);

        return savedUser;
    }

    // 2. OTP VERIFICATION
    public boolean verifyOtp(String email, String inputOtp) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null && user.getOtp().equals(inputOtp)) {
            user.setVerified(true);
            user.setOtp(null); // பயன்படுத்திய பிறகு OTP-ஐ நீக்கிவிடவும்
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // 3. CREATE THEATRE ADMIN
    // UserService.java-வில் மாற்றவும்
    @Transactional // இதைச் சேர்த்துக் கொள்ளுங்கள்
    public User createTheatreAdmin(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists!");
        }

        // பாஸ்வேர்டு என்க்ரிப்ஷன்
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(UserRole.ROLE_THEATRE_ADMIN);

        // இதை செக்யூர் செய்யுங்கள்
        return userRepository.save(user);
    }

    // 4. FIND USER
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // 5. GET BY ROLE
    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }
}