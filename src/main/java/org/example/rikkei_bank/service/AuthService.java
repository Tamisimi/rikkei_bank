package org.example.rikkei_bank.service;

import org.example.rikkei_bank.dto.request.LoginRequest;
import org.example.rikkei_bank.dto.request.RefreshTokenRequest;
import org.example.rikkei_bank.dto.response.AuthResponse;
import org.example.rikkei_bank.entity.RefreshToken;
import org.example.rikkei_bank.entity.User;
import org.example.rikkei_bank.repository.RefreshTokenRepository;
import org.example.rikkei_bank.repository.UserRepository;
import org.example.rikkei_bank.security.JwtTokenProvider;
import org.example.rikkei_bank.security.TokenBlacklistService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenBlacklistService tokenBlacklistService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider,
                       UserRepository userRepository, RefreshTokenRepository refreshTokenRepository,
                       TokenBlacklistService tokenBlacklistService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenBlacklistService = tokenBlacklistService;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshTokenStr = jwtTokenProvider.generateRefreshToken(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenStr)
                .user(user)
                .expiryDate(java.time.Instant.now().plusMillis(86400000))
                .build();
        refreshTokenRepository.save(refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenStr)
                .expiresIn(1800000L)
                .build();
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        Optional<RefreshToken> refreshOpt = refreshTokenRepository.findByToken(request.getRefreshToken());
        if (refreshOpt.isEmpty() || refreshOpt.get().getExpiryDate().isBefore(java.time.Instant.now())) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        User user = refreshOpt.get().getUser();
        String newAccessToken = jwtTokenProvider.generateAccessToken(user);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(request.getRefreshToken())
                .expiresIn(1800000L)
                .build();
    }

    @Transactional
    public void logout(String token) {
        tokenBlacklistService.blacklistToken(token);
    }
}