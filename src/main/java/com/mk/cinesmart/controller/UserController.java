package com.mk.cinesmart.controller;

import com.mk.cinesmart.model.*;
import com.mk.cinesmart.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired private MovieService movieService;
    @Autowired private ShowService showService;
    @Autowired private BookingService bookingService;
    @Autowired private UserService userService;
    @Autowired private SnackService snackService;
    @Autowired private FeedbackService feedbackService;
    @Autowired private EmailService emailService;
    @Autowired private TicketService ticketService;
    @Autowired private UpcomingMovieService upcomingMovieService;

    // 1. HOME PAGE
    @GetMapping("/home")
    public String showUserHome(Model model) {
        model.addAttribute("movies", movieService.getAllMovies());
        return "user/home";
    }

    // 2. MOVIE DETAILS
    @GetMapping("/movie/{id}")
    public String showMovieDetails(@PathVariable("id") Long id, Model model) {
        Movie movie = movieService.getMovieById(id);
        if (movie == null) return "redirect:/user/home?error=Movie+Not+Found";

        List<Show> shows = showService.getUpcomingShowsForMovie(id);

        // தியேட்டர் பெயரின் அடிப்படையில் ஷோக்களை குரூப் செய்தல்
        Map<String, List<Show>> showsByTheatre = shows.stream()
                .collect(Collectors.groupingBy(show -> show.getScreen().getTheatre().getName()));

        model.addAttribute("movie", movie);
        model.addAttribute("showsByTheatre", showsByTheatre); // குரூப் செய்த லிஸ்ட்டை அனுப்புகிறோம்
        model.addAttribute("feedbacks", feedbackService.getFeedbackByMovie(id));
        return "user/movie-details";
    }

    // 3. FEEDBACK SUBMISSION
    @PostMapping("/movie/{movieId}/feedback")
    @Transactional
    public String submitFeedback(@PathVariable("movieId") Long movieId,
                                 @ModelAttribute("feedback") Feedback feedback,
                                 Principal principal) {
        User user = userService.findUserByEmail(principal.getName());
        feedback.setUser(user);
        feedback.setMovie(movieService.getMovieById(movieId));
        feedbackService.saveFeedback(feedback);
        return "redirect:/user/movie/" + movieId + "?success=Feedback+Submitted";
    }

    // 4. SEAT SELECTION
    @GetMapping("/show/{showId}/seats")
    public String showSeatSelection(@PathVariable Long showId, Model model) {
        Show show = showService.getShowById(showId);
        if (show == null || show.getScreen() == null) {
            return "redirect:/user/home?error=Show+Details+Missing";
        }
        model.addAttribute("show", show);
        model.addAttribute("screen", show.getScreen());
        model.addAttribute("bookedSeats", bookingService.getBookedSeatsForShow(showId));
        return "user/seat-selection";
    }

    // 5. BOOKING CONFIRMATION (Security Improved)
    @PostMapping("/booking/confirm")
    @Transactional
    public String confirmBooking(@RequestParam("showId") Long showId,
                                 @RequestParam("seats") String seats,
                                 @RequestParam("amount") Double amount,
                                 @RequestParam(value = "snackIds", required = false) List<Long> snackIds,
                                 @RequestParam(value = "quantities", required = false) List<Integer> quantities,
                                 Principal principal) {

        User user = userService.findUserByEmail(principal.getName());
        Show show = showService.getShowById(showId);

        if (show == null) return "redirect:/user/home?error=Invalid+Show";

        try {
            Booking booking = bookingService.saveNewBooking(user, show, seats, amount, snackIds, quantities);

            // Async email logic or try-catch block
            try {
                String ticketPath = ticketService.generateTicketImage(booking);
                emailService.sendTicketEmail(user.getEmail(), "CineSmart - Ticket Confirmed!", "Your booking is confirmed.", ticketPath);
            } catch (Exception e) {
                System.err.println("Email failed, but booking successful: " + e.getMessage());
            }
            return "redirect:/user/history?success=Booked+Successfully";
        } catch (Exception e) {
            return "redirect:/user/home?error=Booking+Failed";
        }
    }

    // 6. CANCELLATION (Ownership Validation Added)
    @PostMapping("/booking/cancel/{bookingId}")
    @Transactional
    public String cancelTicket(@PathVariable("bookingId") Long bookingId, Principal principal) {
        User user = userService.findUserByEmail(principal.getName());
        Booking booking = bookingService.getBookingById(bookingId);

        if (booking != null && booking.getUser().getId().equals(user.getId())) {
            bookingService.cancelBookingInstantly(bookingId);
            return "redirect:/user/history?success=Cancelled+Successfully";
        }
        return "redirect:/user/history?error=Unauthorized";
    }

    // 7. LIST FOR RESALE (Ownership Validation Added)
    @PostMapping("/booking/resale/list/{bookingId}")
    @Transactional
    public String listTicketForResale(@PathVariable("bookingId") Long bookingId, Principal principal) {
        User user = userService.findUserByEmail(principal.getName());
        Booking booking = bookingService.getBookingById(bookingId);

        if (booking != null && booking.getUser().getId().equals(user.getId())) {
            bookingService.listForResale(bookingId);
            return "redirect:/user/history?success=Listed+for+Resale";
        }
        return "redirect:/user/history?error=Unauthorized";
    }
}