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
    private String bookingId; // எ.கா: CS-2026-XYZ89 (Unique Ticket Code)

    @Column(nullable = false)
    private LocalDateTime bookingDateTime;

    @ElementCollection // PostgreSQL-ல் தனி டேபிளாக சீட்களை லிஸ்ட் பண்ணும் (எ.கா: A1, A2)
    @CollectionTable(name = "booking_seats", joinColumns = @JoinColumn(name = "booking_id"))
    @Column(name = "seat_number")
    private List<String> selectedSeats;

    @Column(nullable = false)
    private Double totalAmount; // 18% GST + Convenience fee எல்லாம் சேர்த்த மொத்த தொகை

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BookingStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    // இந்த புக்கிங்கான பேமெண்ட் விபரம்
    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Payment payment;
}
