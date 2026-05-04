package com.example.otp.controller;

import com.example.otp.model.User;
import com.example.otp.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);
    private final AdminService adminService;

    public AdminController(AdminService adminService) { this.adminService = adminService; }

    public record ConfigRequest(int codeLength, int ttlSeconds) {}
    public record UserResponse(Long id, String login, String role) {}

    @GetMapping("/config")
    public Object getConfig() {
        log.info("GET /api/admin/config");
        return adminService.getConfig();
    }

    @PutMapping("/config")
    public String updateConfig(@RequestBody ConfigRequest request) {
        log.info("PUT /api/admin/config codeLength={} ttl={}", request.codeLength(), request.ttlSeconds());
        adminService.updateConfig(request.codeLength(), request.ttlSeconds());
        return "updated";
    }

    @GetMapping("/users")
    public List<UserResponse> users() {
        log.info("GET /api/admin/users");
        List<User> users = adminService.users();
        return users.stream().map(u -> new UserResponse(u.id(), u.login(), u.role().name())).toList();
    }

    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable long id) {
        log.info("DELETE /api/admin/users/{}", id);
        adminService.deleteUser(id);
        return "deleted";
    }
}
