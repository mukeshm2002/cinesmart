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

    public Movie saveMovie(Movie movie,
                           MultipartFile posterFile,
                           List<String> actorNames,
                           List<MultipartFile> actorFiles) throws IOException {

        // 1. இது அப்டேட்டா அல்லது புதிய மூவியா என்று பார்க்க ID-ஐச் சரிபார்க்கவும்
        Movie targetMovie = movie;
        if (movie.getId() != null) {
            targetMovie = movieRepository.findById(movie.getId())
                    .orElseThrow(() -> new RuntimeException("Movie not found"));

            // புதிய மதிப்புகளை அப்டேட் செய்கிறோம்
            targetMovie.setTitle(movie.getTitle());
            targetMovie.setGenre(movie.getGenre());
            targetMovie.setLanguage(movie.getLanguage());
            targetMovie.setStatus(movie.getStatus());
            targetMovie.setReleaseDate(movie.getReleaseDate());
            targetMovie.setDurationInMinutes(movie.getDurationInMinutes());
            targetMovie.setDescription(movie.getDescription());
        }

        // 2. Poster upload (புதியது வந்தால் மட்டும் மாற்றவும்)
        if (posterFile != null && !posterFile.isEmpty()) {
            targetMovie.setPosterUrl(imageService.uploadImage(posterFile));
        }

        // 3. Actors and Actor Images Upload
        if (actorNames != null && !actorNames.isEmpty()) {
            targetMovie.setActorNames(actorNames);

            // புதிய ஆக்டர் இமேஜ்கள் வந்தால் மட்டும் அப்டேட் செய்ய
            if (actorFiles != null && !actorFiles.isEmpty()) {
                List<String> actorImageUrls = new ArrayList<>();
                for (MultipartFile file : actorFiles) {
                    if (file != null && !file.isEmpty()) {
                        actorImageUrls.add(imageService.uploadImage(file));
                    }
                }
                targetMovie.setActorImages(actorImageUrls);
            }
        }

        return movieRepository.save(targetMovie);
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