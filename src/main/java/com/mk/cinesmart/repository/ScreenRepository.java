package com.mk.cinesmart.repository;

import com.mk.cinesmart.model.Screen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScreenRepository extends JpaRepository<Screen, Long> {

    // ஸ்கிரீன் பேரை வைத்து தேட
    Optional<Screen> findByScreenName(String screenName);

    // குறிப்பிட்ட தியேட்டருக்குள் இருக்கும் ஸ்கிரீன்களை மட்டும் எடுக்க (இது மிக முக்கியம்!)
    List<Screen> findByTheatreId(Long theatreId);

    // தியேட்டர் வாரியாக செக் செய்ய
    boolean existsByScreenNameAndTheatreId(String screenName, Long theatreId);
}