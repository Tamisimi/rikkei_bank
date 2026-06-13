package org.example.rikkei_bank.service;

import org.example.rikkei_bank.entity.Account;
import org.example.rikkei_bank.entity.User;
import org.example.rikkei_bank.repository.AccountRepository;
import org.example.rikkei_bank.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PinService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PinService(AccountRepository accountRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void changePin(Long userId, String newPin) {
        Account account = accountRepository.findByUserId(userId).stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // PIN nên hash tương tự password
        account.setTransactionPin(passwordEncoder.encode(newPin));
        accountRepository.save(account);
    }

    @Transactional
    public void forgotPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void forgotPasswordByUsername(String username, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}