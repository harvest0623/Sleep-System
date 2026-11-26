# 助眠服务管理系统

基于 Vue3 + SpringBoot（SSM 分层）+ MySQL8 的助眠服务管理系统，包含用户端移动 H5 与管理后台 PC Web。

## 技术栈

| 端 | 技术 |
| --- | --- |
| 用户端 | HTML + Vue3 + Vite + vue-router + Pinia（375 基准，纯 H5） |
| 管理端 | HTML + Vue3 + Vite + vue-router + Pinia + Element Plus（1440 基准） |
| 服务端 | Java 21 + SpringBoot 3 + SpringMVC + MyBatis-Plus（SSM 分层） |
| 数据库 | MySQL 8（utf8mb4） |
| 缓存 | Caffeine 本地缓存（不依赖 Redis） |

## 目录结构

```
Sleep-System/
├─ docs/                  原型设计文档、项目实施路线文档
├─ apps/
│  ├─ user-h5/            用户端移动 H5
│  └─ admin-web/          管理后台 PC Web
├─ server/                SpringBoot 服务端
├─ db/                    建库脚本与初始化数据
├─ package.json           pnpm workspace 根配置
└─ pnpm-workspace.yaml    工作区声明
```

## 端口

| 端口 | 服务 |
| --- | --- |
| 3306 | MySQL |
| 8080 | 后端 API |
| 5173 | 用户端 H5 |
| 5174 | 管理后台 |

前端通过 Vite 代理将 `/api` 转发至 `http://127.0.0.1:8080`。

## 启动方式

前置：JDK 21、Maven 3.9+、Node 20+、pnpm、MySQL 8。

```bash
# 1. 初始化数据库（首次执行）
mysql -u root -p < db/01-schema.sql
mysql -u root -p sleep_system < db/02-seed.sql

# 2. 安装前端依赖（根目录）
pnpm install

# 3. 启动后端
cd server && mvn spring-boot:run

# 4. 启动用户端
pnpm dev:user

# 5. 启动管理端
pnpm dev:admin
```

默认管理员账号：`admin` / `admin123`（首次登录后请修改）。

> 数据库连接配置位于 `server/src/main/resources/application-local.yml`，该文件不纳入版本管理。

## 开发约定

- 所有代码统一使用 **4 个空格** 缩进。
- 接口统一前缀 `/api`，统一响应结构 `{ code, message, data }`。
- 数据库表统一 `utf8mb4`，主键 `BIGINT UNSIGNED` 自增，逻辑删除字段 `deleted`。
- 时间字段统一 `DATETIME`，时区 Asia/Shanghai。
- 页面编号（U-xx / A-xx）与设计文档保持一致，路由命名参考 `docs/原型设计文档.md`。

## 当前进度

| 阶段 | 状态 |
| --- | --- |
| M0 底座与契约 | 进行中（工程骨架、数据库基线、三端联调已完成；接口契约与组件库待补） |
| M1 账号与权限 | 待开始 |
| M2 数据与内容 | 待开始 |
| M3 干预与测评 | 待开始 |
| M4 服务与交易 | 待开始 |
| M5 触达与运营 | 待开始 |