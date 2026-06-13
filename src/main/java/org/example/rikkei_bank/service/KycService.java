package org.example.rikkei_bank.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
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

        if (frontImage.isEmpty()) {
            throw new RuntimeException("File upload is empty");
        }

        try {
            // Upload lên Cloudinary
            Map uploadResult = cloudinary.uploader().upload(frontImage.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "rikkei_bank/kyc",
                            "public_id", "kyc_" + userId + "_" + System.currentTimeMillis()
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

            return kyc;

        } catch (IOException e) {
            throw new RuntimeException("Upload file to Cloudinary failed: " + e.getMessage());
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