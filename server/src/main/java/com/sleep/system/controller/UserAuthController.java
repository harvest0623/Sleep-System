package com.sleep.system.controller;

import com.sleep.system.common.Result;
import com.sleep.system.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 用户端认证：U-02 登录、U-03 注册、U-04 找回密码、U-05 首次建档、U-06 协议授权
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserAuthController {

    private final AuthService authService;

    public record CaptchaBody(String phone) {
    }

    public record LoginBody(String phone, String code) {
    }

    public record RegisterBody(String phone, String code, String password) {
    }

    public record ResetBody(String phone, String code, String newPassword) {
    }

    public record ProfileBody(String nickname, Integer gender, LocalDate birthDate, String avatar) {
    }

    public record OnboardBody(Integer difficulty, Integer latency, List<String> wakePeriods,
                              List<String> preferences, String remindTime) {
    }

    @PostMapping("/auth/captcha")
    public Result<Map<String, Object>> captcha(@RequestBody CaptchaBody body) {
        String code = authService.sendCode(body.phone());
        return Result.success(Map.of("phone", body.phone(), "devCode", code));
    }

    @PostMapping("/auth/login")
    public Result<Map<String, Object>> login(@RequestBody LoginBody body) {
        return Result.success(authService.userLogin(body.phone(), body.code()));
    }

    @PostMapping("/auth/register")
    public Result<Map<String, Object>> register(@RequestBody RegisterBody body) {
        return Result.success(authService.userRegister(body.phone(), body.code(), body.password()));
    }

    @PostMapping("/auth/reset-password")
    public Result<Void> resetPassword(@RequestBody ResetBody body) {
        authService.resetUserPassword(body.phone(), body.code(), body.newPassword());
        return Result.success();
    }

    @GetMapping("/profile")
    public Result<Map<String, Object>> profile() {
        return Result.success(authService.userProfile());
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody ProfileBody body) {
        authService.updateUserProfile(body.nickname(), body.gender(), body.birthDate(), body.avatar());
        return Result.success();
    }

    @PostMapping("/profile/onboard")
    public Result<Void> onboard(@RequestBody OnboardBody body) {
        authService.saveOnboard(body.difficulty(), body.latency(), body.wakePeriods(),
                body.preferences(), body.remindTime());
        return Result.success();
    }
}
