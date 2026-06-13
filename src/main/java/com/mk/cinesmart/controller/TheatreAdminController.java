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

    private Long getTheatreId(Principal principal) {
        if (principal == null) throw new IllegalStateException("நீங்கள் லாகின் செய்யவில்லை!");

        User user = userService.findUserByEmail(principal.getName());

        if (user == null) {
            throw new IllegalStateException("பயனர் டேட்டாபேஸில் இல்லை!");
        }

        // தியேட்டர் இருக்கிறதா என சரிபார்க்கவும்
        if (user.getTheatre() == null) {
            // ஒருவேளை தியேட்டர் ஐடி மட்டும் டேபிளில் இருந்து, ஆப்ஜெக்ட் வராவிட்டால்
            // இங்கே பிழை கிடைக்கும்.
            System.err.println("Debug: User " + user.getEmail() + " has NO Theatre object!");
            throw new IllegalStateException("அட்மினுக்கு தியேட்டர் இணைக்கப்படவில்லை!");
        }

        return user.getTheatre().getId();
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

    // --- SHOW ADD PAGE (GET) ---
    @GetMapping("/show/add")
    public String showAddShowForm(Model model, Principal principal) {
        Long tId = getTheatreId(principal);
        model.addAttribute("show", new Show());
        model.addAttribute("movies", movieService.getAllMovies()); // எல்லா மூவீஸையும் காட்ட
        model.addAttribute("screens", screenService.getScreensByTheatre(tId)); // இந்த தியேட்டரின் ஸ்கிரீன்களை மட்டும் காட்ட
        return "theatre-admin/add-show";
    }

    // --- SCREEN ADD PAGE (GET) ---
    @GetMapping("/screen/add")
    public String showAddScreenForm(Model model) {
        model.addAttribute("screen", new Screen());
        return "theatre-admin/add-screen";
    }

    // --- SNACK ADD PAGE (GET) ---
    @GetMapping("/snack/add")
    public String showAddSnackForm(Model model) {
        model.addAttribute("snack", new Snack());
        return "theatre-admin/add-snack";
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
    public String saveShow(@ModelAttribute("show") Show show, Principal principal) {
        // ஷோ உருவாக்கும்போது தியேட்டரை கண்டிப்பாக செட் செய்யவும்
        show.setTheatre(userService.findUserByEmail(principal.getName()).getTheatre());
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