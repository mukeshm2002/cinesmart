package com.mk.cinesmart.config;

import com.mk.cinesmart.model.UserRole;
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
                // Cross-Site Request Forgery (CSRF) பாதுகாப்பு (Thymeleaf ஃபார்ம்ஸ்ல இது ஆட்டோமேட்டிக்கா ஒர்க் ஆகும்)
                .csrf(csrf -> csrf.disable()) // டெவலப்மென்ட் அப்போ ஈஸியா இருக்க இப்போதைக்கு டிஸேபிள் பண்றோம்

                // 🔐 URL Authorization Rules (யார் யாருக்கு எதுக்கு பர்மிஷன் இருக்கு?)
                .authorizeHttpRequests(auth -> auth
                        // CSS, JS, Images போன்ற ஸ்டேடிக் ஃபைல்களுக்கு எல்லாருக்கும் அனுமதி
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()

                        // ரெஜிஸ்ட்ரேஷன் மற்றும் லாகின் பேஜுக்கு எல்லாரும் வரலாம்
                        .requestMatchers("/register", "/login", "/error").permitAll()

                        // 1. Super Admin URL பாதுகாப்பு
                        .requestMatchers("/super-admin/**").hasRole("SUPER_ADMIN")

                        // 2. Theater Admin URL பாதுகாப்பு
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // 3. End User URL பாதுகாப்பு
                        .requestMatchers("/user/**").hasRole("USER")

                        // மத்த எந்த ஒரு URL-ஐ ஆக்சஸ் பண்ணனும்னாலும் கண்டிப்பா லாகின் பண்ணிருக்கணும்
                        .anyRequest().authenticated()
                )

                // 🔑 Custom Form Login Configuration
                .formLogin(form -> form
                        .loginPage("/login") // நம்ம கஸ்டம் Thymeleaf லாகின் HTML பேஜ் URL
                        .loginProcessingUrl("/login") // லாகின் ஃபார்ம் சப்மிட் பண்ண வேண்டிய POST URL
                        .usernameParameter("email") // லாகினுக்கு நம்ம 'username'-க்கு பதிலா 'email' யூஸ் பண்றோம்
                        .passwordParameter("password")
                        // லாகின் சக்சஸ் ஆன உடனே அவங்க ரோலுக்கு ஏத்த மாதிரி டேஷ்போர்டுக்கு அனுப்ப ஒரு கஸ்டம் ஹேண்ட்லர் வைக்கலாம்,
                        // இப்போதைக்கு ஹோம் பேஜுக்கு அனுப்புறோம்
                        .defaultSuccessUrl("/user/home", true)
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
