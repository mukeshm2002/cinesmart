package com.mk.cinesmart.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String bookingId;

    @Column(nullable = false)
    private LocalDateTime bookingDateTime;

    @ElementCollection
    @CollectionTable(name = "booking_seats", joinColumns = @JoinColumn(name = "booking_id"))
    @Column(name = "seat_number")
    private List<String> selectedSeats;

    @Column(nullable = false)
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BookingStatus status;

    // --- புதிய மாற்றங்கள் ---

    // தியேட்டர் அட்மின் டேஷ்போர்டுக்காக தியேட்டர் ஐடியை இங்கே சேர்க்கலாம்
    // அல்லது ஷோ வழியாகவும் எடுக்கலாம், ஆனால் பெர்ஃபார்மென்ஸுக்காக இதை சேர்க்கிறது நல்லது
    private Long theatreId;

    // ரீசேல் நடக்கும்போது புதிய உரிமையாளர் விவரம் தேவைப்படும்
    private String resoldToUserEmail;

    // -----------------------

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Payment payment;
}