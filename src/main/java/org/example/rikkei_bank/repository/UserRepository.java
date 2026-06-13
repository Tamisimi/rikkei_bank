package org.example.rikkei_bank.repository;

import org.example.rikkei_bank.dto.response.UserResponseDto;
import org.example.rikkei_bank.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    // JPQL Constructor Projection theo UC-02
    @Query("SELECT new org.example.rikkei_bank.dto.response.UserResponseDto(" +
            "u.id, u.username, u.fullName, u.email, u.phoneNumber, " +
            "u.isActive, u.isKyc, u.createdAt) " +
            "FROM User u")
    Page<UserResponseDto> findAllUserResponseDto(Pageable pageable);

    @Query("SELECT new org.example.rikkei_bank.dto.response.UserResponseDto(" +
            "u.id, u.username, u.fullName, u.email, u.phoneNumber, " +
            "u.isActive, u.isKyc, u.createdAt) " +
            "FROM User u WHERE u.id = :id")
    Optional<UserResponseDto> findUserResponseDtoById(Long id);
}