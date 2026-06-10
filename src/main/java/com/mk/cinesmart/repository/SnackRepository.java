package com.mk.cinesmart.repository;

import com.mk.cinesmart.model.Snack;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SnackRepository extends JpaRepository<Snack, Long> {

    // ஸ்டாக் காலியான ஸ்நாக்ஸை மட்டும் அட்மினுக்கு அலர்ட் பண்ணி காட்ட (Custom JPQL Query)
    @Query("SELECT s FROM Snack s WHERE s.availableStock <= :threshold")
    List<Snack> findLowStockSnacks(@Param("threshold") Integer threshold);

    // விலை கம்மியா இருக்கிற ஸ்நாக்ஸ்ல இருந்து அதிகமா இருக்கிற வரைக்கும் வரிசைப்படுத்த
    List<Snack> findAllByOrderByPriceAsc();

    List<Snack> findByAvailableStockGreaterThan(int minStock);
}
