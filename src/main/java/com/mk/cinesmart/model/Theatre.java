package com.mk.cinesmart.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "theatres")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Theatre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name; // எ.கா: "PVR Cinemas", "INOX"

    @Column(nullable = false)
    private String location; // தியேட்டர் இருக்கும் இடம்

    @Column(nullable = false, unique = true)
    private String adminEmail; // இந்த ஈமெயில் மூலம் தான் தியேட்டர் அட்மின் லாகின் செய்வார்

    // ஒரு தியேட்டரில் பல ஸ்கிரீன்கள் இருக்கும்
    @OneToMany(mappedBy = "theatre", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Screen> screens;

    // ஒரு தியேட்டரில் பல ஷோக்கள் நடக்கலாம்
    @OneToMany(mappedBy = "theatre", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Show> shows;
}