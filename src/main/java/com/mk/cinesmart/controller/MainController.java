package com.mk.cinesmart.controller;

import com.mk.cinesmart.model.User;
import com.mk.cinesmart.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MainController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "verified", required = false) boolean verified, Model model) {
        if (verified) {
            model.addAttribute("message", "பதிவு வெற்றி! இப்போது லாகின் செய்யவும்.");
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(@RequestParam(value = "step", required = false) String step,
                               Model model) {
        if ("otp".equals(step)) {
            return "register-otp"; // தனி HTML பக்கம் இருப்பது சிறந்தது
        }
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute("user") User user,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        try {
            if (userService.emailExists(user.getEmail())) {
                redirectAttributes.addFlashAttribute("error", "இந்த மின்னஞ்சல் ஏற்கனவே உள்ளது!");
                return "redirect:/register";
            }

            userService.prepareTempRegistration(user, session);
            // URL-ல் மின்னஞ்சலை அனுப்பாமல் செஷன் மூலம் கையாளுகிறோம்
            return "redirect:/register?step=otp";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "பதிவு செய்வதில் சிக்கல்: " + e.getMessage());
            return "redirect:/register";
        }
    }

    @PostMapping("/verify-otp")
    public String processOtp(@RequestParam("otp") String otp,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        // செஷனில் இருந்து மின்னஞ்சலை எடுக்கிறோம் (URL-ல் அனுப்பத் தேவையில்லை)
        String email = (String) session.getAttribute("tempEmail");

        if (email != null && userService.verifyOtpAndSaveUser(email, otp, session)) {
            return "redirect:/login?verified=true";
        } else {
            redirectAttributes.addFlashAttribute("error", "தவறான OTP! மீண்டும் முயலவும்.");
            return "redirect:/register?step=otp";
        }
    }
}