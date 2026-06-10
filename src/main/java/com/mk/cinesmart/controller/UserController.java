package com.mk.cinesmart.controller;

import com.mk.cinesmart.model.Show;
import com.mk.cinesmart.model.User;
import com.mk.cinesmart.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private ShowService showService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private SnackService snackService;

    // 1. HOME PAGE - யூசர் லாகின் பண்ண உடனே இருக்குற எல்லா படங்களையும் காட்ட
    @GetMapping("/home")
    public String showUserHome(Model model) {
        model.addAttribute("movies", movieService.getAllMovies());
        return "user/home";
    }

    // 2. MOVIE DETAIL & SHOWS - ஒரு படத்தை கிளிக் பண்ணா அதோட விபரம் மற்றும் ஷோ டைமிங்ஸ காட்ட
    @GetMapping("/movie/{id}")
    public String showMovieDetails(@PathVariable("id") Long id, Model model) {
        model.addAttribute("movie", movieService.getMovieById(id));
        model.addAttribute("shows", showService.getUpcomingShowsForMovie(id));
        return "user/movie-details";
    }

    // 3. 2D SEAT SELECTION VIEW - தியேட்டர் சீட் மேப் மற்றும் ரீசேல் டிக்கெட்டுகளை காட்ட
    @GetMapping("/show/{showId}/seats")
    public String showSeatSelection(@PathVariable("showId") Long showId, Model model) {
        Show show = showService.getShowById(showId);
        model.addAttribute("show", show);
        model.addAttribute("screen", show.getScreen());

        // P2P Resale மார்க்கெட்ல மத்தவங்க விக்க வச்சிருக்க சீட்ஸ் (Yellow Seats)
        model.addAttribute("resaleBookings", bookingService.getResaleBookingsByShow(showId));
        return "user/seat-selection";
    }

    // 4. INSTANT CANCELLATION (50% REFUND) BUTTON
    @PostMapping("/booking/cancel/{bookingId}")
    public String cancelTicket(@PathVariable("bookingId") Long bookingId) {
        try {
            bookingService.cancelBookingInstantly(bookingId);
            return "redirect:/user/history?success=Ticket+Cancelled.+50%+Refund+Processed!";
        } catch (Exception e) {
            return "redirect:/user/history?error=" + e.getMessage();
        }
    }

    // 5. POST TICKET TO P2P RESALE MARKETPLACE (YELLOW SEAT LOGIC)
    @PostMapping("/booking/resale/list/{bookingId}")
    public String listTicketForResale(@PathVariable("bookingId") Long bookingId) {
        try {
            bookingService.listForResale(bookingId);
            return "redirect:/user/history?success=Ticket+Listed+in+Resale+Marketplace.+Waiting+for+buyers.";
        } catch (Exception e) {
            return "redirect:/user/history?error=" + e.getMessage();
        }
    }

    // 6. BUY A RESALED TICKET FROM MARKETPLACE (100% REFUND TO SELLER, 10% TO THEATER)
    @PostMapping("/booking/resale/buy/{originalBookingId}")
    public String buyResaledTicket(@PathVariable("originalBookingId") Long originalBookingId, Principal principal) {
        try {
            User buyer = userService.findUserByEmail(principal.getName());
            bookingService.purchaseResaledTicket(originalBookingId, buyer);
            return "redirect:/user/history?success=Resale+Ticket+Purchased+Successfully!";
        } catch (Exception e) {
            return "redirect:/user/home?error=" + e.getMessage();
        }
    }

    // 7. USER BOOKING HISTORY - யூசரோட பழைய மற்றும் தற்போதைய டிக்கெட் விபரங்கள்
    @GetMapping("/history")
    public String showBookingHistory(Model model, Principal principal) {
        User currentUser = userService.findUserByEmail(principal.getName());
        model.addAttribute("bookings", bookingService.getBookingsByUser(currentUser.getId()));
        return "user/history";
    }

    // 8. 🍿 SHOW CANTEEN SNACKS MENU
    @GetMapping("/show/{showId}/snacks")
    public String showSnacksMenuPage(@PathVariable("showId") Long showId,
                                     @RequestParam(value = "seats", required = true) String seats, // value ஆட் பண்ணுங்க
                                     Model model) {
        Show show = showService.getShowById(showId);
        if (show == null) return "redirect:/user/home?error=Show+Not+Found";

        model.addAttribute("show", show);
        // இங்க ஏதாவது பார்மட்டிங் தேவைப்பட்டா பண்ணுங்க
        model.addAttribute("selectedSeats", seats);
        model.addAttribute("allSnacks", snackService.getAllActiveSnacks());
        return "user/snacks-menu";
    }

    // 💳 9. MODIFIED PAYMENT PAGE (💡 FIX: டூப்ளிகேட் நீக்கப்பட்டு ஸ்நாக்ஸ் உடன் கூடிய சிங்கிள் பேமெண்ட் பேஜ்)
    @GetMapping("/show/{showId}/payment")
    public String showPaymentPage(@PathVariable("showId") Long showId,
                                  @RequestParam("seats") String seats,
                                  @RequestParam("ticketAmount") Double ticketAmount,
                                  // 💡 ஸ்நாக்ஸ் வாங்கலைனாலும் எர்ரர் வராது (Default values)
                                  @RequestParam(value = "snackAmount", defaultValue = "0.0") Double snackAmount,
                                  @RequestParam(value = "snackDetails", defaultValue = "None") String snackDetails,
                                  Model model) {

        Show show = showService.getShowById(showId);

        // 💡 ஷோ இல்லையென்றால் ஹோம் பேஜுக்கு அனுப்பிடுங்க
        if (show == null) {
            return "redirect:/user/home?error=Show+Expired";
        }

        model.addAttribute("show", show);
        model.addAttribute("selectedSeats", seats);
        model.addAttribute("ticketAmount", ticketAmount);
        model.addAttribute("snackAmount", snackAmount);
        model.addAttribute("snackDetails", snackDetails);
        model.addAttribute("totalAmount", (ticketAmount + snackAmount));

        return "user/payment";
    }

    // 💳 10. PROCESS FINAL CONFIRMED BOOKING PAYMENT
    @PostMapping("/booking/confirm")
    public String confirmBooking(@RequestParam("showId") Long showId,
                                 @RequestParam("seats") String seats,
                                 @RequestParam("amount") Double amount,
                                 Principal principal) {
        try {
            User currentUser = userService.findUserByEmail(principal.getName());
            Show show = showService.getShowById(showId);

            List<String> seatsList = java.util.Arrays.asList(seats.split(","));

            // 1. புது புக்கிங் ரெக்கார்டு கிரியேட் பண்றோம்
            com.mk.cinesmart.model.Booking booking = com.mk.cinesmart.model.Booking.builder()
                    .bookingId("CS-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                    .bookingDateTime(java.time.LocalDateTime.now())
                    .selectedSeats(seatsList)
                    .totalAmount(amount)
                    .status(com.mk.cinesmart.model.BookingStatus.CONFIRMED)
                    .user(currentUser)
                    .show(show)
                    .build();

            // 2. பேமெண்ட் ரெக்கார்டு கிரியேட் பண்றோம்
            com.mk.cinesmart.model.Payment payment = com.mk.cinesmart.model.Payment.builder()
                    .transactionId("TXN-" + java.util.UUID.randomUUID().toString().substring(0, 10).toUpperCase())
                    .totalPaidAmount(amount)
                    .paymentStatus(com.mk.cinesmart.model.PaymentStatus.SUCCESS)
                    .paymentDateTime(java.time.LocalDateTime.now())
                    .booking(booking)
                    .build();

            booking.setPayment(payment);

            // 3. சர்வீஸ் மூலமா சேவ் பண்றோம்
            bookingService.saveNewBooking(booking, payment);

            return "redirect:/user/history?success=Ticket+Booked+Successfully!+Enjoy+the+movie.";
        } catch (Exception e) {
            return "redirect:/user/home?error=" + e.getMessage();
        }
    }

}