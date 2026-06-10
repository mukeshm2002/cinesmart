package com.mk.cinesmart.service;


import com.mk.cinesmart.model.User;
import com.mk.cinesmart.model.UserRole;
import com.mk.cinesmart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // ஸ்பிரிங் செக்யூரிட்டி பாஸ்வேர்ட் என்கோடர்
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 1. USER REGISTRATION (With BCrypt Encryption)
    public User registerUser(User user) {
        // ஈமெயில் ஏற்கனவே இருக்கானு செக் பண்றோம்
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email ID already registered!");
        }

        // பாஸ்வேர்டை ஹேஷ் செய்கிறோம்
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // டிஃபால்ட்டா எல்லாருக்கும் ROLE_USER தான் போகும் (அட்மினை டேட்டாபேஸ்ல மாத்திக்கலாம்)
        if (user.getRole() == null) {
            user.setRole(UserRole.ROLE_USER);
        }

        return userRepository.save(user);
    }

    // 2. FIND USER BY EMAIL (For Authentication)
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    // 3. GET USERS BY ROLE (For Super Admin Dashboard)
    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }
}
