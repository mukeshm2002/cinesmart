package com.mk.cinesmart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1. Password Encoder Bean - பாஸ்வேர்டை ஹேஷ் பண்ணவும், லாகின் அப்போ வெரிஃபை பண்ணவும் பயன்படும்
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. SecurityFilterChain - இதுதான் நம்ம ஆப்போட செக்யூரிட்டி கேட் கீப்பர் (Rules Engine)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Cross-Site Request Forgery (CSRF) பாதுகாப்பு (டெவலப்மென்ட் அப்போ ஈஸியா இருக்க இப்போதைக்கு டிஸேபிள் பண்றோம்)
                .csrf(csrf -> csrf.disable())

                // 🔐 URL Authorization Rules (ಯಾರ್ யார் எந்தெந்த பேஜுக்கு போகலாம்?)
                .authorizeHttpRequests(auth -> auth
                        // CSS, JS, Images போன்ற ஸ்டேடிக் ஃபைல்களுக்கு எல்லாருக்கும் அனுமதி
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()

                        // ரெஜிஸ்ட்ரேஷன், லாகின் மற்றும் எர்ரர் பேஜுக்கு எல்லாரும் வரலாம்
                        .requestMatchers("/register", "/login", "/error").permitAll()

                        // 💡 FIX 1: 'hasRole'-க்கு பதிலா 'hasAuthority' யூஸ் பண்ணி Enum வேல்யூவை அப்படியே மேட்ச் பண்றோம்
                        // 1. Super Admin URL பாதுகாப்பு
                        .requestMatchers("/super-admin/**").hasAuthority("ROLE_SUPER_ADMIN")

                        // 2. Theater Admin URL பாதுகாப்பு
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")

                        // 💡 FIX 2: சூப்பர் அட்மினும் யூசர் பேஜை (CineSmart ஹோம் பேஜ்) பார்க்கணும்னா hasAnyAuthority குடுக்கலாம்
                        // 3. End User URL பாதுகாப்பு
                        .requestMatchers("/user/**").hasAnyAuthority("ROLE_USER", "ROLE_SUPER_ADMIN")

                        // மத்த எந்த ஒரு URL-ஐ ஆக்சஸ் பண்ணனும்னாலும் கண்டிப்பா லாகின் பண்ணிருக்கணும்
                        .anyRequest().authenticated()
                )

                // 🔑 Custom Form Login Configuration
                .formLogin(form -> form
                        .loginPage("/login") // நம்ம கஸ்டம் Thymeleaf லாகின் HTML பேஜ் URL
                        .loginProcessingUrl("/login") // லாகின் ஃபார்ம் சப்மிட் பண்ண வேண்டிய POST URL
                        .usernameParameter("email") // லாகினுக்கு நம்ம 'username'-க்கு பதிலா 'email' யூஸ் பண்றோம்
                        .passwordParameter("password")

                        // 💡 FIX 3: லாகின் சக்சஸ் ஆன உடனே இப்போதைக்கு டெஸ்ட் பண்ண நேரா சூப்பர் அட்மின் டேஷ்போர்டுக்கே அனுப்புறோம்
                        .defaultSuccessUrl("/super-admin/dashboard", true)
                        .permitAll()
                )

                // 🚪 Logout Configuration
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true") // லாக் அவுட் ஆன உடனே லாகின் பேஜுக்கு கூட்டிட்டு போகும்
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }
}