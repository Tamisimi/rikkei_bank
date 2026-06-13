package org.example.rikkei_bank.repository;

import org.example.rikkei_bank.entity.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {
    Optional<TokenBlacklist> findByAccessToken(String accessToken);
    void deleteByExpiryAtBefore(LocalDateTime now); // Cleanup cũ
}