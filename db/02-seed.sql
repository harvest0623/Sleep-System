-- 助眠服务管理系统 · 初始化数据
-- 管理员初始密码：admin123（BCrypt，首次登录后请修改）

USE sleep_system;

-- 角色
INSERT INTO sys_role (id, role_name, role_code, data_scope, remark, status)
VALUES (1, '超级管理员', 'SUPER_ADMIN', 1, '拥有全部菜单与数据权限', 1),
       (2, '运营专员', 'OPERATOR', 1, '负责内容、用户与营销运营', 1),
       (3, '客服专员', 'SERVICE', 3, '负责预约、售后与消息处理', 1)
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name);

-- 管理员
INSERT INTO sys_admin (id, username, password, real_name, phone, status)
VALUES (1, 'admin', '$2b$10$4BTjH3QlcxaCzjF3VxhC8.ogrKJ8zFRJnr0CaBpxa/3wkUMNT2IHm', '系统管理员', NULL, 1)
ON DUPLICATE KEY UPDATE password = VALUES(password);

INSERT INTO sys_admin_role (admin_id, role_id)
VALUES (1, 1)
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

-- 一级菜单
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, icon, menu_type, sort, status)
VALUES (1, 0, '工作台', '/dashboard', 'dashboard/index', 'Odometer', 2, 1, 1),
       (2, 0, '数据看板', '/dashboard-stat', NULL, 'DataLine', 1, 2, 1),
       (3, 0, '用户中心', '/user', NULL, 'User', 1, 3, 1),
       (4, 0, '内容中心', '/content', NULL, 'Headset', 1, 4, 1),
       (5, 0, '计划与激励', '/plan', NULL, 'Calendar', 1, 5, 1),
       (6, 0, '测评中心', '/assessment', NULL, 'Document', 1, 6, 1),
       (7, 0, '助眠师中心', '/therapist', NULL, 'Avatar', 1, 7, 1),
       (8, 0, '预约咨询', '/appointment', NULL, 'ChatDotRound', 1, 8, 1),
       (9, 0, '交易中心', '/trade', NULL, 'ShoppingCart', 1, 9, 1),
       (10, 0, '营销中心', '/marketing', NULL, 'Present', 1, 10, 1),
       (11, 0, '消息中心', '/message', NULL, 'Bell', 1, 11, 1),
       (12, 0, '社区运营', '/community', NULL, 'ChatLineSquare', 1, 12, 1),
       (13, 0, '系统设置', '/system', NULL, 'Setting', 1, 13, 1)
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 超级管理员拥有全部菜单
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu
ON DUPLICATE KEY UPDATE menu_id = VALUES(menu_id);
