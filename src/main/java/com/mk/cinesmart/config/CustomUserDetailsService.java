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
        // டேட்டாபேஸ்ல ஈமெயில் இருக்கான்னு தேடுறோம்
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // ஸ்பிரிங் செக்யூரிட்டிக்கு புரியுற மாதிரி யூசர் விபரங்களை மாற்றுகிறோம்
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(), // இது ஏற்கனவே BCrypt-ல் ஹேஷ் செய்யப்பட்ட பாஸ்வேர்ட்
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()))
                // எ.கா: ROLE_USER, ROLE_ADMIN, ROLE_SUPER_ADMIN
        );
    }
}
