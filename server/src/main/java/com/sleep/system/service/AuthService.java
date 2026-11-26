package com.sleep.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.sleep.system.common.BusinessException;
import com.sleep.system.common.JwtUtil;
import com.sleep.system.common.UserContext;
import com.sleep.system.entity.AppUser;
import com.sleep.system.entity.AppUserProfile;
import com.sleep.system.entity.SysAdmin;
import com.sleep.system.entity.SysMenu;
import com.sleep.system.entity.SysRole;
import com.sleep.system.mapper.AppUserMapper;
import com.sleep.system.mapper.AppUserProfileMapper;
import com.sleep.system.mapper.SysAdminMapper;
import com.sleep.system.mapper.SysMenuMapper;
import com.sleep.system.mapper.SysRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 认证服务：管理员登录、用户登录注册、验证码、个人资料
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String DEV_CODE = "123456";

    private final SysAdminMapper adminMapper;

    private final AppUserMapper userMapper;

    private final AppUserProfileMapper profileMapper;

    private final SysRoleMapper roleMapper;

    private final SysMenuMapper menuMapper;

    private final BCryptPasswordEncoder passwordEncoder;

    private final Cache<String, String> codeCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(5))
            .maximumSize(10000)
            .build();

    /**
     * 发送验证码。开发期固定返回 123456，接入短信后改为下发并返回 null
     */
    public String sendCode(String phone) {
        if (phone == null || !phone.matches("^1\\d{10}$")) {
            throw new BusinessException("请输入正确的手机号");
        }
        codeCache.put(phone, DEV_CODE);
        return DEV_CODE;
    }

    public Map<String, Object> adminLogin(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new BusinessException("请输入账号和密码");
        }
        SysAdmin admin = adminMapper.selectOne(new LambdaQueryWrapper<SysAdmin>()
                .eq(SysAdmin::getUsername, username));
        if (admin == null || !passwordEncoder.matches(password, admin.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }
        if (admin.getStatus() != null && admin.getStatus() == 0) {
            throw new BusinessException(403, "该账号已被禁用，请联系超级管理员");
        }
        admin.setLastLoginTime(LocalDateTime.now());
        adminMapper.updateById(admin);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("token", JwtUtil.create(admin.getId(), "admin"));
        result.put("admin", toAdminVO(admin));
        result.put("menus", menusOfAdmin(admin.getId()));
        return result;
    }

    public Map<String, Object> currentAdmin() {
        Long adminId = UserContext.requireAdminId();
        SysAdmin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException(401, "登录已失效，请重新登录");
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("admin", toAdminVO(admin));
        result.put("menus", menusOfAdmin(adminId));
        return result;
    }

    public void resetAdminPassword(String username, String phone, String code, String newPassword) {
        verifyCode(phone, code);
        if (newPassword == null || newPassword.length() < 6) {
            throw new BusinessException("新密码至少 6 位");
        }
        SysAdmin admin = adminMapper.selectOne(new LambdaQueryWrapper<SysAdmin>()
                .eq(SysAdmin::getUsername, username));
        if (admin == null || phone == null || !phone.equals(admin.getPhone())) {
            throw new BusinessException("账号与手机号不匹配");
        }
        admin.setPassword(passwordEncoder.encode(newPassword));
        adminMapper.updateById(admin);
    }

    public Map<String, Object> userLogin(String phone, String code) {
        verifyCode(phone, code);
        AppUser user = userMapper.selectOne(new LambdaQueryWrapper<AppUser>()
                .eq(AppUser::getPhone, phone));
        if (user == null) {
            throw new BusinessException(404, "该手机号尚未注册，请先注册");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(403, "账号已被冻结，请联系客服");
        }
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("token", JwtUtil.create(user.getId(), "user"));
        result.put("user", toUserVO(user));
        return result;
    }

    @Transactional
    public Map<String, Object> userRegister(String phone, String code, String password) {
        verifyCode(phone, code);
        if (password == null || password.length() < 8) {
            throw new BusinessException("密码至少 8 位");
        }
        Long exists = userMapper.selectCount(new LambdaQueryWrapper<AppUser>()
                .eq(AppUser::getPhone, phone));
        if (exists != null && exists > 0) {
            throw new BusinessException("该手机号已注册，请直接登录");
        }
        AppUser user = new AppUser();
        user.setPhone(phone);
        user.setNickname("助眠用户" + phone.substring(phone.length() - 4));
        user.setPassword(passwordEncoder.encode(password));
        user.setGender(0);
        user.setMemberLevel(0);
        user.setStatus(1);
        userMapper.insert(user);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("token", JwtUtil.create(user.getId(), "user"));
        result.put("user", toUserVO(user));
        return result;
    }

    public void resetUserPassword(String phone, String code, String newPassword) {
        verifyCode(phone, code);
        if (newPassword == null || newPassword.length() < 8) {
            throw new BusinessException("新密码至少 8 位");
        }
        AppUser user = userMapper.selectOne(new LambdaQueryWrapper<AppUser>()
                .eq(AppUser::getPhone, phone));
        if (user == null) {
            throw new BusinessException(404, "该手机号尚未注册");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }

    public Map<String, Object> userProfile() {
        AppUser user = userMapper.selectById(UserContext.requireUserId());
        if (user == null) {
            throw new BusinessException(401, "登录已失效，请重新登录");
        }
        Map<String, Object> result = toUserVO(user);
        AppUserProfile profile = profileMapper.selectOne(new LambdaQueryWrapper<AppUserProfile>()
                .eq(AppUserProfile::getUserId, user.getId()));
        result.put("profile", profile);
        return result;
    }

    public void updateUserProfile(String nickname, Integer gender, LocalDate birthDate, String avatar) {
        Long userId = UserContext.requireUserId();
        AppUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(401, "登录已失效，请重新登录");
        }
        if (nickname != null && !nickname.isBlank()) {
            if (nickname.length() > 20) {
                throw new BusinessException("昵称不能超过 20 个字符");
            }
            user.setNickname(nickname);
        }
        if (gender != null) {
            user.setGender(gender);
        }
        if (birthDate != null) {
            user.setBirthDate(birthDate);
        }
        if (avatar != null) {
            user.setAvatar(avatar);
        }
        userMapper.updateById(user);
    }

    @Transactional
    public void saveOnboard(Integer difficulty, Integer latency, List<String> wakePeriods,
                            List<String> preferences, String remindTime) {
        Long userId = UserContext.requireUserId();
        AppUserProfile profile = profileMapper.selectOne(new LambdaQueryWrapper<AppUserProfile>()
                .eq(AppUserProfile::getUserId, userId));
        boolean isNew = profile == null;
        if (isNew) {
            profile = new AppUserProfile();
            profile.setUserId(userId);
        }
        profile.setSleepDifficulty(difficulty);
        profile.setLatencyMinutes(latency);
        profile.setWakePeriods(wakePeriods == null ? null : String.join(",", wakePeriods));
        profile.setPreferences(preferences == null ? null : String.join(",", preferences));
        profile.setRemindTime(remindTime);
        if (isNew) {
            profileMapper.insert(profile);
        } else {
            profileMapper.updateById(profile);
        }
    }

    private void verifyCode(String phone, String code) {
        String cached = codeCache.getIfPresent(phone);
        if (cached == null || !cached.equals(code)) {
            throw new BusinessException("验证码错误或已过期");
        }
        codeCache.invalidate(phone);
    }

    private Map<String, Object> toAdminVO(SysAdmin admin) {
        Map<String, Object> vo = new LinkedHashMap<>();
        vo.put("id", admin.getId());
        vo.put("username", admin.getUsername());
        vo.put("realName", admin.getRealName());
        vo.put("phone", admin.getPhone());
        vo.put("status", admin.getStatus());
        vo.put("lastLoginTime", admin.getLastLoginTime());
        return vo;
    }

    private Map<String, Object> toUserVO(AppUser user) {
        Map<String, Object> vo = new LinkedHashMap<>();
        vo.put("id", user.getId());
        vo.put("phone", user.getPhone());
        vo.put("nickname", user.getNickname());
        vo.put("avatar", user.getAvatar());
        vo.put("gender", user.getGender());
        vo.put("birthDate", user.getBirthDate());
        vo.put("memberLevel", user.getMemberLevel());
        vo.put("memberExpireAt", user.getMemberExpireAt());
        return vo;
    }

    public List<Map<String, Object>> menusOfAdmin(Long adminId) {
        List<SysRole> roles = roleMapper.selectByAdminId(adminId);
        boolean superAdmin = roles.stream().anyMatch(role -> "SUPER_ADMIN".equals(role.getRoleCode()));
        List<SysMenu> menus;
        if (superAdmin || roles.isEmpty()) {
            menus = menuMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                    .eq(SysMenu::getStatus, 1)
                    .orderByAsc(SysMenu::getSort));
        } else {
            Map<Long, SysMenu> merged = new LinkedHashMap<>();
            for (SysRole role : roles) {
                for (SysMenu menu : menuMapper.selectByRoleId(role.getId())) {
                    merged.putIfAbsent(menu.getId(), menu);
                }
            }
            menus = new ArrayList<>(merged.values());
        }
        return buildTree(menus);
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> buildTree(List<SysMenu> menus) {
        Map<Long, Map<String, Object>> nodes = new LinkedHashMap<>();
        for (SysMenu menu : menus) {
            Map<String, Object> node = new LinkedHashMap<>();
            node.put("id", menu.getId());
            node.put("name", menu.getMenuName());
            node.put("path", menu.getPath());
            node.put("icon", menu.getIcon());
            node.put("type", menu.getMenuType());
            node.put("children", new ArrayList<Map<String, Object>>());
            nodes.put(menu.getId(), node);
        }
        List<Map<String, Object>> roots = new ArrayList<>();
        for (SysMenu menu : menus) {
            Map<String, Object> node = nodes.get(menu.getId());
            Map<String, Object> parent = nodes.get(menu.getParentId());
            if (parent == null) {
                roots.add(node);
            } else {
                ((List<Map<String, Object>>) parent.get("children")).add(node);
            }
        }
        return roots;
    }
}
