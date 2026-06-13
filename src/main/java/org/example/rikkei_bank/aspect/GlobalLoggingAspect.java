package org.example.rikkei_bank.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Aspect
@Component
public class GlobalLoggingAspect {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * FR-11: Log thời gian thực hiện cho TẤT CẢ các method trong Controller
     */
    @Around("execution(* org.example.rikkei_bank.controller.*.*(..))")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        long startTime = System.currentTimeMillis();
        String timestamp = LocalDateTime.now().format(FORMATTER);

        log.info("[AUDIT] [START] {} - {} - {}", timestamp, methodName, getRequestInfo());

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            log.info("[AUDIT] [SUCCESS] {} - {} - Time: {}ms", timestamp, methodName, executionTime);
            return result;

        } catch (Exception ex) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("[AUDIT] [FAILED] {} - {} - Time: {}ms - Error: {}",
                    timestamp, methodName, executionTime, ex.getMessage());
            throw ex;
        }
    }

    /**
     * Log cho Service Layer (Nghiệp vụ)
     */
    @Around("execution(* org.example.rikkei_bank.service.*.*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        long startTime = System.currentTimeMillis();

        log.info("[SERVICE] START - {}", methodName);

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            log.info("[SERVICE] SUCCESS - {} - Time: {}ms", methodName, executionTime);
            return result;
        } catch (Exception ex) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("[SERVICE] FAILED - {} - Time: {}ms - {}", methodName, executionTime, ex.getMessage());
            throw ex;
        }
    }

    private String getRequestInfo() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                return attributes.getRequest().getMethod() + " " + attributes.getRequest().getRequestURI();
            }
        } catch (Exception e) {
            // Ignore
        }
        return "";
    }
}