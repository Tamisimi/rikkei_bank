package org.example.rikkei_bank.security;

import org.example.rikkei_bank.entity.TokenBlacklist;
import org.example.rikkei_bank.repository.TokenBlacklistRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TokenBlacklistService {

    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public TokenBlacklistService(TokenBlacklistRepository tokenBlacklistRepository,
                                 JwtTokenProvider jwtTokenProvider) {
        this.tokenBlacklistRepository = tokenBlacklistRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public void blacklistToken(String token) {
        TokenBlacklist blacklist = TokenBlacklist.builder()
                .accessToken(token)
                .expiryAt(LocalDateTime.now().plusSeconds(300)) // 5 phút
                .build();
        tokenBlacklistRepository.save(blacklist);
    }

    public boolean isBlacklisted(String token) {
        return tokenBlacklistRepository.findByAccessToken(token).isPresent();
    }
}