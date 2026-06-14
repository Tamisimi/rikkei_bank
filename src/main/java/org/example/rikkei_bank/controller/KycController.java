package org.example.rikkei_bank.controller;

import org.example.rikkei_bank.dto.response.ApiResponse;
import org.example.rikkei_bank.entity.KycProfile;
import org.example.rikkei_bank.security.CustomUserDetails;
import org.example.rikkei_bank.service.KycService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/kyc")
public class KycController {

    private final KycService kycService;

    public KycController(KycService kycService) {
        this.kycService = kycService;
    }

    // FR-04: Upload eKYC (Customer)
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<KycProfile>> uploadKyc(
            @RequestParam("frontImage") MultipartFile frontImage,
            Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        KycProfile kyc = kycService.uploadKyc(userId, frontImage);
        return ResponseEntity.ok(ApiResponse.success("Upload hồ sơ eKYC thành công", kyc));
    }

    // FR-09: Phê duyệt eKYC (Staff / Admin)
    @PutMapping("/approve")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<KycProfile>> approveKyc(
            @RequestParam Long userId,
            @RequestParam boolean approved) {

        // Tìm KYC theo userId thay vì kycId
        KycProfile kyc = kycService.approveKycByUserId(userId, approved);

        String message = approved ? "Phê duyệt eKYC thành công" : "Từ chối eKYC thành công";
        return ResponseEntity.ok(ApiResponse.success(message, kyc));
    }
}