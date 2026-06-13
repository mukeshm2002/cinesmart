package com.mk.cinesmart.service;

import com.mk.cinesmart.model.Movie;
import com.mk.cinesmart.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ImageService imageService;

    // ADD NEW MOVIE with Actor Details
    public Movie saveMovie(Movie movie,
                           MultipartFile posterFile,
                           List<String> actorNames,
                           List<MultipartFile> actorFiles) throws IOException {

        // 1. Poster upload
        if (posterFile != null && !posterFile.isEmpty()) {
            movie.setPosterUrl(imageService.uploadImage(posterFile));
        } else if (movie.getPosterUrl() == null) {
            movie.setPosterUrl("https://images.cloudinary.com/default-movie-poster.jpg");
        }

        // 2. Actors and Actor Images Upload (Cloudinary)
        if (actorNames != null && !actorNames.isEmpty() && actorFiles != null) {
            List<String> actorImageUrls = new ArrayList<>();
            for (MultipartFile file : actorFiles) {
                if (file != null && !file.isEmpty()) {
                    actorImageUrls.add(imageService.uploadImage(file));
                }
            }
            movie.setActorNames(actorNames);
            movie.setActorImages(actorImageUrls);
        }

        return movieRepository.save(movie);
    }

    // மற்ற மெத்தடுகள் அப்படியே இருக்கட்டும்...
    public List<Movie> getAllMovies() { return movieRepository.findAll(); }

    public List<Movie> getMoviesByStatus(String status) { return movieRepository.findByStatus(status); }

    public Movie getMovieById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with ID: " + id));
    }

    public List<Movie> getMoviesByLanguage(String language) { return movieRepository.findByLanguage(language); }

    public void deleteMovie(Long id) {
        if (!movieRepository.existsById(id)) throw new RuntimeException("Movie not found");
        movieRepository.deleteById(id);
    }
}