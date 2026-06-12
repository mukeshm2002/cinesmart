package com.mk.cinesmart.service;

import com.mk.cinesmart.model.UpcomingMovie;
import com.mk.cinesmart.repository.UpcomingMovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class UpcomingMovieService {

    @Autowired
    private UpcomingMovieRepository upcomingMovieRepository;

    @Autowired
    private ImageService imageService;

    public UpcomingMovie saveUpcomingMovie(UpcomingMovie movie, MultipartFile posterFile) throws IOException {

        // 1. படம் அப்லோட் செய்யப்பட்டுள்ளதா எனச் சரிபார்க்கவும்
        if (posterFile != null && !posterFile.isEmpty()) {
            try {
                String uploadedPosterUrl = imageService.uploadImage(posterFile);
                movie.setPosterUrl(uploadedPosterUrl);
            } catch (Exception e) {
                // இமேஜ் அப்லோட் ஃபெயில் ஆனால், டீஃபால்ட் படம் அல்லது எர்ரரை த்ரோ செய்யவும்
                throw new IOException("Image upload failed: " + e.getMessage());
            }
        }
        // 2. படம் இல்லையென்றால், ஏற்கனவே URL உள்ளதா எனப் பார்க்கவும், இல்லையெனில் Default URL-ஐ செட் செய்யவும்
        else if (movie.getPosterUrl() == null || movie.getPosterUrl().trim().isEmpty()) {
            movie.setPosterUrl("https://images.cloudinary.com/default-movie-poster.jpg");
        }

        // 3. பாதுகாப்பு: மற்ற கட்டாய ஃபீல்டுகள் null ஆக இல்லை என்பதை உறுதி செய்யவும்
        if (movie.getTitle() == null || movie.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Movie title cannot be empty");
        }

        // 4. டேட்டாபேஸில் சேமிக்கவும்
        return upcomingMovieRepository.save(movie);
    }

    // 2. அனைத்து அப்-கமிங் படங்களையும் எடுக்க
    public List<UpcomingMovie> getAllUpcomingMovies() {
        return upcomingMovieRepository.findAllByOrderByReleaseDateAsc();
    }

    // 3. படத்தை நீக்க (தேவைப்பட்டால்)
    public void deleteMovie(Long id) {
        upcomingMovieRepository.deleteById(id);
    }

    // 4. ஒரு குறிப்பிட்ட படத்தை எடுக்க
    public UpcomingMovie getMovieById(Long id) {
        return upcomingMovieRepository.findById(id).orElse(null);
    }
}