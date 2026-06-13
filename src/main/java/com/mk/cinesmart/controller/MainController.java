package com.mk.cinesmart.controller;

import com.mk.cinesmart.model.User;
import com.mk.cinesmart.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(@RequestParam(value = "step", required = false) String step,
                               @RequestParam(value = "email", required = false) String email,
                               @RequestParam(value = "error", required = false) String error,
                               Model model) {
        if ("otp".equals(step)) {
            model.addAttribute("email", email);
            if (error != null) {
                model.addAttribute("error", "Invalid OTP! Try again.");
            }
        } else {
            model.addAttribute("user", new User());
        }
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute("user") User user, HttpSession session, Model model) {
        try {
            // இமெயில் ஏற்கனவே உள்ளதா என சரிபார்க்கவும்
            if (userService.emailExists(user.getEmail())) {
                model.addAttribute("error", "Email ID already registered!");
                return "register";
            }

            // சர்வீஸ் மூலம் தற்காலிக பதிவு மற்றும் OTP அனுப்புதலை மேற்கொள்ளவும்
            String otp = userService.prepareTempRegistration(user, session);

            return "redirect:/register?step=otp&email=" + user.getEmail();
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @PostMapping("/verify-otp")
    public String processOtp(@RequestParam("email") String email,
                             @RequestParam("otp") String otp,
                             HttpSession session,
                             Model model) {
        // OTP சரியாக இருந்தால் மட்டுமே டேட்டாபேஸில் சேமிக்கப்படும்
        if (userService.verifyOtpAndSaveUser(email, otp, session)) {
            return "redirect:/login?verified=true";
        } else {
            return "redirect:/register?step=otp&email=" + email + "&error=true";
        }
    }
}