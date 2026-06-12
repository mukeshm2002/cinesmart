package com.mk.cinesmart.controller;

import com.mk.cinesmart.model.Movie;
import com.mk.cinesmart.model.UpcomingMovie;
import com.mk.cinesmart.model.User;
import com.mk.cinesmart.model.UserRole;
import com.mk.cinesmart.service.MovieService;
import com.mk.cinesmart.service.UpcomingMovieService;
import com.mk.cinesmart.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Controller
@RequestMapping("/super-admin")
public class SuperAdminController {


    @Autowired
    private MovieService movieService;

    @Autowired
    private UserService userService;

    // 1. READ: DASHBOARD (List All Movies)
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("movies", movieService.getAllMovies());
        model.addAttribute("theatreAdmins", userService.getUsersByRole(UserRole.ROLE_THEATRE_ADMIN));
        model.addAttribute("upcomingMovies", upcomingMovieService.getAllUpcomingMovies());
        return "super-admin/dashboard";
    }

    // 2. CREATE: GET FORM
    @GetMapping("/movie/add")
    public String showAddMovieForm(Model model) {
        model.addAttribute("movie", new Movie());
        return "super-admin/add-movie";
    }

    // 3. CREATE & UPDATE: SAVE MOVIE
    @PostMapping("/movie/save")
    public String saveMovie(@ModelAttribute("movie") Movie movie,
                            @RequestParam(value = "posterFile", required = false) MultipartFile posterFile,
                            Model model) {
        try {
            movieService.saveMovie(movie, posterFile);
            return "redirect:/super-admin/dashboard?success=Movie+Saved+Successfully";
        } catch (IOException e) {
            model.addAttribute("error", "Image upload failed: " + e.getMessage());
            return "super-admin/add-movie";
        }
    }

    // 4. UPDATE: EDIT FORM
    @GetMapping("/movie/edit/{id}")
    public String showEditMovieForm(@PathVariable("id") Long id, Model model) {
        Movie movie = movieService.getMovieById(id);
        model.addAttribute("movie", movie);
        return "super-admin/add-movie"; // அதே ஃபார்மை பயன்படுத்தலாம்
    }

    // 5. DELETE: REMOVE MOVIE
    @GetMapping("/movie/delete/{id}")
    public String deleteMovie(@PathVariable("id") Long id) {
        try {
            movieService.deleteMovie(id);
            return "redirect:/super-admin/dashboard?deleted=Movie+Removed";
        } catch (Exception e) {
            return "redirect:/super-admin/dashboard?error=" + e.getMessage();
        }
    }

    @Autowired
    private UpcomingMovieService upcomingMovieService;

    // 1. அப்-கமிங் மூவி ஆட் செய்யும் ஃபார்ம் பேஜ்
    @GetMapping("/upcoming/add")
    public String showAddUpcomingMoviePage(Model model) {
        model.addAttribute("movie", new UpcomingMovie());
        return "super-admin/add-upcoming-movie";
    }

    // 2. அப்-கமிங் மூவி சேமிக்கும் மெத்தட்
    @PostMapping("/upcoming/save")
    public String saveUpcomingMovie(@ModelAttribute("upcomingMovie") UpcomingMovie upcomingMovie,
                                    @RequestParam(value = "posterFile", required = false) MultipartFile posterFile,
                                    Model model) {
        try {
            // UpcomingMovieService-லும் இதே போன்ற save மெத்தடை வைத்துக்கொள்ளவும்
            upcomingMovieService.saveUpcomingMovie(upcomingMovie, posterFile);
            return "redirect:/super-admin/dashboard?success=Upcoming+Movie+Added+Successfully";
        } catch (IOException e) {
            model.addAttribute("error", "Image upload failed: " + e.getMessage());
            return "super-admin/add-upcoming-movie";
        }
    }

    // 3. லிஸ்ட் பார்க்க (Optional)
    @GetMapping("/upcoming/list")
    public String listUpcomingMovies(Model model) {
        model.addAttribute("movies", upcomingMovieService.getAllUpcomingMovies());
        return "super-admin/upcoming-list";
    }

    // 4. படத்தை நீக்க
    @GetMapping("/upcoming/delete/{id}")
    public String deleteUpcomingMovie(@PathVariable("id") Long id) {
        upcomingMovieService.deleteMovie(id);
        return "redirect:/super-admin/upcoming/list?deleted=true";
    }

    // --- THEATRE ADMIN MANAGEMENT ---

    // 1. தியேட்டர் அட்மினை ஆட் செய்யும் ஃபார்ம்
    @GetMapping("/theatre-admin/add")
    public String showAddTheatreAdminForm(Model model) {
        model.addAttribute("user", new User());
        return "super-admin/add-theatre-admin";
    }

    // 2. தியேட்டர் அட்மினை சேமிக்கும் மெத்தட்
    @PostMapping("/theatre-admin/save")
    public String saveTheatreAdmin(@ModelAttribute("user") User user) {
        // உங்கள் UserService-ல் உள்ள மெத்தட் பெயர் 'createTheatreAdmin' என்பதால் அதையே பயன்படுத்துகிறேன்
        userService.createTheatreAdmin(user);
        return "redirect:/super-admin/dashboard?success=Theatre+Admin+Added";
    }
}