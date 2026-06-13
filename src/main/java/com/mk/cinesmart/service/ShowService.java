package com.mk.cinesmart.service;

import com.mk.cinesmart.model.Show;
import com.mk.cinesmart.repository.ShowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ShowService {

    @Autowired
    private ShowRepository showRepository;

    // 1. CREATE NEW SHOW (Theater Admin Module)
    @Transactional
    public Show createShow(Show show) {
        // தியேட்டர் மற்றும் ஸ்கிரீன் மேப்பிங் சரியாக இருக்கிறதா என சரிபார்க்கவும்
        if (show.getScreen() == null || show.getScreen().getTheatre() == null) {
            throw new IllegalArgumentException("Invalid Show Configuration: Screen and Theatre must be associated!");
        }

        // தியேட்டர் ரெஃபரன்ஸை உறுதிப்படுத்துதல்
        show.setTheatre(show.getScreen().getTheatre());

        // அதே ஸ்கிரீன், அதே தேதியில் முரண்பாடான நேரத்தில ஷோ உள்ளதா என சரிபார்க்கவும்
        List<Show> existingShowsOnDay = showRepository.findByScreenIdAndShowDate(
                show.getScreen().getId(),
                show.getShowDate()
        );

        for (Show existingShow : existingShowsOnDay) {
            if (existingShow.getStartTime().equals(show.getStartTime())) {
                throw new IllegalStateException("Time Conflict! This screen already has a show scheduled at " + show.getStartTime());
            }
        }

        // சீட் எண்ணிக்கையை செட் செய்தல்
        show.setAvailableSeats(show.getScreen().getTotalSeats());
        return showRepository.save(show);
    }

    // 2. GET SHOWS BY THEATRE
    @Transactional(readOnly = true)
    public List<Show> getShowsByTheatre(Long theatreId) {
        return showRepository.findByTheatreId(theatreId);
    }

    // 3. GET ALL SHOWS FOR A MOVIE
    @Transactional(readOnly = true)
    public List<Show> getUpcomingShowsForMovie(Long movieId) {
        return showRepository.findUpcomingShowsByMovie(movieId, LocalDate.now());
    }

    // 4. GET SHOW BY ID
    @Transactional(readOnly = true)
    public Show getShowById(Long id) {
        return showRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Show not found with ID: " + id));
    }

    // 5. CANCEL AN ENTIRE SHOW
    @Transactional
    public void deleteShow(Long id) {
        if (!showRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete: Show not found with ID: " + id);
        }
        showRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Show> getAllShowsForToday() {
        return showRepository.findByShowDate(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public Integer getTotalTicketsSoldByTheatre(Long theatreId) {
        Long totalSold = showRepository.findTotalTicketsSoldByTheatre(theatreId);
        return (totalSold != null) ? totalSold.intValue() : 0;
    }
}