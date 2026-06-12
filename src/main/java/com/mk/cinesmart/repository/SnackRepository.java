package com.mk.cinesmart.repository;

import com.mk.cinesmart.model.Snack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SnackRepository extends JpaRepository<Snack, Long> {

    // அட்மின் அலர்ட்டுக்காக - குறிப்பிட்ட தியேட்டரில் ஸ்டாக் குறையும் போது
    @Query("SELECT s FROM Snack s WHERE s.availableStock <= :threshold AND s.theatre.id = :theatreId")
    List<Snack> findLowStockSnacksByTheatre(@Param("threshold") Integer threshold, @Param("theatreId") Long theatreId);

    // யூசர் ஆர்டர் ஸ்கிரீனுக்காக - குறிப்பிட்ட தியேட்டரின் ஸ்நாக்ஸ் மட்டும்
    List<Snack> findByTheatreIdOrderByPriceAsc(Long theatreId);

    // ஆக்டிவ் ஸ்நாக்ஸை தியேட்டர் வாரியாக பில்டர் பண்ண
    List<Snack> findByTheatreIdAndAvailableStockGreaterThan(Long theatreId, int minStock);
}