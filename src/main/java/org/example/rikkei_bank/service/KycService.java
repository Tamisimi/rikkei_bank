package org.example.rikkei_bank.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.example.rikkei_bank.dto.response.KycProfileResponse;
import org.example.rikkei_bank.entity.KycProfile;
import org.example.rikkei_bank.entity.enums.Status;
import org.example.rikkei_bank.entity.User;
import org.example.rikkei_bank.repository.KycProfileRepository;
import org.example.rikkei_bank.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
public class KycService {

    private final KycProfileRepository kycProfileRepository;
    private final UserRepository userRepository;
    private final Cloudinary cloudinary;

    public KycService(KycProfileRepository kycProfileRepository,
                      UserRepository userRepository,
                      Cloudinary cloudinary) {
        this.kycProfileRepository = kycProfileRepository;
        this.userRepository = userRepository;
        this.cloudinary = cloudinary;
    }

    @Transactional
    public KycProfile uploadKyc(Long userId, MultipartFile frontImage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Nếu không có file → bỏ qua upload (không bắt buộc)
        if (frontImage == null || frontImage.isEmpty()) {
            log.warn("No image uploaded for userId: {}", userId);
            return null; // hoặc tạo KycProfile mà không có ảnh
        }

        try {
            Map uploadResult = cloudinary.uploader().upload(frontImage.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "rikkei_bank/kyc",
                            "public_id", "kyc_" + userId + "_" + System.currentTimeMillis(),
                            "resource_type", "image"
                    ));

            String imageUrl = (String) uploadResult.get("secure_url");

            KycProfile kyc = KycProfile.builder()
                    .user(user)
                    .idNumber("ID" + System.currentTimeMillis())
                    .fullName(user.getFullName())
                    .status(Status.PENDING)
                    .idCardFrontUrl(imageUrl)
                    .build();

            kyc = kycProfileRepository.save(kyc);
            user.setKycProfile(kyc);
            userRepository.save(user);

            log.info("✅ Upload eKYC thành công cho userId: {}", userId);
            return kyc;

        } catch (Exception e) {
            log.error("❌ Cloudinary upload failed for userId {}: {}", userId, e.getMessage());
            throw new RuntimeException("Upload ảnh eKYC thất bại: " + e.getMessage(), e);
        }
    }

    // Approve KYC giữ nguyên
    @Transactional
    public KycProfile approveKyc(Long kycId, boolean approved) {
        KycProfile kyc = kycProfileRepository.findById(kycId)
                .orElseThrow(() -> new RuntimeException("KYC not found"));

        kyc.setStatus(approved ? Status.CONFIRM : Status.REJECT);
        if (approved) {
            kyc.setVerifiedAt(LocalDateTime.now());
            kyc.getUser().setIsKyc(true);
        }

        return kycProfileRepository.save(kyc);
    }
}