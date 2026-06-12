package com.mk.cinesmart.service;


import com.mk.cinesmart.model.Feedback;
import com.mk.cinesmart.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    // 1. ADD NEW USER FEEDBACK / REVIEW
    public Feedback addFeedback(Feedback feedback) {
        feedback.setCreatedAt(LocalDateTime.now()); // ரிவியூ போடுற கரண்ட் டைமை செட் பண்றோம்
        return feedbackRepository.save(feedback);
    }

    // 2. GET ALL REVIEWS FOR A SPECIFIC MOVIE (To show on Movie Detail Page)
    public List<Feedback> getFeedbacksByMovie(Long movieId) {
        return feedbackRepository.findByMovieIdOrderByCreatedAtDesc(movieId);
    }

    // 3. GET AVERAGE RATING OF A MOVIE (To show Stars on Home Page)
    public Double getAverageMovieRating(Long movieId) {
        Double avgRating = feedbackRepository.getAverageRatingByMovie(movieId);
        // ஒருவேளை படத்துக்கு இன்னும் யாரும் ரேட்டிங் தரலனா, Default-ஆ 0.00-ன்னு ரிட்டர்ன் பண்ணும்
        return (avgRating != null) ? avgRating : 0.0;
    }
    public List<Feedback> getFeedbackByMovie(Long movieId) {
        return feedbackRepository.findByMovieId(movieId);
    }

    public void saveFeedback(Feedback feedback) {
        feedbackRepository.save(feedback);
    }
}
