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
    private String transactionId; // Gateway-ல இருந்து வர்ற ID (எ.கா: TXN-9834721)

    @Column(nullable = false)
    private Double totalPaidAmount; // யூசர் பே பண்ண மொத்த காசு

    @Column(nullable = false)
    @Builder.Default // Lombok இருந்தால் இதை சேர்க்கவும்
    private Double refundedAmount = 0.0;// ரீஃபண்ட் பண்ண காசு (0.00 / 50% / 100%)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus paymentStatus;

    @Column(nullable = false)
    private LocalDateTime paymentDateTime;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
}
