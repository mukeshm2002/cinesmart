package com.mk.cinesmart.model;

import jakarta.persistence.*;
import lombok.*;
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
    private Integer durationInMinutes; // எ.கா: 150 (2h 30m)

    @Column(nullable = false, length = 30)
    private String language;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String posterUrl; // Cloudinary image URL இங்க தான் சேவ் ஆகும்

    // One movie can have multiple shows across different screens
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Show> shows;
}
