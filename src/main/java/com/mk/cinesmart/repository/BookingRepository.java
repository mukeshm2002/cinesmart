package com.mk.cinesmart.repository;


import com.mk.cinesmart.model.Booking;
import com.mk.cinesmart.model.BookingStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // டிக்கெட் கோடை வச்சு புக்கிங் விபரங்களை எடுக்க (எ.கா: QR Code ஸ்கேன் பண்ணும்போது)
    Optional<Booking> findByBookingId(String bookingId);

    // ஒரு குறிப்பிட்ட யூசரோட புக்கிங் ஹிஸ்டரியை காட்ட
    List<Booking> findByUserIdOrderByBookingDateTimeDesc(Long userId);

    // 🔥 P2P Resale Logic: ஒரு குறிப்பிட்ட ஷோவுக்கு ரீசேல் மார்க்கெட்ல விக்க ரெடியா இருக்குற சீட்களை மட்டும் தனியா பிரிக்க
    @Query("SELECT b FROM Booking b WHERE b.show.id = :showId AND b.status = com.cinesmart.model.BookingStatus.RESALE_LISTED")
    List<Booking> findResaleBookingsByShow(@Param("showId") Long showId);

    // தியேட்டர் அட்மின் டேஷ்போர்டுக்காக புக்கிங் ஸ்டேட்டஸ் வச்சு கவுண்ட் எடுக்க
    long countByStatus(BookingStatus status);
}
