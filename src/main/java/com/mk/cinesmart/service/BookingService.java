package com.mk.cinesmart.service;


import com.mk.cinesmart.model.*;
import com.mk.cinesmart.repository.BookingRepository;
import com.mk.cinesmart.repository.PaymentRepository;
import com.mk.cinesmart.repository.ShowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ShowRepository showRepository;

    // 🔥 1. INSTANT CANCELLATION LOGIC (50% REFUND)
    @Transactional
    public Booking cancelBookingInstantly(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only CONFIRMED tickets can be cancelled.");
        }

        // Status-ஐ CANCELLED ஆக மாற்றுகிறோம்
        booking.setStatus(BookingStatus.CANCELLED);

        // 50% ரீஃபண்ட் கணக்கீடு
        double refundAmount = booking.getTotalAmount() * 0.50;

        Payment payment = booking.getPayment();
        if (payment != null) {
            payment.setRefundedAmount(refundAmount);
            payment.setPaymentStatus(PaymentStatus.REFUNDED_PARTIAL);
            paymentRepository.save(payment);
        }

        // தியேட்டர் சீட்களை மீண்டும் பொது விற்பனைக்கு ரிலீஸ் செய்ய ஷோவோட கவுன்ட்டை கூட்டுகிறோம்
        Show show = booking.getShow();
        show.setAvailableSeats(show.getAvailableSeats() + booking.getSelectedSeats().size());
        showRepository.save(show);

        return bookingRepository.save(booking);
    }

    // 🔥 2. LIST TICKET FOR P2P RESALE (MARKETPLACE)
    @Transactional
    public Booking listForResale(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // ஷோ ஆரம்பிக்க 2 மணி நேரத்துக்கு முன்னாடி தான் லிஸ்ட் பண்ண முடியும் (Time Gate Window)
        if (LocalDateTime.now().isAfter(booking.getShow().getShowDate().atTime(booking.getShow().getStartTime()).minusHours(2))) {
            throw new IllegalStateException("Resale window closed! Cannot list tickets less than 2 hours before the show.");
        }

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only CONFIRMED tickets can be listed for resale.");
        }

        // சீட்களை ரிலீஸ் பண்ணாமல், ஸ்டேட்டஸை மட்டும் RESALE_LISTED (Yellow Seat) என மாற்றுகிறோம்
        booking.setStatus(BookingStatus.RESALE_LISTED);
        return bookingRepository.save(booking);
    }

    // 🔥 3. BUY RESALED TICKET (ATOMIC TRANSACTION WITH SERIALIZABLE ISOLATION)
    // Concurrency மற்றும் Double Booking-ஐ தடுக்க இந்த Isolation Level ரொம்ப முக்கியம்!
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Booking purchaseResaledTicket(Long originalBookingId, User buyer) {
        Booking originalBooking = bookingRepository.findById(originalBookingId)
                .orElseThrow(() -> new RuntimeException("Original booking record not found"));

        if (originalBooking.getStatus() != BookingStatus.RESALE_LISTED) {
            throw new IllegalStateException("This ticket is no longer available for resale.");
        }

        // STEP A: பழைய ஓனருக்கு 100% ரீஃபண்ட் (ஆனால் தியேட்டர் 10% கமிஷன் எடுத்துக்கொள்ளும்)
        originalBooking.setStatus(BookingStatus.RESALE_SOLD);
        Payment originalPayment = originalBooking.getPayment();
        if (originalPayment != null) {
            double originalPaid = originalBooking.getTotalAmount();
            // 10% கமிஷன் போக மீதி 90% பழைய யூசருக்கு ரீஃபண்ட் ஆகும்
            originalPayment.setRefundedAmount(originalPaid * 0.90);
            originalPayment.setPaymentStatus(PaymentStatus.REFUNDED_FULL);
            paymentRepository.save(originalPayment);
        }
        bookingRepository.save(originalBooking);

        // STEP B: புதிய பையருக்கு (Buyer) புதிய புக்கிங் கன்பர்ம் செய்தல்
        Booking newBooking = Booking.builder()
                .bookingId("CS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .bookingDateTime(LocalDateTime.now())
                .selectedSeats(originalBooking.getSelectedSeats()) // அதே சீட் நம்பர்கள்
                .totalAmount(originalBooking.getTotalAmount()) // அதே பிரைஸ் (No Price Scalping)
                .status(BookingStatus.CONFIRMED)
                .user(buyer)
                .show(originalBooking.getShow())
                .build();

        Booking savedNewBooking = bookingRepository.save(newBooking);

        // STEP C: புதிய பையருக்கான பேமெண்ட் ரெக்கார்டு கிரியேட் செய்தல்
        Payment newPayment = Payment.builder()
                .transactionId("TXN-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase())
                .totalPaidAmount(originalBooking.getTotalAmount())
                .refundedAmount(0.0)
                .paymentStatus(PaymentStatus.SUCCESS)
                .paymentDateTime(LocalDateTime.now())
                .booking(savedNewBooking)
                .build();

        paymentRepository.save(newPayment);

        return savedNewBooking;
    }
    // =========================================================================
// 🔍 MISSING METHODS FOR CONTROLLER INTEGRATION
// =========================================================================

    // 1. ஒரு குறிப்பிட்ட ஷோவுக்கு ரீசேல் மார்க்கெட்ல இருக்கிற புக்கிங்ஸை எடுக்க
    public List<Booking> getResaleBookingsByShow(Long showId) {
        return bookingRepository.findResaleBookingsByShow(showId);
    }

    // 2. லாகின் பண்ணியிருக்கிற யூசரோட ஒட்டுமொத்த புக்கிங் ஹிஸ்டரியை காட்ட
    public List<Booking> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserIdOrderByBookingDateTimeDesc(userId);
    }
    @org.springframework.transaction.annotation.Transactional
    public void saveNewBooking(Booking booking, Payment payment) {
        // 1. புக்கிங் மற்றும் பேமெண்ட் விபரங்களை டேட்டாபேஸில் சேவ் செய்கிறோம்
        bookingRepository.save(booking);

        // 2. தியேட்டர் ஸ்கிரீனில் சீட் கவுன்ட்டைக் குறைக்கிறோம்
        Show show = booking.getShow();
        int seatsBookedCount = booking.getSelectedSeats().size();

        if (show.getAvailableSeats() < seatsBookedCount) {
            throw new IllegalStateException("Sorry, requested seats are no longer available!");
        }

        show.setAvailableSeats(show.getAvailableSeats() - seatsBookedCount);
        // இங்க showRepository-ஐ ஆட்டோவொயர் பண்ணி சேவ் பண்ணிக்கலாம்
        // showRepository.save(show);
    }
    // 1. ரீசேல் சீட்களை மட்டும் எடுக்க
    public List<String> getResaleSeatsOnly(Long showId) {
        // 'this' யூஸ் பண்ணி அதே கிளாஸ்ல இருக்குற மெத்தடை கூப்பிடுறோம்
        return this.getResaleBookingsByShow(showId).stream()
                .flatMap(resale -> resale.getSelectedSeats().stream())
                .collect(java.util.stream.Collectors.toList());
    }

    // 2. புக் ஆன சீட்கள் (ரீசேல்-ஐ தவிர்த்து)
    public List<String> getBookedSeatsForShow(Long showId) {
        List<Booking> bookings = bookingRepository.findByShowId(showId);
        List<String> resaleSeats = this.getResaleSeatsOnly(showId);

        return bookings.stream()
                .flatMap(b -> b.getSelectedSeats().stream())
                .filter(seat -> !resaleSeats.contains(seat)) // ரீசேல் சீட்டை நீக்குறோம்
                .collect(java.util.stream.Collectors.toList());
    }
}
