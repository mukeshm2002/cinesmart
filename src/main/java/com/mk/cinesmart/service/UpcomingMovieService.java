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

    // 1. புதிய படத்தை ஆட் செய்ய
    public UpcomingMovie saveUpcomingMovie(UpcomingMovie movie, MultipartFile posterFile) throws IOException {
        // 1. படம் அப்லோட் செய்யப்பட்டால் அதை Cloudinary-ல் அப்லோட் செய்யவும்
        if (posterFile != null && !posterFile.isEmpty()) {
            String uploadedPosterUrl = imageService.uploadImage(posterFile);
            movie.setPosterUrl(uploadedPosterUrl);
        }
        // 2. படம் அப்லோட் செய்யவில்லை மற்றும் ஏற்கனவே URL இல்லை என்றால், default படத்தை வைக்கவும்
        else if (movie.getPosterUrl() == null || movie.getPosterUrl().isEmpty()) {
            movie.setPosterUrl("https://images.cloudinary.com/default-movie-poster.jpg");
        }

        // 3. டேட்டாபேஸில் சேமிக்கவும்
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