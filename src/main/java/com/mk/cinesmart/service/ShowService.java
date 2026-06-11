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
            // சிம்பிள் டைம் ஓவர்லேப் செக்: புது ஷோவோட ஸ்டார்ட் டைம், ஏற்கனவே இருக்குற ஷோவோட டைம்க்குள் வரக்கூடாது
            // (ரியல்-டைம்ல மூவி டுரேஷனையும் கணக்குல எடுத்துக்கலாம். இங்க பேசிக் செக் பண்ணிருக்கோம்)
            if (existingShow.getStartTime().equals(show.getStartTime())) {
                throw new IllegalStateException("Time Conflict! This screen already has a show scheduled at " + show.getStartTime());
            }
        }

        // புது ஷோ என்பதால் ஆரம்பத்துல தியேட்டர் ஸ்கிரீனோட மொத்த சீட்களும் விற்பனைக்கு கிடைக்கும்
        show.setAvailableSeats(show.getScreen().getTotalSeats());
        return showRepository.save(show);
    }

    // 2. GET ALL SHOWS FOR A MOVIE (User Seat Selection Page-க்கு போகும்போது)
    public List<Show> getUpcomingShowsForMovie(Long movieId) {
        return showRepository.findUpcomingShowsByMovie(movieId, LocalDate.now());
    }

    // 3. GET SHOW BY ID
    public Show getShowById(Long id) {
        return showRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Show not found with ID: " + id));
    }

    // 4. CANCEL AN ENTIRE SHOW (In case of emergency/theatre issue)
    public void deleteShow(Long id) {
        showRepository.deleteById(id);
    }

    public List<Show> getAllShowsForToday() {
        return showRepository.findByShowDate(LocalDate.now());
    }
}
