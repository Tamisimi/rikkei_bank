package org.example.rikkei_bank.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.example.rikkei_bank.dto.request.TransferRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Aspect
@Component
public class TransactionLoggingAspect {

    /**
     * UC-04: Ghi log sau khi chuyển tiền thành công (@AfterReturning)
     */
    @AfterReturning(
            pointcut = "execution(* org.example.rikkei_bank.service.TransactionService.transfer(..))",
            returning = "result"
    )
    public void logSuccessfulTransfer(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        TransferRequest request = (TransferRequest) args[0];
        Long userId = (Long) args[1];

        String logMessage = String.format(
                "[AUDIT] UserId=%d transferred Amount=%s to TargetAccountId=%d - SUCCESS",
                userId,
                request.getAmount(),
                request.getTargetAccountId()
        );

        log.info(logMessage);
        // Có thể lưu vào bảng AuditLog sau này
    }

    /**
     * Ghi log khi chuyển tiền thất bại (@AfterThrowing)
     */
    @AfterThrowing(
            pointcut = "execution(* org.example.rikkei_bank.service.TransactionService.transfer(..))",
            throwing = "ex"
    )
    public void logFailedTransfer(JoinPoint joinPoint, Exception ex) {
        Object[] args = joinPoint.getArgs();
        TransferRequest request = (TransferRequest) args[0];
        Long userId = (Long) args[1];

        String logMessage = String.format(
                "[AUDIT] UserId=%d FAILED to transfer Amount=%s to TargetAccountId=%d - Reason: %s",
                userId,
                request.getAmount(),
                request.getTargetAccountId(),
                ex.getMessage()
        );

        log.error(logMessage);
    }

    /**
     * Log chung cho mọi thay đổi số dư (có thể mở rộng sau)
     */
    @AfterReturning(
            pointcut = "execution(* org.example.rikkei_bank.service.AccountService.*(..)) && " +
                    "(execution(* *Balance(..)) || execution(* *createAccountForUser(..)))"
    )
    public void logBalanceChange(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        log.info("[AUDIT] Balance operation executed: {}", methodName);
    }
}