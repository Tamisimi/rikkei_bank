package org.example.rikkei_bank.dto.response;

import lombok.*;
import org.example.rikkei_bank.entity.enums.Status;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycProfileResponse {
    private Long id;
    private String idNumber;
    private String fullName;
    private Status status;
    private String idCardFrontUrl;
    private LocalDateTime verifiedAt;
    private LocalDateTime createdAt;
}