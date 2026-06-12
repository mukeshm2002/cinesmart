package com.mk.cinesmart.service;

import org.springframework.stereotype.Service;
import com.mk.cinesmart.model.Booking;

@Service
public class TicketService {
    public String generateTicketImage(Booking booking) {
        // இங்குதான் Java Graphics2D பயன்படுத்தி டிக்கெட் இமேஜ் உருவாக்கப்படும்
        // இப்போதைக்கு தற்காலிகமாக ஒரு path ரிட்டர்ன் செய்யவும்
        return "src/main/resources/tickets/ticket_" + booking.getId() + ".png";
    }
}