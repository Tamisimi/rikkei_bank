package org.example.rikkei_bank.repository;

import org.example.rikkei_bank.entity.KycProfile;
import org.example.rikkei_bank.entity.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KycProfileRepository extends JpaRepository<KycProfile, Long> {
    Optional<KycProfile> findByUserId(Long userId);
    Optional<KycProfile> findByIdNumber(String idNumber);
    long countByStatus(Status status);
}