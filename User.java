package com.example.otp.controller;

import com.example.otp.security.AuthUser;
import com.example.otp.service.OtpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final OtpService otpService;

    public UserController(OtpService otpService) { this.otpService = otpService; }

    public record GenerateRequest(String operationId, String channel, String destination) {}
    public record ValidateRequest(String operationId, String code) {}
    public record ValidateResponse(boolean valid) {}

    @PostMapping("/otp/generate")
    public String generate(Authentication authentication, @RequestBody GenerateRequest request) {
        AuthUser user = (AuthUser) authentication.getPrincipal();
        log.info("POST /api/user/otp/generate user={} operation={} channel={}", user.login(), request.operationId(), request.channel());
        otpService.generateAndSend(user.id(), request.operationId(), request.channel(), request.destination());
        return "sent";
    }

    @PostMapping("/otp/validate")
    public ValidateResponse validate(Authentication authentication, @RequestBody ValidateRequest request) {
        AuthUser user = (AuthUser) authentication.getPrincipal();
        log.info("POST /api/user/otp/validate user={} operation={}", user.login(), request.operationId());
        return new ValidateResponse(otpService.validate(user.id(), request.operationId(), request.code()));
    }
}
