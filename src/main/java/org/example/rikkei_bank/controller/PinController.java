package org.example.rikkei_bank.controller;

import org.example.rikkei_bank.dto.response.ApiResponse;
import org.example.rikkei_bank.security.CustomUserDetails;
import org.example.rikkei_bank.service.PinService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pin")
public class PinController {

    private final PinService pinService;

    public PinController(PinService pinService) {
        this.pinService = pinService;
    }

    // Đổi mã PIN (Yêu cầu đã đăng nhập)
    @PutMapping("/change")
    public ResponseEntity<ApiResponse<String>> changePin(
            @RequestParam String newPin,
            Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        pinService.changePin(userDetails.getUserId(), newPin);

        return ResponseEntity.ok(ApiResponse.success("Đổi mã PIN thành công", null));
    }

    // Quên mật khẩu (Public - Không cần token)
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @RequestParam String username,
            @RequestParam String newPassword) {

        // TODO: Trong thực tế nên thêm OTP verification
        // Hiện tại cho phép reset trực tiếp theo yêu cầu SRS
        pinService.forgotPasswordByUsername(username, newPassword);

        return ResponseEntity.ok(ApiResponse.success("Đặt lại mật khẩu thành công. Vui lòng đăng nhập lại.", null));
    }
}