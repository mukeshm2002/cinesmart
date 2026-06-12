package com.mk.cinesmart.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "upcoming_movies")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpcomingMovie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "poster_url")
    private String posterUrl;

    @Column(name = "release_date", nullable = false)
    private LocalDate releaseDate;

    @Column(name = "trailer_url")
    private String trailerUrl; // படத்திற்கான ட்ரைலர் லிங்க்

    // ஒருவேளை படம் ரிலீஸ் ஆகிவிட்டது என்றால் இதை மாற்றிக்கொள்ளலாம்
    @Builder.Default
    private boolean isReleased = false;

    // வசதிக்காக: இந்த படம் எப்போது சிஸ்டத்தில் சேர்க்கப்பட்டது
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
