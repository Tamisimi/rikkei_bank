package org.example.rikkei_bank.controller;

import org.example.rikkei_bank.dto.response.AccountResponse;
import org.example.rikkei_bank.dto.response.ApiResponse;
import org.example.rikkei_bank.security.CustomUserDetails;
import org.example.rikkei_bank.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // Tạo tài khoản cho user hiện tại
    @PostMapping
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        // accountService.createAccountForUser(userId);  // Uncomment sau khi cập nhật service
        return ResponseEntity.ok(ApiResponse.success("Tạo tài khoản thành công", null));
    }

    // FR-06: Vấn tin số dư tài khoản
    @GetMapping("/{accountId}/balance")
    public ResponseEntity<ApiResponse<AccountResponse>> getBalance(
            @PathVariable Long accountId,
            Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long currentUserId = userDetails.getUserId();

        AccountResponse response = accountService.getBalance(accountId, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Lấy số dư thành công", response));
    }
}