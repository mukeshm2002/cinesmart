package com.mk.cinesmart.controller;

import com.mk.cinesmart.model.User;
import com.mk.cinesmart.service.UserService;
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

    // 1. CUSTOM LOGIN PAGE
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // templates/login.html-ஐ காட்டும்
    }

    // 2. REGISTRATION PAGE
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register"; // templates/register.html-ஐ காட்டும்
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute("user") User user, Model model) {
        try {
            userService.registerUser(user);
            // லாகினுக்கு அனுப்பாமல், OTP சரிபார்க்கும் பக்கத்திற்கு அனுப்புகிறோம்
            return "redirect:/verify-otp?email=" + user.getEmail();
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", user);
            return "register";
        }
    }

    // 4. OTP வெரிஃபிகேஷன் பக்கம் காட்டுதல்
    @GetMapping("/verify-otp")
    public String showOtpPage(@RequestParam("email") String email, Model model) {
        model.addAttribute("email", email);
        return "verify-otp"; // templates/verify-otp.html தேவை
    }

    // 5. OTP சரிபார்த்தல்
    @PostMapping("/verify-otp")
    public String processOtp(@RequestParam("email") String email,
                             @RequestParam("otp") String otp,
                             Model model) {
        if (userService.verifyOtp(email, otp)) {
            return "redirect:/login?verified=true";
        } else {
            model.addAttribute("error", "தவறான OTP! மீண்டும் முயற்சிக்கவும்.");
            model.addAttribute("email", email);
            return "verify-otp";
        }
    }
}