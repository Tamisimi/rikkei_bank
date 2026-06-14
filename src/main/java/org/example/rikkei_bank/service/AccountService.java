package org.example.rikkei_bank.service;

import org.example.rikkei_bank.dto.response.AccountResponse;
import org.example.rikkei_bank.entity.Account;
import org.example.rikkei_bank.entity.User;
import org.example.rikkei_bank.repository.AccountRepository;
import org.example.rikkei_bank.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    /**
     * Tạo tài khoản mặc định cho User (gọi sau khi đăng ký)
     */
    @Transactional
    public Account createAccountForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        String accountNumber = "RB" + System.currentTimeMillis();

        Account account = Account.builder()
                .accountNumber(accountNumber)
                .balance(BigDecimal.ZERO)
                .currency("VND")
                .user(user)                    // ← Phải gán đúng user
                .active(true)
                .build();

        return accountRepository.save(account);
    }

    /**
     * FR-06: Vấn tin số dư (Kiểm tra quyền sở hữu)
     */
    public AccountResponse getBalance(Long accountId, Long userId) {
        Account account = accountRepository.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new RuntimeException("Account not found or you don't have permission to view this account"));

        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance() != null ? account.getBalance() : BigDecimal.ZERO)
                .currency(account.getCurrency())
                .active(account.getActive())
                .createdAt(account.getCreatedAt())
                .build();
    }

    /**
     * Lấy danh sách tài khoản của user hiện tại
     */
    public List<Account> getAccountsByUserId(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    /**
     * Tìm tài khoản mặc định (tài khoản đầu tiên) của user
     */
    public Account findDefaultAccount(Long userId) {
        return accountRepository.findFirstByUserIdOrderByIdAsc(userId)
                .orElseThrow(() -> new RuntimeException("User has no account"));
    }
}