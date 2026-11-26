package com.sleep.system.controller;

import com.sleep.system.common.LogOperation;
import com.sleep.system.common.Result;
import com.sleep.system.service.AdminService;
import com.sleep.system.service.OpsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 管理端账号、角色、菜单、日志与工作台
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminAccountController {

    private final AdminService adminService;

    private final OpsService opsService;

    @GetMapping("/dashboard/summary")
    public Result<Map<String, Object>> dashboard() {
        return Result.success(opsService.dashboard());
    }

    @GetMapping("/admins")
    public Result<Map<String, Object>> pageAdmins(@RequestParam(defaultValue = "1") long page,
                                                  @RequestParam(defaultValue = "10") long size,
                                                  @RequestParam(required = false) String keyword) {
        return Result.success(adminService.pageAdmins(page, size, keyword));
    }

    @PostMapping("/admins")
    @LogOperation(module = "管理员管理", action = "保存账号")
    public Result<Long> saveAdmin(@RequestBody Map<String, Object> body) {
        return Result.success(adminService.saveAdmin(body));
    }

    @PutMapping("/admins/{id}/status")
    @LogOperation(module = "管理员管理", action = "启用禁用")
    public Result<Void> toggleStatus(@PathVariable Long id, @RequestParam Integer status) {
        adminService.toggleStatus(id, status);
        return Result.success();
    }

    @DeleteMapping("/admins/{id}")
    @LogOperation(module = "管理员管理", action = "删除账号")
    public Result<Void> deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return Result.success();
    }

    @GetMapping("/roles")
    public Result<List<Map<String, Object>>> listRoles() {
        return Result.success(adminService.listRoles());
    }

    @PostMapping("/roles")
    @LogOperation(module = "角色权限", action = "保存角色")
    public Result<Long> saveRole(@RequestBody Map<String, Object> body) {
        return Result.success(adminService.saveRole(body));
    }

    @GetMapping("/roles/{id}/menus")
    public Result<List<Long>> roleMenus(@PathVariable Long id) {
        return Result.success(adminService.roleMenuIds(id));
    }

    @PutMapping("/roles/{id}/menus")
    @LogOperation(module = "角色权限", action = "分配菜单")
    public Result<Void> saveRoleMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        adminService.saveRoleMenus(id, menuIds);
        return Result.success();
    }

    @GetMapping("/menus/tree")
    public Result<List<Map<String, Object>>> menuTree() {
        return Result.success(adminService.menuTree());
    }

    @GetMapping("/logs")
    public Result<Map<String, Object>> pageLogs(@RequestParam(defaultValue = "1") long page,
                                                @RequestParam(defaultValue = "10") long size,
                                                @RequestParam(required = false) String keyword,
                                                @RequestParam(required = false) String module) {
        return Result.success(opsService.pageLogs(page, size, keyword, module));
    }
}
