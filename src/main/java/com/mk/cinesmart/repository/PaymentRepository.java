package com.mk.cinesmart.repository;

import com.mk.cinesmart.model.Payment;
import com.mk.cinesmart.model.PaymentStatus; // இதையும் சேர்த்துக்கொள்ளுங்கள்
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Booking ID வைத்து பேமெண்ட் தேடல்
    Optional<Payment> findByBookingId(Long bookingId);

    // Transaction ID வைத்து வெரிஃபிகேஷன்
    Optional<Payment> findByTransactionId(String transactionId);

    // மொத்த வசூல் (Super Admin-க்காக)
    @Query("SELECT SUM(p.totalPaidAmount) FROM Payment p WHERE p.paymentStatus = :status")
    Double calculateTotalRevenue(@Param("status") PaymentStatus status);

    // தியேட்டர் வாரியான வருவாய் (Theatre Admin-க்காக)
    @Query("SELECT SUM(p.totalPaidAmount) FROM Payment p JOIN p.booking b WHERE b.theatreId = :theatreId AND p.paymentStatus = :status")
    Double calculateRevenueByTheatre(@Param("theatreId") Long theatreId, @Param("status") PaymentStatus status);
}