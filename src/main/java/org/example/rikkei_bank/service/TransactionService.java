package org.example.rikkei_bank.service;

import org.example.rikkei_bank.dto.request.TransferRequest;
import org.example.rikkei_bank.dto.response.TransactionResponse;
import org.example.rikkei_bank.entity.Account;
import org.example.rikkei_bank.entity.Transaction;
import org.example.rikkei_bank.exception.InsufficientBalanceException;
import org.example.rikkei_bank.repository.AccountRepository;
import org.example.rikkei_bank.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    public TransactionService(AccountRepository accountRepository,
                              TransactionRepository transactionRepository,
                              AccountService accountService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
    }

    /**
     * FR-07: Chuyển tiền nội bộ
     * Nhận userId từ Controller → tìm tài khoản nguồn của user
     */
    @Transactional(rollbackFor = Exception.class)
    public TransactionResponse transfer(TransferRequest request, Long userId) {
        Account fromAccount = accountService.findDefaultAccount(userId);

        Account toAccount = accountRepository.findById(request.getTargetAccountId())
                .orElseThrow(() -> new RuntimeException("Target account not found"));

        BigDecimal currentBalance = fromAccount.getBalance() != null ? fromAccount.getBalance() : BigDecimal.ZERO;

        if (currentBalance.compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Số dư không đủ để thực hiện giao dịch");
        }

        fromAccount.setBalance(currentBalance.subtract(request.getAmount()));
        toAccount.setBalance((toAccount.getBalance() != null ? toAccount.getBalance() : BigDecimal.ZERO)
                .add(request.getAmount()));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        Transaction transaction = Transaction.builder()
                .transactionCode(UUID.randomUUID().toString())
                .amount(request.getAmount())
                .description(request.getDescription() != null ? request.getDescription() : "Chuyển tiền nội bộ")
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .build();

        transaction = transactionRepository.save(transaction);

        return TransactionResponse.builder()
                .id(transaction.getId())
                .transactionCode(transaction.getTransactionCode())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .type("DEBIT")
                .createdAt(transaction.getCreatedAt())
                .build();
    }

    /**
     * FR-08: Xem sao kê lịch sử giao dịch
     * Kiểm tra quyền sở hữu account
     */
    public Page<TransactionResponse> getTransactionHistory(Long accountId, Long userId, Pageable pageable) {
        // Kiểm tra account có thuộc về user không
        accountRepository.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new RuntimeException("Account not found or access denied"));

        Page<Transaction> transactions = transactionRepository
                .findTransactionsByAccountId(accountId, pageable);

        return transactions.map(t -> {
            String type = (t.getFromAccount() != null && t.getFromAccount().getId().equals(accountId))
                    ? "DEBIT" : "CREDIT";

            return TransactionResponse.builder()
                    .id(t.getId())
                    .transactionCode(t.getTransactionCode())
                    .amount(t.getAmount())
                    .description(t.getDescription())
                    .type(type)
                    .createdAt(t.getCreatedAt())
                    .build();
        });
    }
}