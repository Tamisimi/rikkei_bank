package org.example.rikkei_bank.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferRequest {
    @NotNull
    private Long targetAccountId;

    @NotNull
    @Positive
    private BigDecimal amount;

    private String description;
}