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
        if (screenRepository.existsByScreenName(screen.getScreenName())) {
            throw new IllegalArgumentException("Screen name already exists!");
        }

        // 2D Matrix Grid Logic: Rows மற்றும் Seats Per Row வச்சு மொத்த சீட்களை கணக்கிடுகிறது
        int calculatedTotalSeats = screen.getTotalRows() * screen.getSeatsPerRow();
        screen.setTotalSeats(calculatedTotalSeats);

        return screenRepository.save(screen);
    }

    // 2. GET ALL SCREENS
    public List<Screen> getAllScreens() {
        return screenRepository.findAll();
    }

    // 3. GET SCREEN BY ID
    public Screen getScreenById(Long id) {
        return screenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Screen not found with ID: " + id));
    }
}
