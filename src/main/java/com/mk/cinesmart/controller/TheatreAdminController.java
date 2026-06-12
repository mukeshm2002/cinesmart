package com.mk.cinesmart.controller;

import com.mk.cinesmart.model.*;
import com.mk.cinesmart.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;

@Controller
@RequestMapping("/theatre-admin")
public class TheatreAdminController {

    @Autowired private ScreenService screenService;
    @Autowired private ShowService showService;
    @Autowired private SnackService snackService;
    @Autowired private MovieService movieService;
    @Autowired private PaymentService paymentService;
    @Autowired private UserService userService;

    // தியேட்டர் அட்மினின் தியேட்டரை மட்டும் டேஷ்போர்டில் காட்டுதல்
    private Long getTheatreId(Principal principal) {
        return userService.findUserByEmail(principal.getName()).getTheatre().getId();
    }

    @GetMapping("/dashboard")
    public String showAdminDashboard(Model model, Principal principal) {
        Long tId = getTheatreId(principal);

        // விற்பனை கண்காணிப்பு (Ticket Sales)
        model.addAttribute("totalTicketsSold", showService.getTotalTicketsSoldByTheatre(tId));

        // மற்றவை அப்படியே இருக்கட்டும்
        model.addAttribute("totalRevenue", paymentService.getRevenueByTheatre(tId));
        model.addAttribute("screens", screenService.getScreensByTheatre(tId));
        model.addAttribute("shows", showService.getShowsByTheatre(tId));
        model.addAttribute("allSnacks", snackService.getSnacksByTheatre(tId));

        return "theatre-admin/dashboard";
    }

    // --- SCREEN CRUD ---
    @PostMapping("/screen/save")
    public String saveScreen(@ModelAttribute("screen") Screen screen, Principal principal) {
        screen.setTheatre(userService.findUserByEmail(principal.getName()).getTheatre());
        screenService.addScreen(screen);
        return "redirect:/theatre-admin/dashboard?success=Screen+Added";
    }

    @GetMapping("/screen/delete/{id}")
    public String deleteScreen(@PathVariable Long id) {
        screenService.deleteScreen(id);
        return "redirect:/theatre-admin/dashboard?success=Screen+Deleted";
    }

    // --- SHOW CRUD ---
    @PostMapping("/show/save")
    public String saveShow(@ModelAttribute("show") Show show) {
        showService.createShow(show);
        return "redirect:/theatre-admin/dashboard?success=Show+Scheduled";
    }

    @GetMapping("/show/delete/{id}")
    public String deleteShow(@PathVariable Long id) {
        showService.deleteShow(id);
        return "redirect:/theatre-admin/dashboard?success=Show+Cancelled";
    }

    // --- SNACK CRUD ---
    @PostMapping("/snack/save")
    public String saveSnack(@ModelAttribute("snack") Snack snack,
                            @RequestParam(required = false) MultipartFile snackImageFile,
                            Principal principal) throws Exception {
        snack.setTheatre(userService.findUserByEmail(principal.getName()).getTheatre());
        snackService.saveSnack(snack, snackImageFile);
        return "redirect:/theatre-admin/dashboard?success=Snack+Updated";
    }

    @GetMapping("/snack/delete/{id}")
    public String deleteSnack(@PathVariable Long id) {
        snackService.deleteSnack(id);
        return "redirect:/theatre-admin/dashboard?success=Snack+Removed";
    }
}