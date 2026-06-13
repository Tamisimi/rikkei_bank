package org.example.rikkei_bank.controller;

import org.example.rikkei_bank.dto.response.ApiResponse;
import org.example.rikkei_bank.security.CustomUserDetails;
import org.example.rikkei_bank.service.PinService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pin")
public class PinController {

    private final PinService pinService;

    public PinController(PinService pinService) {
        this.pinService = pinService;
    }

    // Đổi mã PIN
    @PutMapping("/change")
    public ResponseEntity<ApiResponse<String>> changePin(
            @RequestParam String newPin,
            Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        pinService.changePin(userDetails.getUserId(), newPin);

        return ResponseEntity.ok(ApiResponse.success("Đổi mã PIN thành công", null));
    }

    // Quên mật khẩu (cần xác thực thêm OTP trong thực tế)
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @RequestParam String newPassword,
            Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        pinService.forgotPassword(userDetails.getUserId(), newPassword);

        return ResponseEntity.ok(ApiResponse.success("Đổi mật khẩu thành công", null));
    }
}