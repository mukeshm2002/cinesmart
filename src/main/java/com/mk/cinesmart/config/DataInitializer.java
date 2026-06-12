package com.mk.cinesmart.config;

import com.mk.cinesmart.model.User;
import com.mk.cinesmart.model.UserRole;
import com.mk.cinesmart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // ஏற்கனவே Super Admin இருக்கிறாரா என்று பார்க்கவும்
        if (!userRepository.existsByRole(UserRole.ROLE_SUPER_ADMIN)) {
            User superAdmin = new User();
            superAdmin.setName("Super Admin");
            superAdmin.setEmail("admin@cinesmart.com"); // உங்கள் மின்னஞ்சல்
            superAdmin.setMobileNumber("0000000000");
            superAdmin.setPassword(passwordEncoder.encode("Admin@123")); // பாதுகாப்பான கடவுச்சொல்
            superAdmin.setRole(UserRole.ROLE_SUPER_ADMIN);
            superAdmin.setVerified(true);

            userRepository.save(superAdmin);
            System.out.println("Super Admin created successfully!");
        }
    }
}
