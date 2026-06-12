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
    private ImageService imageService;

    // 1. ADD NEW MOVIE (Super Admin can set status as "RELEASED" or "UPCOMING")
    public Movie saveMovie(Movie movie, MultipartFile posterFile) throws IOException {
        if (posterFile != null && !posterFile.isEmpty()) {
            String uploadedPosterUrl = imageService.uploadImage(posterFile);
            movie.setPosterUrl(uploadedPosterUrl);
        } else if (movie.getPosterUrl() == null) {
            movie.setPosterUrl("https://images.cloudinary.com/default-movie-poster.jpg");
        }
        return movieRepository.save(movie);
    }

    // 2. GET ALL MOVIES
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    // 3. NEW: GET MOVIES BY STATUS (Upcoming or Released)
    public List<Movie> getMoviesByStatus(String status) {
        return movieRepository.findByStatus(status);
    }

    // 4. GET MOVIE BY ID
    public Movie getMovieById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with ID: " + id));
    }

    // 5. FILTER MOVIES BY LANGUAGE
    public List<Movie> getMoviesByLanguage(String language) {
        return movieRepository.findByLanguage(language);
    }

    // 6. DELETE MOVIE
    public void deleteMovie(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new RuntimeException("Movie not found to delete");
        }
        movieRepository.deleteById(id);
    }
}