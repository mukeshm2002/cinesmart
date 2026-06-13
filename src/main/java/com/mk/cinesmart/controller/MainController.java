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

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(@RequestParam(value = "step", required = false) String step,
                               @RequestParam(value = "email", required = false) String email,
                               Model model) {
        if ("otp".equals(step)) {
            model.addAttribute("email", email);
        } else {
            model.addAttribute("user", new User());
        }
        return "register"; // templates/register.html-ஐ காட்டும்
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute("user") User user, Model model) {
        try {
            userService.registerUser(user);
            return "redirect:/register?step=otp&email=" + user.getEmail();
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @PostMapping("/verify-otp")
    public String processOtp(@RequestParam("email") String email,
                             @RequestParam("otp") String otp,
                             Model model) {
        if (userService.verifyOtp(email, otp)) {
            return "redirect:/login?verified=true";
        } else {
            // பிழை இருந்தால் மீண்டும் அதே OTP பக்கத்திற்கே அனுப்பவும்
            return "redirect:/register?step=otp&email=" + email + "&error=InvalidOTP";
        }
    }
}