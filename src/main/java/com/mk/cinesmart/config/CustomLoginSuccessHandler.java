package com.mk.cinesmart.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String targetUrl = "/user/home"; // டீஃபால்ட் ஹோம் பேஜ்

        // ரோலை செக் பண்ணி சரியான URL-க்கு அனுப்புறோம்
        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();

            if (role.equals("ROLE_SUPER_ADMIN")) {
                targetUrl = "/super-admin/dashboard";
                break;
            } else if (role.equals("ROLE_THEATRE_ADMIN")) {
                targetUrl = "/theatre-admin/dashboard";
                break;
            } else if (role.equals("ROLE_USER")) {
                targetUrl = "/user/home";
                break;
            }
        }

        response.sendRedirect(targetUrl);
    }
}