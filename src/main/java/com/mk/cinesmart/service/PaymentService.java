package com.mk.cinesmart.service;

import com.mk.cinesmart.model.Payment;
import com.mk.cinesmart.model.PaymentStatus;
import com.mk.cinesmart.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    // 1. PROCESS & SAVE INITIAL PAYMENT
    public Payment processPayment(Payment payment) {
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setRefundedAmount(0.00);
        return paymentRepository.save(payment);
    }

    // 2. GET PAYMENT DETAILS BY BOOKING ID
    public Payment getPaymentByBookingId(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("No payment history found for this booking ID: " + bookingId));
    }

    // 3. GET TOTAL BOX OFFICE REVENUE (Super Admin Analytics)
    public Double getTotalRevenue() {
        Double total = paymentRepository.calculateTotalRevenue(PaymentStatus.SUCCESS); // Repository-ல் Status-ஐ சேர்க்க வேண்டும்
        return (total != null) ? total : 0.00;
    }

    // 4. NEW: GET REVENUE BY THEATRE (Theatre Admin Dashboard)
    public Double getRevenueByTheatre(Long theatreId) {
        // PaymentStatus.SUCCESS என பாஸ் செய்கிறோம்
        Double total = paymentRepository.calculateRevenueByTheatre(theatreId, PaymentStatus.SUCCESS);
        return (total != null) ? total : 0.00;
    }
}