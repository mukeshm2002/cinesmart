package com.mk.cinesmart.repository;


import com.mk.cinesmart.model.Payment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Booking ID வச்சு அதோட பேமெண்ட் ட்ரான்சாக்ஷனை கண்டுபிடிக்க (Refund ப்ராசஸ் பண்ண முக்கியம்)
    Optional<Payment> findByBookingId(Long bookingId);

    // Gateway transaction ID வச்சு வெரிஃபை பண்ண
    Optional<Payment> findByTransactionId(String transactionId);

    // 📊 Admin Analytics: தியேட்டரோட மொத்த பாக்ஸ் ஆபீஸ் வசூலை கணக்கிட
    @Query("SELECT SUM(p.totalPaidAmount) FROM Payment p WHERE p.paymentStatus = com.cinesmart.model.PaymentStatus.SUCCESS")
    Double calculateTotalRevenue();
}
