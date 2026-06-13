package org.example.rikkei_bank.service;

import org.example.rikkei_bank.dto.request.RegisterRequest;
import org.example.rikkei_bank.dto.response.UserResponseDto;
import org.example.rikkei_bank.entity.Role;
import org.example.rikkei_bank.entity.User;
import org.example.rikkei_bank.repository.RoleRepository;
import org.example.rikkei_bank.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountService accountService;
    private final KycService kycService;

    public UserService(UserRepository userRepository, RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder, AccountService accountService,
                       KycService kycService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.accountService = accountService;
        this.kycService = kycService;
    }

    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .isActive(true)
                .isKyc(false)
                .build();

        Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Role CUSTOMER not found"));
        user.setRoles(Set.of(customerRole));

        user = userRepository.save(user);

        // Tự động tạo Account sau khi đăng ký
        accountService.createAccountForUser(user.getId());

        // Nếu có file eKYC → upload
        if (request.getFrontImage() != null && !request.getFrontImage().isEmpty()) {
            kycService.uploadKyc(user.getId(), request.getFrontImage());
        }

        return user;
    }

    // Các phương thức cũ giữ nguyên...
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        return userRepository.findAllUserResponseDto(pageable);
    }

    public UserResponseDto getUserById(Long id) {
        return userRepository.findUserResponseDtoById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}