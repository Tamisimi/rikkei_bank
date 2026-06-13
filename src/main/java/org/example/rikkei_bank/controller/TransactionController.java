package org.example.rikkei_bank.controller;

import org.example.rikkei_bank.dto.request.TransferRequest;
import org.example.rikkei_bank.dto.response.ApiResponse;
import org.example.rikkei_bank.dto.response.TransactionResponse;
import org.example.rikkei_bank.security.CustomUserDetails;
import org.example.rikkei_bank.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // FR-07: Chuyển tiền
    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<TransactionResponse>> transfer(
            @RequestBody TransferRequest request,
            Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long currentUserId = userDetails.getUserId();

        TransactionResponse response = transactionService.transfer(request, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Chuyển tiền thành công", response));
    }

    // FR-08: Xem sao kê
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getTransactionHistory(
            @RequestParam Long accountId,
            Pageable pageable,
            Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long currentUserId = userDetails.getUserId();

        Page<TransactionResponse> history = transactionService.getTransactionHistory(accountId, currentUserId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Lấy lịch sử giao dịch thành công", history));
    }
}