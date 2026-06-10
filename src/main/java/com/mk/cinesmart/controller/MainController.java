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

    // 3. PROCESS REGISTRATION FORM
    @PostMapping("/register")
    public String processRegister(@ModelAttribute("user") User user, Model model) {
        try {
            userService.registerUser(user);
            return "redirect:/login?registered=true";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
}
