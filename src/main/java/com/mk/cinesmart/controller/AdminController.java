package com.mk.cinesmart.controller;


import com.mk.cinesmart.model.Screen;
import com.mk.cinesmart.model.Show;
import com.mk.cinesmart.model.Snack;
import com.mk.cinesmart.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ScreenService screenService;

    @Autowired
    private ShowService showService;

    @Autowired
    private SnackService snackService;

    @Autowired
    private MovieService movieService;

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/dashboard")
    public String showAdminDashboard(Model model) {
        try {
            model.addAttribute("totalRevenue", paymentService.getTotalRevenue());
            model.addAttribute("screens", screenService.getAllScreens());
            model.addAttribute("allSnacks", snackService.getAllSnacks());
            model.addAttribute("totalSnacksSold", snackService.getTotalSnacksSoldToday());

            // 💡 இன்னைக்கு நடக்குற எல்லா ஷோக்களையும் தனி லிஸ்ட்டா அனுப்புறோம்
            model.addAttribute("shows", showService.getAllShowsForToday());

        } catch (Exception e) {
            model.addAttribute("error", "Data load failed: " + e.getMessage());
            model.addAttribute("screens", new java.util.ArrayList<>());
        }
        return "admin/dashboard";
    }

    // =========================================================================
    // SCREEN MANAGEMENT
    // =========================================================================
    @GetMapping("/screen/add")
    public String showAddScreenForm(Model model) {
        model.addAttribute("screen", new Screen());
        return "admin/add-screen";
    }

    @PostMapping("/screen/save")
    public String saveScreen(@ModelAttribute("screen") Screen screen, Model model) {
        try {
            screenService.addScreen(screen);
            return "redirect:/admin/dashboard?success=Screen+Configured+with+2D+Grid";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "admin/add-screen";
        }
    }

    // =========================================================================
    // SHOW TIMING MANAGEMENT
    // =========================================================================
    @GetMapping("/show/add")
    public String showAddShowForm(Model model) {
        model.addAttribute("show", new Show());
        model.addAttribute("movies", movieService.getAllMovies());
        model.addAttribute("screens", screenService.getAllScreens());
        return "admin/add-show";
    }

    @PostMapping("/show/save")
    public String saveShow(@ModelAttribute("show") Show show, Model model) {
        try {
            showService.createShow(show);
            return "redirect:/admin/dashboard?success=Show+Scheduled+Successfully";
        } catch (IllegalStateException e) {
            // ஷோ டைம் ஓவர்லேப் ஆனா எர்ரரை ஃபார்முக்கே திருப்பி அனுப்புறோம்
            model.addAttribute("error", e.getMessage());
            model.addAttribute("movies", movieService.getAllMovies());
            model.addAttribute("screens", screenService.getAllScreens());
            return "admin/add-show";
        }
    }

    // =========================================================================
    // SNACK INVENTORY MANAGEMENT - UPDATED FOR STABILITY
    // =========================================================================
    @GetMapping("/snack/add")
    public String showAddSnackForm(Model model) {
        model.addAttribute("snack", new Snack());
        return "admin/add-snack";
    }

    @PostMapping("/snack/save")
    public String saveSnack(@ModelAttribute("snack") Snack snack,
                            @RequestParam(value = "snackImageFile", required = false) MultipartFile snackImageFile,
                            Model model) {
        try {
            // இப்போ இமேஜ் இல்லாம சப்மிட் பண்ணாலும் ஆப் கிராஷ் ஆகாது
            snackService.saveSnack(snack, snackImageFile);
            return "redirect:/admin/dashboard?success=Snack+Added+to+Canteen";
        } catch (Exception e) {
            // எர்ரர் மெசேஜை லாக் பண்ணி ஃபார்முக்கே காட்டுறோம்
            model.addAttribute("error", "Failed to save snack: " + e.getMessage());
            return "admin/add-snack";
        }
    }
}
