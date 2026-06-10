package com.mk.cinesmart.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "snacks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Snack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String itemName; // எ.கா: "Large Popcorn", "Cold Coffee"

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer availableStock; // ஸ்டாக் மேனேஜ்மென்ட்

    @Column(nullable = false)
    private String imageUrl; // Cloudinary image URL
}
