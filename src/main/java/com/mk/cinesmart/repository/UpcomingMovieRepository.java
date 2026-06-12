package com.mk.cinesmart.repository;


import com.mk.cinesmart.model.UpcomingMovie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UpcomingMovieRepository extends JpaRepository<UpcomingMovie, Long> {

    // ரிலீஸ் தேதியின்படி வரிசைப்படுத்தி எடுக்க (Coming Soon லிஸ்ட்)
    List<UpcomingMovie> findAllByOrderByReleaseDateAsc();
}
