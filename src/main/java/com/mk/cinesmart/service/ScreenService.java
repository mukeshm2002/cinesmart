package com.mk.cinesmart.service;

import com.mk.cinesmart.model.Screen;
import com.mk.cinesmart.repository.ScreenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ScreenService {

    @Autowired
    private ScreenRepository screenRepository;

    // 1. ADD NEW THEATER SCREEN
    @Transactional
    public Screen addScreen(Screen screen) {
        // தியேட்டர் விவரம் உள்ளதா என சரிபார்க்கவும்
        if (screen.getTheatre() == null || screen.getTheatre().getId() == null) {
            throw new IllegalArgumentException("Theatre information is required to add a screen!");
        }

        // அதே பெயரில் ஸ்கிரீன் உள்ளதா என சரிபார்க்கவும்
        if (screenRepository.existsByScreenNameAndTheatreId(screen.getScreenName(), screen.getTheatre().getId())) {
            throw new IllegalArgumentException("Screen name already exists in this theatre!");
        }

        // சீட் கணக்கீடு
        int calculatedTotalSeats = screen.getTotalRows() * screen.getSeatsPerRow();
        screen.setTotalSeats(calculatedTotalSeats);

        return screenRepository.save(screen);
    }

    // 2. GET ALL SCREENS
    @Transactional(readOnly = true)
    public List<Screen> getAllScreens() {
        return screenRepository.findAll();
    }

    // 3. GET SCREENS BY THEATRE
    @Transactional(readOnly = true)
    public List<Screen> getScreensByTheatre(Long theatreId) {
        return screenRepository.findByTheatreId(theatreId);
    }

    // 4. GET SCREEN BY ID
    @Transactional(readOnly = true)
    public Screen getScreenById(Long id) {
        return screenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Screen not found with ID: " + id));
    }

    // 5. DELETE SCREEN
    @Transactional
    public void deleteScreen(Long id) {
        if (!screenRepository.existsById(id)) {
            throw new RuntimeException("Screen not found with ID: " + id);
        }
        screenRepository.deleteById(id);
    }
}