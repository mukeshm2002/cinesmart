package com.mk.cinesmart.repository;

import com.mk.cinesmart.model.Show;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {
    List<Show> findByScreenIdAndShowDate(Long screenId, LocalDate showDate);
    List<Show> findByShowDate(LocalDate showDate);

    // புதிய மாற்றம்: குறிப்பிட்ட தியேட்டரில் உள்ள ஷோக்களை மட்டும் எடுக்க
    List<Show> findByTheatreId(Long theatreId);

    @Query("SELECT s FROM Show s WHERE s.movie.id = :movieId AND s.showDate >= :currentDate ORDER BY s.showDate ASC, s.startTime ASC")
    List<Show> findUpcomingShowsByMovie(@Param("movieId") Long movieId, @Param("currentDate") LocalDate currentDate);

    @Query("SELECT SUM(s.screen.capacity - s.availableSeats) FROM Show s WHERE s.theatre.id = :theatreId")
    Long findTotalTicketsSoldByTheatre(@Param("theatreId") Long theatreId);
}