package com.mk.cinesmart.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String transactionId;

    @Column(nullable = false)
    private Double totalPaidAmount;

    @Column(nullable = false)
    @Builder.Default
    private Double refundedAmount = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus paymentStatus;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime paymentDateTime = LocalDateTime.now(); // ஆட்டோமேட்டிக்காக தற்போதைய நேரம்

    // CascadeType.ALL கொடுத்தால் தான் Booking சேமிக்கப்படும்போது Payment-ம் சரியாக சேமிக்கப்படும்
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
}