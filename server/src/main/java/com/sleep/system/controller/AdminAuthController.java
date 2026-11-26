package com.sleep.system.controller;

import com.sleep.system.common.Result;
import com.sleep.system.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 管理端认证：A-01 登录、A-02 找回密码
 */
@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AuthService authService;

    public record LoginBody(String username, String password) {
    }

    public record ResetBody(String username, String phone, String code, String newPassword) {
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginBody body) {
        return Result.success(authService.adminLogin(body.username(), body.password()));
    }

    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@RequestBody ResetBody body) {
        authService.resetAdminPassword(body.username(), body.phone(), body.code(), body.newPassword());
        return Result.success();
    }

    @GetMapping("/info")
    public Result<Map<String, Object>> info() {
        return Result.success(authService.currentAdmin());
    }
}
