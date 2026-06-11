package com.mk.cinesmart.repository;

import com.mk.cinesmart.model.Snack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SnackRepository extends JpaRepository<Snack, Long> {

    // 💡 அட்மின் அலர்ட்டுக்காக
    @Query("SELECT s FROM Snack s WHERE s.availableStock <= :threshold")
    List<Snack> findLowStockSnacks(@Param("threshold") Integer threshold);

    // 💡 யூசர் ஆர்டர் ஸ்கிரீனுக்காக
    List<Snack> findAllByOrderByPriceAsc();

    // 💡 ஆக்டிவ் ஸ்நாக்ஸை பில்டர் பண்ண
    List<Snack> findByAvailableStockGreaterThan(int minStock);
}