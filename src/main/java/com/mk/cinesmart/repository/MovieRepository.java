package com.mk.cinesmart.repository;

import com.mk.cinesmart.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByLanguage(String language);
    List<Movie> findByGenre(String genre);
    // புதிய மாற்றம்: ஸ்டேட்டஸ் படி பிரிக்க
    List<Movie> findByStatus(String status);
}