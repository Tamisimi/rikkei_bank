package org.example.rikkei_bank.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class UserResponseDto {

    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Boolean isActive;
    private Boolean isKyc;
    private LocalDateTime createdAt;

    // Constructor cho JPQL Projection
    public UserResponseDto(Long id, String username, String fullName, String email,
                           String phoneNumber, Boolean isActive, Boolean isKyc, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.isActive = isActive;
        this.isKyc = isKyc;
        this.createdAt = createdAt;
    }
}