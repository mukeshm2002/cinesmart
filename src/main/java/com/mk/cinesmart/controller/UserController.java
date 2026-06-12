package com.mk.cinesmart.controller;

import com.mk.cinesmart.model.*;
import com.mk.cinesmart.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

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

        model.addAttribute("movie", movie);
        model.addAttribute("shows", showService.getUpcomingShowsForMovie(id));
        model.addAttribute("feedbacks", feedbackService.getFeedbackByMovie(id));
        return "user/movie-details";
    }

    // 3. FEEDBACK SUBMISSION
    @PostMapping("/movie/{movieId}/feedback")
    public String submitFeedback(@PathVariable("movieId") Long movieId,
                                 @ModelAttribute("feedback") Feedback feedback,
                                 Principal principal) {
        User user = userService.findUserByEmail(principal.getName());
        feedback.setUser(user);
        feedback.setMovie(movieService.getMovieById(movieId));
        feedbackService.saveFeedback(feedback);
        return "redirect:/user/movie/" + movieId + "?success=Feedback+Submitted";
    }

    @GetMapping("/upcoming")
    public String showUpcomingMovies(Model model) {
        model.addAttribute("upcomingMovies", upcomingMovieService.getAllUpcomingMovies());
        return "user/upcoming"; // இது உங்கள் user/upcoming.html கோப்பை காட்டும்
    }

    // 4. SEAT SELECTION
    @GetMapping("/show/{showId}/seats")
    public String showSeatSelection(@PathVariable Long showId, Model model) {
        Show show = showService.getShowById(showId);

        // முக்கியமானது: ஷோ மூலம் ஸ்கிரீனை எடுக்க வேண்டும்
        if (show == null || show.getScreen() == null) {
            return "redirect:/user/home?error=Show+Details+Missing";
        }

        model.addAttribute("show", show);
        model.addAttribute("screen", show.getScreen()); // இந்த லைன் விடுபட்டிருந்தது!
        model.addAttribute("bookedSeats", bookingService.getBookedSeatsForShow(showId));

        // resaleSeats உங்கள் சர்வீஸில் இருந்தால் அதை இங்கேயும் சேர்க்கவும்
        //model.addAttribute("resaleSeats", bookingService.getResaleSeatsForShow(showId));

        return "user/seat-selection";
    }

    // 5. SNACKS MENU
    @GetMapping("/show/{showId}/snacks")
    public String showSnacksMenuPage(@PathVariable("showId") Long showId, @RequestParam("seats") String seats, Model model) {
        Show show = showService.getShowById(showId);
        model.addAttribute("allSnacks", snackService.getActiveSnacksByTheatre(show.getScreen().getTheatre().getId()));
        model.addAttribute("show", show);
        model.addAttribute("selectedSeats", seats);
        return "user/snacks-menu";
    }

    // 6. PAYMENT PAGE
    @GetMapping("/show/{showId}/payment")
    public String showPaymentPage(@PathVariable("showId") Long showId, @RequestParam("seats") String seats,
                                  @RequestParam("ticketAmount") Double ticketAmount,
                                  @RequestParam(value = "snackAmount", defaultValue = "0.0") Double snackAmount, Model model) {
        model.addAttribute("show", showService.getShowById(showId));
        model.addAttribute("selectedSeats", seats);
        model.addAttribute("totalAmount", (ticketAmount + snackAmount));
        return "user/payment";
    }

    @PostMapping("/booking/confirm")
    public String confirmBooking(@RequestParam("showId") Long showId,
                                 @RequestParam("seats") String seats,
                                 @RequestParam("amount") Double amount,
                                 @RequestParam(value = "snackIds", required = false) List<Long> snackIds,
                                 @RequestParam(value = "quantities", required = false) List<Integer> quantities,
                                 Principal principal) {

        // 1. தற்போதைய பயனரை கண்டறிதல்
        User user = userService.findUserByEmail(principal.getName());
        Show show = showService.getShowById(showId);

        // 2. தியேட்டர் கட்டமைப்பு சரியாக உள்ளதா என சரிபார்த்தல்
        // ஷோவிலோ அல்லது அதன் ஸ்கிரீனிலோ தியேட்டர் இல்லை என்றால் பிழை காட்டவும்
        if (show == null || (show.getTheatre() == null && (show.getScreen() == null || show.getScreen().getTheatre() == null))) {
            return "redirect:/user/home?error=System+Configuration+Error";
        }

        try {
            // 3. புக்கிங்கைச் சேமித்தல்
            Booking booking = bookingService.saveNewBooking(user, show, seats, amount, snackIds, quantities);

            // 4. டிக்கெட் இமேஜ் மற்றும் மின்னஞ்சல் (தனி try-catch - புக்கிங் சேவ் ஆன பின் இது நடந்தால் நல்லது)
            try {
                String ticketPath = ticketService.generateTicketImage(booking);

                String movieTitle = show.getMovie().getTitle();
                String subject = "CineSmart - Your Ticket Confirmed!";
                String body = "Dear " + user.getName() + ",\n\nYour booking for '" + movieTitle +
                        "' is confirmed.\nSeats: " + seats + "\nAmount: ₹" + amount +
                        "\n\nThank you for choosing CineSmart!";

                emailService.sendTicketEmail(user.getEmail(), subject, body, ticketPath);
            } catch (Exception e) {
                // மின்னஞ்சல் அனுப்ப முடியவில்லை என்றால், அதை மட்டும் லாக் செய்துவிட்டு புக்கிங்கைத் தொடரவும்
                System.err.println("Note: Booking confirmed, but email/ticket generation failed: " + e.getMessage());
            }

            return "redirect:/user/history?success=Booked+Successfully";

        } catch (Exception e) {
            // புக்கிங் சேவ் செய்யும்போது பிழை ஏற்பட்டால்
            e.printStackTrace();
            return "redirect:/user/home?error=Booking+Failed+Please+Try+Again";
        }
    }

    // 8. BOOKING HISTORY & MANAGEMENT
    @GetMapping("/history")
    public String showBookingHistory(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        User user = userService.findUserByEmail(principal.getName());
        if (user != null) {
            List<Booking> bookings = bookingService.getBookingsByUser(user.getId());
            model.addAttribute("bookings", bookings);
        }
        return "user/history";
    }

    // --- NEWLY ADDED P2P RESALE & CANCELLATION LOGIC ---

    // 9. INSTANT CANCELLATION (50% REFUND)
    @PostMapping("/booking/cancel/{bookingId}")
    public String cancelTicket(@PathVariable("bookingId") Long bookingId) {
        bookingService.cancelBookingInstantly(bookingId);
        return "redirect:/user/history?success=Ticket+Cancelled+with+50%+Refund";
    }

    // 10. LIST TICKET FOR RESALE
    @PostMapping("/booking/resale/list/{bookingId}")
    public String listTicketForResale(@PathVariable("bookingId") Long bookingId) {
        // உங்களுடைய BookingService-ல் listForResale மெத்தட் இருக்கிறதா என சரிபார்க்கவும்
        bookingService.listForResale(bookingId);
        return "redirect:/user/history?success=Ticket+Listed+for+Resale";
    }

    // 11. BUY RESALED TICKET
    @PostMapping("/booking/resale/buy/{originalBookingId}")
    public String buyResaledTicket(@PathVariable("originalBookingId") Long originalBookingId, Principal principal) {
        User buyer = userService.findUserByEmail(principal.getName());
        bookingService.purchaseResaledTicket(originalBookingId, buyer);
        return "redirect:/user/history?success=Resale+Ticket+Purchased+Successfully";
    }
}