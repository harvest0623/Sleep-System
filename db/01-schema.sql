-- 助眠服务管理系统 · 基础表结构
-- 字符集 utf8mb4，引擎 InnoDB，时间统一 Asia/Shanghai

CREATE DATABASE IF NOT EXISTS sleep_system
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE sleep_system;

-- 后台管理员
CREATE TABLE IF NOT EXISTS sys_admin (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    username        VARCHAR(50)     NOT NULL COMMENT '登录账号',
    password        VARCHAR(100)    NOT NULL COMMENT 'BCrypt 加密密码',
    real_name       VARCHAR(50)              DEFAULT NULL COMMENT '姓名',
    phone           VARCHAR(20)              DEFAULT NULL COMMENT '手机号',
    status          TINYINT         NOT NULL DEFAULT 1 COMMENT '1 启用 0 禁用',
    last_login_time DATETIME                 DEFAULT NULL COMMENT '最后登录时间',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         NOT NULL DEFAULT 0 COMMENT '0 正常 1 已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_admin_username (username)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '后台管理员';

-- 角色
CREATE TABLE IF NOT EXISTS sys_role (
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    role_name   VARCHAR(50)     NOT NULL COMMENT '角色名称',
    role_code   VARCHAR(50)     NOT NULL COMMENT '角色编码',
    data_scope  TINYINT         NOT NULL DEFAULT 1 COMMENT '1 全部 2 本部门 3 仅本人',
    remark      VARCHAR(255)             DEFAULT NULL COMMENT '备注',
    status      TINYINT         NOT NULL DEFAULT 1 COMMENT '1 启用 0 禁用',
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted     TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (role_code)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '后台角色';

-- 管理员与角色关联
CREATE TABLE IF NOT EXISTS sys_admin_role (
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    admin_id    BIGINT UNSIGNED NOT NULL COMMENT '管理员 ID',
    role_id     BIGINT UNSIGNED NOT NULL COMMENT '角色 ID',
    PRIMARY KEY (id),
    UNIQUE KEY uk_admin_role (admin_id, role_id),
    KEY idx_admin_role_admin (admin_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '管理员角色关联';

-- 菜单与权限
CREATE TABLE IF NOT EXISTS sys_menu (
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    parent_id   BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '父级 ID，0 为一级',
    menu_name   VARCHAR(50)     NOT NULL COMMENT '菜单名称',
    path        VARCHAR(120)             DEFAULT NULL COMMENT '路由路径',
    component   VARCHAR(120)             DEFAULT NULL COMMENT '前端组件标识',
    icon        VARCHAR(50)              DEFAULT NULL COMMENT '图标',
    menu_type   TINYINT         NOT NULL DEFAULT 1 COMMENT '1 目录 2 菜单 3 按钮',
    sort        INT             NOT NULL DEFAULT 0 COMMENT '排序',
    status      TINYINT         NOT NULL DEFAULT 1 COMMENT '1 启用 0 禁用',
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted     TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_menu_parent (parent_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '菜单与权限';

-- 角色与菜单关联
CREATE TABLE IF NOT EXISTS sys_role_menu (
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    role_id     BIGINT UNSIGNED NOT NULL COMMENT '角色 ID',
    menu_id     BIGINT UNSIGNED NOT NULL COMMENT '菜单 ID',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_menu (role_id, menu_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '角色菜单关联';

-- C 端用户
CREATE TABLE IF NOT EXISTS app_user (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    phone           VARCHAR(20)     NOT NULL COMMENT '手机号',
    nickname        VARCHAR(50)              DEFAULT NULL COMMENT '昵称',
    avatar          VARCHAR(255)             DEFAULT NULL COMMENT '头像地址',
    gender          TINYINT                  DEFAULT 0 COMMENT '0 未知 1 男 2 女',
    birth_date      DATE                     DEFAULT NULL COMMENT '生日',
    password        VARCHAR(100)             DEFAULT NULL COMMENT '登录密码（可空，验证码登录）',
    member_level    TINYINT         NOT NULL DEFAULT 0 COMMENT '会员等级',
    member_expire_at DATETIME                DEFAULT NULL COMMENT '会员到期时间',
    status          TINYINT         NOT NULL DEFAULT 1 COMMENT '1 正常 0 冻结',
    last_login_time DATETIME                 DEFAULT NULL COMMENT '最后登录时间',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_phone (phone)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = 'C 端用户';

-- 操作日志
CREATE TABLE IF NOT EXISTS sys_operation_log (
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    admin_id    BIGINT UNSIGNED          DEFAULT NULL COMMENT '操作人 ID',
    admin_name  VARCHAR(50)              DEFAULT NULL COMMENT '操作人账号',
    module      VARCHAR(50)              DEFAULT NULL COMMENT '所属模块',
    action      VARCHAR(50)              DEFAULT NULL COMMENT '操作类型',
    target      VARCHAR(255)             DEFAULT NULL COMMENT '操作对象',
    detail      TEXT                     DEFAULT NULL COMMENT '操作详情',
    ip          VARCHAR(50)              DEFAULT NULL COMMENT '来源 IP',
    result      TINYINT         NOT NULL DEFAULT 1 COMMENT '1 成功 0 失败',
    cost_ms     INT                      DEFAULT NULL COMMENT '耗时（毫秒）',
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (id),
    KEY idx_log_admin (admin_id),
    KEY idx_log_created (created_at)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '后台操作日志';
