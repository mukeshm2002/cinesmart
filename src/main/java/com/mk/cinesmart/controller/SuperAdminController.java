package com.mk.cinesmart.controller;


import com.mk.cinesmart.model.Movie;
import com.mk.cinesmart.service.MovieService;
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

    // 1. DASHBOARD - ஹோம் பேஜ் (எல்லா மூவிகளையும் லிஸ்ட் பண்ணி காட்ட)
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("movies", movieService.getAllMovies());
        return "super-admin/dashboard"; // templates/super-admin/dashboard.html-க்கு போகும்
    }

    // 2. GET FORM - புது மூவி ஆட் பண்ற ஃபார்ம்-ஐ காட்ட
    @GetMapping("/movie/add")
    public String showAddMovieForm(Model model) {
        model.addAttribute("movie", new Movie());
        return "super-admin/add-movie"; // templates/super-admin/add-movie.html
    }

    // 3. POST FORM - ஃபார்ம் சப்மிட் பண்ணும்போது Cloudinary-ல் அப்லோட் செய்து டேட்டாபேஸில் சேவ் செய்ய
    @PostMapping("/movie/save")
    public String saveMovie(@ModelAttribute("movie") Movie movie,
                            @RequestParam("posterFile") MultipartFile posterFile,
                            Model model) {
        try {
            movieService.saveMovie(movie, posterFile);
            return "redirect:/super-admin/dashboard?success=Movie+Added+Successfully";
        } catch (IOException e) {
            model.addAttribute("error", "Image upload failed: " + e.getMessage());
            return "super-admin/add-movie";
        }
    }

    // 4. DELETE MOVIE - ஒரு மூவியை தியேட்டர் லிஸ்ட்டில் இருந்து தூக்க
    @GetMapping("/movie/delete/{id}")
    public String deleteMovie(@PathVariable("id") Long id) {
        try {
            movieService.deleteMovie(id);
            return "redirect:/super-admin/dashboard?deleted=Movie+Removed";
        } catch (Exception e) {
            return "redirect:/super-admin/dashboard?error=" + e.getMessage();
        }
    }
}
