package com.mk.cinesmart.repository;


import com.mk.cinesmart.model.Screen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ScreenRepository extends JpaRepository<Screen, Long> {

    // ஸ்கிரீன் பேரை வச்சு தேட (எ.கா: "Screen 1", "IMAX" ஏற்கனவே இருக்கானு பார்க்க)
    Optional<Screen> findByScreenName(String screenName);

    // குறிப்பிட்ட ஸ்கிரீன் நேம்ல டேட்டா இருக்கானு செக் பண்ண
    boolean existsByScreenName(String screenName);
}
