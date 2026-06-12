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
    Optional<Booking> findByBookingId(String bookingId);
    List<Booking> findByUserIdOrderByBookingDateTimeDesc(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.show.id = :showId AND b.status = com.mk.cinesmart.model.BookingStatus.RESALE_LISTED")
    List<Booking> findResaleBookingsByShow(@Param("showId") Long showId);

    // புதிய மாற்றம்: தியேட்டர் வாரியான புக்கிங்ஸ் (அட்மின் டேஷ்போர்டுக்காக)
    List<Booking> findByTheatreId(Long theatreId);

    long countByStatus(BookingStatus status);

    @Query("SELECT s FROM Booking b JOIN b.selectedSeats s WHERE b.show.id = :showId AND b.status = 'CONFIRMED'")
    List<String> findBookedSeatsByShowId(@Param("showId") Long showId);

    // BookingRepository.java
    @Query("SELECT b FROM Booking b JOIN FETCH b.show s JOIN FETCH s.movie WHERE b.user.id = :userId ORDER BY b.bookingDateTime DESC")
    List<Booking> findByUserIdWithDetails(@Param("userId") Long userId);
}