package org.example.rikkei_bank.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.rikkei_bank.entity.enums.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "kyc_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String idNumber;

    private String fullName;
    private LocalDate dob;
    private String sex;
    private String address;

    @Column(name = "id_card_front_url")
    private String idCardFrontUrl;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    private LocalDateTime verifiedAt;
    private LocalDateTime createdAt;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}