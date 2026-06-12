package com.mk.cinesmart.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 50)
    private String genre;

    @Column(nullable = false)
    private Integer durationInMinutes;

    @Column(nullable = false, length = 30)
    private String language;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String posterUrl;

    // --- புதிய மாற்றங்கள் ---

    // படம் இப்போ ஓடுதா இல்ல வரப்போகுதா என்பதை முடிவு செய்ய
    @Column(nullable = false, length = 20)
    private String status; // "RELEASED" or "UPCOMING"

    // படம் ரிலீஸ் ஆகும் தேதி
    private LocalDate releaseDate;

    // -----------------------

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Show> shows;
}