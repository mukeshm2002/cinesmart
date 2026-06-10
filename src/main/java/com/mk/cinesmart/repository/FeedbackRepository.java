package com.mk.cinesmart.repository;

import com.mk.cinesmart.model.Feedback;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    // ஒரு குறிப்பிட்ட மூவிக்கான எல்லா ஃபீட்பேக்குகளையும் லேட்டஸ்ட் தேதியின்படி வரிசைப்படுத்தி எடுக்க
    List<Feedback> findByMovieIdOrderByCreatedAtDesc(Long movieId);

    // 📊 Movie Detail Page Engine: ஒரு படத்தோட ஆவரேஜ் ரேட்டிங்கை (Stars) கணக்கிட கஸ்டம் குவெரி
    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.movie.id = :movieId")
    Double getAverageRatingByMovie(@Param("movieId") Long movieId);
}
