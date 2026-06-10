package com.mk.cinesmart.service;


import com.mk.cinesmart.model.Movie;
import com.mk.cinesmart.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ImageService imageService; // Cloudinary அப்லோடுக்காக

    // 1. ADD NEW MOVIE WITH CLOUDINARY POSTER (Super Admin Module)
    public Movie saveMovie(Movie movie, MultipartFile posterFile) throws IOException {
        // போஸ்டர் இமேஜ் பைல் காலியாக இல்லைனா Cloudinary-க்கு அப்லோட் பண்ணி URL-ஐ வாங்குறோம்
        if (posterFile != null && !posterFile.isEmpty()) {
            String uploadedPosterUrl = imageService.uploadImage(posterFile);
            movie.setPosterUrl(uploadedPosterUrl);
        } else {
            // ஒருவேளை இமேஜ் இல்லைனா ஒரு டிஃபால்ட் போஸ்டர் லிங்க்
            movie.setPosterUrl("https://images.cloudinary.com/default-movie-poster.jpg");
        }
        return movieRepository.save(movie);
    }

    // 2. GET ALL MOVIES (For Users Home Page Discovery)
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    // 3. GET MOVIE BY ID
    public Movie getMovieById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with ID: " + id));
    }

    // 4. FILTER MOVIES BY LANGUAGE
    public List<Movie> getMoviesByLanguage(String language) {
        return movieRepository.findByLanguage(language);
    }

    // 5. DELETE MOVIE (Cascade rules automatically handles related shows)
    public void deleteMovie(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new RuntimeException("Movie not found to delete");
        }
        movieRepository.deleteById(id);
    }
}
