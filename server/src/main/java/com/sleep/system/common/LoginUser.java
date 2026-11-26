package com.sleep.system.common;

/**
 * 当前登录身份
 *
 * @param id   管理员 ID 或用户 ID
 * @param type admin / user
 */
public record LoginUser(Long id, String type) {

    public boolean isAdmin() {
        return "admin".equals(type);
    }
}
