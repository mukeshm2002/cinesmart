package com.mk.cinesmart.repository;


import com.mk.cinesmart.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    // Language வச்சு மூவிகளை பில்டர் பண்ண (எ.கா: Tamil, English)
    List<Movie> findByLanguage(String language);

    // Genre வச்சு மூவிகளை பில்டர் பண்ண (எ.கா: Action, Thriller)
    List<Movie> findByGenre(String genre);
}
