package com.example.otp.model;

import java.time.LocalDateTime;

public record OtpCode(Long id, Long userId, String operationId, String code, OtpStatus status,
                      LocalDateTime createdAt, LocalDateTime expiresAt) {}
