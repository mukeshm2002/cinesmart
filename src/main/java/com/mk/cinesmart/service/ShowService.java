package com.mk.cinesmart.service;

import com.mk.cinesmart.model.Show;
import com.mk.cinesmart.repository.ShowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class ShowService {

    @Autowired
    private ShowRepository showRepository;

    // 1. CREATE NEW SHOW (Theater Admin Module)
    public Show createShow(Show show) {
        // Validation: தியேட்டர் அட்மின் செட் பண்ணும் ஸ்கிரீன்ல அதே தேதியில இருக்குற மத்த ஷோக்களை செக் பண்றோம்
        List<Show> existingShowsOnDay = showRepository.findByScreenIdAndShowDate(
                show.getScreen().getId(),
                show.getShowDate()
        );

        for (Show existingShow : existingShowsOnDay) {
            if (existingShow.getStartTime().equals(show.getStartTime())) {
                throw new IllegalStateException("Time Conflict! This screen already has a show scheduled at " + show.getStartTime());
            }
        }

        show.setAvailableSeats(show.getScreen().getTotalSeats());
        return showRepository.save(show);
    }

    // 2. NEW: GET SHOWS BY THEATRE (Theater Admin Dashboard-க்காக)
    public List<Show> getShowsByTheatre(Long theatreId) {
        return showRepository.findByTheatreId(theatreId);
    }

    // 3. GET ALL SHOWS FOR A MOVIE
    public List<Show> getUpcomingShowsForMovie(Long movieId) {
        return showRepository.findUpcomingShowsByMovie(movieId, LocalDate.now());
    }

    // 4. GET SHOW BY ID
    public Show getShowById(Long id) {
        return showRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Show not found with ID: " + id));
    }

    // 5. CANCEL AN ENTIRE SHOW
    public void deleteShow(Long id) {
        showRepository.deleteById(id);
    }

    public List<Show> getAllShowsForToday() {
        return showRepository.findByShowDate(LocalDate.now());
    }

    public Integer getTotalTicketsSoldByTheatre(Long theatreId) {
        Integer sold = showRepository.findTotalTicketsSoldByTheatre(theatreId);
        return (sold != null) ? sold : 0; // நல் (null) வந்தால் 0 என்று திருப்பி அனுப்பவும்
    }
}