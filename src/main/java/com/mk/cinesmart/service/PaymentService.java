package com.mk.cinesmart.service;


import com.mk.cinesmart.model.Payment;
import com.mk.cinesmart.model.PaymentStatus;
import com.mk.cinesmart.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    // 1. PROCESS & SAVE INITIAL PAYMENT
    public Payment processPayment(Payment payment) {
        payment.setPaymentStatus(PaymentStatus.SUCCESS); // இப்போதைக்கு ஸ்ட்ரெயிட்டா சக்சஸ் பண்றோம் (Gateway-ஐ அப்புறம் லிங்க் பண்ணலாம்)
        payment.setRefundedAmount(0.00); // ஆரம்பத்துல ரீஃபண்ட் எதுவும் இருக்காது
        return paymentRepository.save(payment);
    }

    // 2. GET PAYMENT DETAILS BY BOOKING ID
    public Payment getPaymentByBookingId(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("No payment history found for this booking ID: " + bookingId));
    }

    // 3. GET TOTAL BOX OFFICE REVENUE (Admin Analytics)
    public Double getTotalRevenue() {
        Double total = paymentRepository.calculateTotalRevenue();
        return (total != null) ? total : 0.00;
    }
}
