package com.sleep.system.common;

/**
 * 登录上下文，基于 ThreadLocal
 */
public final class UserContext {

    private static final ThreadLocal<LoginUser> HOLDER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(LoginUser loginUser) {
        HOLDER.set(loginUser);
    }

    public static LoginUser get() {
        return HOLDER.get();
    }

    public static Long currentId() {
        LoginUser loginUser = HOLDER.get();
        return loginUser == null ? null : loginUser.id();
    }

    public static Long requireAdminId() {
        LoginUser loginUser = HOLDER.get();
        if (loginUser == null || !loginUser.isAdmin()) {
            throw new BusinessException(401, "登录已失效，请重新登录");
        }
        return loginUser.id();
    }

    public static Long requireUserId() {
        LoginUser loginUser = HOLDER.get();
        if (loginUser == null || loginUser.isAdmin()) {
            throw new BusinessException(401, "登录已失效，请重新登录");
        }
        return loginUser.id();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
