package com.mk.cinesmart.service;

import com.mk.cinesmart.model.*;
import com.mk.cinesmart.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {

    @Autowired private BookingRepository bookingRepository;
    @Autowired private PaymentRepository paymentRepository;
    @Autowired private ShowRepository showRepository;
    @Autowired private SnackRepository snackRepository;

    // 1. INSTANT CANCELLATION
    @Transactional
    public Booking cancelBookingInstantly(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new RuntimeException("Booking not found"));
        if (booking.getStatus() != BookingStatus.CONFIRMED) throw new IllegalStateException("Only CONFIRMED tickets can be cancelled.");

        booking.setStatus(BookingStatus.CANCELLED);
        Payment payment = booking.getPayment();
        if (payment != null) {
            payment.setRefundedAmount(booking.getTotalAmount() * 0.50);
            payment.setPaymentStatus(PaymentStatus.REFUNDED_PARTIAL);
            paymentRepository.save(payment);
        }
        Show show = booking.getShow();
        show.setAvailableSeats(show.getAvailableSeats() + booking.getSelectedSeats().size());
        showRepository.save(show);
        return bookingRepository.save(booking);
    }

    // 2. LIST TICKET FOR RESALE
    @Transactional
    public void listForResale(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setStatus(BookingStatus.RESALE_LISTED);
        bookingRepository.save(booking);
    }

    // 3. PURCHASE RESALED TICKET
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Booking purchaseResaledTicket(Long originalBookingId, User buyer) {
        Booking originalBooking = bookingRepository.findById(originalBookingId).orElseThrow(() -> new RuntimeException("Booking not found"));
        if (originalBooking.getStatus() != BookingStatus.RESALE_LISTED) throw new IllegalStateException("Ticket not available.");

        originalBooking.setStatus(BookingStatus.RESALE_SOLD);
        bookingRepository.save(originalBooking);

        Booking newBooking = Booking.builder()
                .bookingId("CS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .bookingDateTime(LocalDateTime.now())
                .selectedSeats(new ArrayList<>(originalBooking.getSelectedSeats()))
                .totalAmount(originalBooking.getTotalAmount())
                .status(BookingStatus.CONFIRMED)
                .user(buyer)
                .show(originalBooking.getShow())
                .theatreId(originalBooking.getTheatreId())
                .build();

        return bookingRepository.save(newBooking);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Booking saveNewBooking(User user, Show show, String seats, Double amount, List<Long> snackIds, List<Integer> quantities) {
        List<String> seatsList = Arrays.asList(seats.split(","));

        // 1. Theatre ID-ஐ பாதுகாப்பாகப் பெறுதல்
        Long theatreId = null;
        if (show.getTheatre() != null) {
            theatreId = show.getTheatre().getId();
        } else if (show.getScreen() != null && show.getScreen().getTheatre() != null) {
            theatreId = show.getScreen().getTheatre().getId();
        } else {
            throw new RuntimeException("Booking Failed: Theatre information missing for this show.");
        }

        // 2. Booking உருவாக்கல்
        Booking booking = Booking.builder()
                .bookingId("CS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .bookingDateTime(LocalDateTime.now())
                .selectedSeats(seatsList)
                .totalAmount(amount)
                .status(BookingStatus.CONFIRMED)
                .user(user)
                .show(show)
                .theatreId(theatreId)
                .build();

        // 3. Payment உருவாக்கல் (Booking-ஐ இதனுள் இணைத்தல்)
        Payment payment = Payment.builder()
                .transactionId("TXN-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase())
                .totalPaidAmount(amount)
                .paymentStatus(PaymentStatus.SUCCESS)
                .paymentDateTime(LocalDateTime.now()) // நேரத்தைச் சரியாகச் சேர்க்கவும்
                .booking(booking) // இது மிக முக்கியம்
                .build();

        // Booking-ல் Payment-ஐ செட் செய்தல்
        booking.setPayment(payment);

        // 4. Update Seats Stock
        if (show.getAvailableSeats() != null) {
            show.setAvailableSeats(show.getAvailableSeats() - seatsList.size());
            showRepository.save(show);
        }

        // 5. Update Snacks Stock
        if (snackIds != null && quantities != null) {
            for (int i = 0; i < snackIds.size(); i++) {
                Snack snack = snackRepository.findById(snackIds.get(i)).orElseThrow();
                snack.setAvailableStock(snack.getAvailableStock() - quantities.get(i));
                snackRepository.save(snack);
            }
        }

        // 6. Booking-ஐ மட்டும் சேமிக்கவும் (CascadeType.ALL இருப்பதால் Payment தானாகச் சேமிக்கப்படும்)
        return bookingRepository.save(booking);
    }

    public List<String> getBookedSeatsForShow(Long showId) {
        return bookingRepository.findBookedSeatsByShowId(showId);
    }

    public List<Booking> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserIdWithDetails(userId);
    }
}