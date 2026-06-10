package com.mk.cinesmart.config;

import com.mk.cinesmart.model.User;
import com.mk.cinesmart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. டேட்டாபேஸ்ல ஈமெயில் இருக்கான்னு தேடுறோம்
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // 2. 💡 எஸ்டிடி ரோலை ஸ்ட்ரிங்கா மாத்தி அத்தாரிட்டியா கிரியேட் பண்றோம் (ROLE_SUPER_ADMIN, ROLE_USER, etc.)
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().name());

        // 3. ஸ்பிரிங் செக்யூரிட்டிக்கு புரியுற மாதிரி UserDetails ஆப்ஜெக்ட்டை பில்ட் பண்ணி ரிட்டன் பண்றோம்
        return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPassword()) // இது ஏற்கனவே BCrypt-ல் ஹேஷ் செய்யப்பட்ட பாஸ்வேர்ட்
                .authorities(Collections.singletonList(authority))
                .build(); // 👈 Spring Security 6.x-ல் 'withUsername().build()' போடுவதுதான் பெஸ்ட் பிராக்டிஸ்!
    }
}