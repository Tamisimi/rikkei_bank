package org.example.rikkei_bank.controller;

import org.example.rikkei_bank.dto.request.RegisterRequest;
import org.example.rikkei_bank.dto.response.ApiResponse;
import org.example.rikkei_bank.dto.response.UserResponseDto;
import org.example.rikkei_bank.entity.User;
import org.example.rikkei_bank.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // FR-04: Đăng ký mở tài khoản + eKYC
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@ModelAttribute RegisterRequest request) {
        // Sử dụng @ModelAttribute để hỗ trợ MultipartFile
        User user = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Đăng ký tài khoản và tạo Account thành công", user));
    }

    // FR-05: Quản lý người dùng
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<Page<UserResponseDto>>> getAllUsers(Pageable pageable) {
        Page<UserResponseDto> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách người dùng thành công", users));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(@PathVariable Long id) {
        UserResponseDto user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin người dùng thành công", user));
    }
}