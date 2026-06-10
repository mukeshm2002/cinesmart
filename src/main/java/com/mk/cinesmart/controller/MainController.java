package com.mk.cinesmart.controller;

import com.mk.cinesmart.model.User;
import com.mk.cinesmart.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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

    // 3. PROCESS REGISTRATION FORM (UPDATED)
    @PostMapping("/register")
    public String processRegister(@ModelAttribute("user") User user, Model model) {
        try {
            // 💡 சர்வீஸ் லேயர் மூலமா பாஸ்வேர்ட் என்க்ரிப்ஷன் மற்றும் ரோல் அசைன்மென்ட் (mk@example.com-க்கு சூப்பர் அட்மின்) நடக்கும்
            userService.registerUser(user);

            // ரெஜிஸ்டர் ஆனதும் லாகின் பேஜுக்கு சக்சஸ் மெசேஜோடு ரீடைரக்ட் பண்றோம்
            return "redirect:/login?registered=true";
        } catch (IllegalArgumentException e) {
            // ஒருவேளை ஈமெயில் ஏற்கனவே இருந்தாலோ அல்லது எர்ரர் வந்தாலோ ஃபார்ம்ல மெசேஜ் காட்டுகிறோம்
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", user); // 💡 ஃபில் பண்ண டேட்டா அழியாம இருக்க ஆப்ஜெக்ட்டை மறுபடி அனுப்புறோம்
            return "register";
        }
    }
}