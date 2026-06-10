package com.mk.cinesmart.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "screens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Screen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String screenName; // எ.கா: "Screen 1", "IMAX"

    @Column(nullable = false)
    private Integer totalSeats; // எ.கா: 120

    @Column(nullable = false)
    private Integer totalRows; // 2D Grid Layout-க்காக (எ.கா: 10 Rows)

    @Column(nullable = false)
    private Integer seatsPerRow; // 2D Grid Layout-க்காக (எ.கா: 12 Seats per Row)

    // One screen can host multiple shows
    @OneToMany(mappedBy = "screen", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Show> shows;
}
