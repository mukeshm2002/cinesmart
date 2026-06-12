package com.mk.cinesmart.service;

import com.mk.cinesmart.model.Screen;
import com.mk.cinesmart.repository.ScreenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ScreenService {

    @Autowired
    private ScreenRepository screenRepository;

    // 1. ADD NEW THEATER SCREEN
    public Screen addScreen(Screen screen) {
        // ஒரு குறிப்பிட்ட தியேட்டருக்குள் அதே பெயரில் ஸ்கிரீன் இருக்கிறதா என சரிபார்க்கிறோம்
        if (screenRepository.existsByScreenNameAndTheatreId(screen.getScreenName(), screen.getTheatre().getId())) {
            throw new IllegalArgumentException("Screen name already exists in this theatre!");
        }

        // 2D Matrix Grid Logic: மொத்த சீட்களை கணக்கிடுதல்
        int calculatedTotalSeats = screen.getTotalRows() * screen.getSeatsPerRow();
        screen.setTotalSeats(calculatedTotalSeats);

        return screenRepository.save(screen);
    }

    // 2. GET ALL SCREENS (Super Admin-க்கு மட்டும்)
    public List<Screen> getAllScreens() {
        return screenRepository.findAll();
    }

    // 3. GET SCREENS BY THEATRE (Theater Admin-க்கு இதுதான் முக்கியம்)
    public List<Screen> getScreensByTheatre(Long theatreId) {
        return screenRepository.findByTheatreId(theatreId);
    }

    // 4. GET SCREEN BY ID
    public Screen getScreenById(Long id) {
        return screenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Screen not found with ID: " + id));
    }

    // 5. DELETE SCREEN
    public void deleteScreen(Long id) {
        if (!screenRepository.existsById(id)) {
            throw new RuntimeException("Screen not found with ID: " + id);
        }
        screenRepository.deleteById(id);
    }
}