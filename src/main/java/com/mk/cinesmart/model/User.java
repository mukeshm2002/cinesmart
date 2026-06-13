package com.mk.cinesmart.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 15)
    private String mobileNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UserRole role;

    // --- புதிய மாற்றங்கள் ---

    // தியேட்டர் அட்மின்கள் எந்த தியேட்டரைச் சேர்ந்தவர்கள் என்று அறிய
    // தியேட்டர் மாடல் உருவாக்கிய பிறகு இங்கே @ManyToOne ரிலேஷன்ஷிப் கொடுக்கலாம்
    private String theatreName;

    // அக்கவுண்ட் வெரிஃபிகேஷன் (OTP-க்காக)
    @Builder.Default
    private boolean isVerified = false;

    // -----------------------

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "theatre_id")
    private Theatre theatre;

    private String otp;
    private boolean verified = false;

    public void setVerified(boolean verified) {
        this.verified = verified; }
}