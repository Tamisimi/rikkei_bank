package org.example.rikkei_bank.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {
    private Long id;
    private String transactionCode;
    private BigDecimal amount;
    private String description;
    private String type;           // DEBIT hoặc CREDIT
    private String status;
    private LocalDateTime createdAt;
}