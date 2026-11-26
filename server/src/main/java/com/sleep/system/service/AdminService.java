package com.sleep.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sleep.system.common.BusinessException;
import com.sleep.system.common.UserContext;
import com.sleep.system.entity.SysAdmin;
import com.sleep.system.entity.SysMenu;
import com.sleep.system.entity.SysRole;
import com.sleep.system.mapper.SysAdminMapper;
import com.sleep.system.mapper.SysAdminRoleMapper;
import com.sleep.system.mapper.SysMenuMapper;
import com.sleep.system.mapper.SysRoleMapper;
import com.sleep.system.mapper.SysRoleMenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 后台账号、角色与菜单权限
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final SysAdminMapper adminMapper;

    private final SysRoleMapper roleMapper;

    private final SysMenuMapper menuMapper;

    private final SysAdminRoleMapper adminRoleMapper;

    private final SysRoleMenuMapper roleMenuMapper;

    private final BCryptPasswordEncoder passwordEncoder;

    public Map<String, Object> pageAdmins(long page, long size, String keyword) {
        LambdaQueryWrapper<SysAdmin> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(condition -> condition
                    .like(SysAdmin::getUsername, keyword)
                    .or().like(SysAdmin::getRealName, keyword)
                    .or().like(SysAdmin::getPhone, keyword));
        }
        wrapper.orderByAsc(SysAdmin::getId);
        Page<SysAdmin> result = adminMapper.selectPage(new Page<>(page, size), wrapper);
        List<Map<String, Object>> records = new ArrayList<>();
        for (SysAdmin admin : result.getRecords()) {
            records.add(toAdminVO(admin));
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("records", records);
        data.put("total", result.getTotal());
        return data;
    }

    @Transactional
    public Long saveAdmin(Map<String, Object> body) {
        Long id = toLong(body.get("id"));
        String username = (String) body.get("username");
        String password = (String) body.get("password");
        String realName = (String) body.get("realName");
        String phone = (String) body.get("phone");
        Integer status = body.get("status") == null ? 1 : toInt(body.get("status"));
        List<Long> roleIds = toLongList(body.get("roleIds"));

        if (username == null || username.isBlank()) {
            throw new BusinessException("请输入登录账号");
        }
        if (id == null) {
            if (password == null || password.length() < 6) {
                throw new BusinessException("初始密码至少 6 位");
            }
            Long exists = adminMapper.selectCount(new LambdaQueryWrapper<SysAdmin>()
                    .eq(SysAdmin::getUsername, username));
            if (exists != null && exists > 0) {
                throw new BusinessException("该账号已存在");
            }
            SysAdmin admin = new SysAdmin();
            admin.setUsername(username);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRealName(realName);
            admin.setPhone(phone);
            admin.setStatus(status);
            adminMapper.insert(admin);
            id = admin.getId();
        } else {
            SysAdmin admin = adminMapper.selectById(id);
            if (admin == null) {
                throw new BusinessException("账号不存在");
            }
            admin.setRealName(realName);
            admin.setPhone(phone);
            admin.setStatus(status);
            if (password != null && !password.isBlank()) {
                admin.setPassword(passwordEncoder.encode(password));
            }
            adminMapper.updateById(admin);
        }
        adminRoleMapper.deleteByAdminId(id);
        if (roleIds != null) {
            for (Long roleId : roleIds) {
                adminRoleMapper.insert(id, roleId);
            }
        }
        return id;
    }

    public void toggleStatus(Long id, Integer status) {
        Long currentId = UserContext.requireAdminId();
        if (id.equals(currentId) && status != null && status == 0) {
            throw new BusinessException("不能禁用当前登录账号");
        }
        SysAdmin admin = adminMapper.selectById(id);
        if (admin == null) {
            throw new BusinessException("账号不存在");
        }
        admin.setStatus(status);
        adminMapper.updateById(admin);
    }

    @Transactional
    public void deleteAdmin(Long id) {
        Long currentId = UserContext.requireAdminId();
        if (id.equals(currentId)) {
            throw new BusinessException("不能删除当前登录账号");
        }
        adminRoleMapper.deleteByAdminId(id);
        adminMapper.deleteById(id);
    }

    public List<Map<String, Object>> listRoles() {
        List<SysRole> roles = roleMapper.selectList(new LambdaQueryWrapper<SysRole>()
                .orderByAsc(SysRole::getId));
        List<Map<String, Object>> list = new ArrayList<>();
        for (SysRole role : roles) {
            Map<String, Object> vo = new LinkedHashMap<>();
            vo.put("id", role.getId());
            vo.put("roleName", role.getRoleName());
            vo.put("roleCode", role.getRoleCode());
            vo.put("dataScope", role.getDataScope());
            vo.put("remark", role.getRemark());
            vo.put("status", role.getStatus());
            list.add(vo);
        }
        return list;
    }

    @Transactional
    public Long saveRole(Map<String, Object> body) {
        Long id = toLong(body.get("id"));
        String roleName = (String) body.get("roleName");
        String roleCode = (String) body.get("roleCode");
        Integer dataScope = body.get("dataScope") == null ? 1 : toInt(body.get("dataScope"));
        String remark = (String) body.get("remark");

        if (roleName == null || roleName.isBlank()) {
            throw new BusinessException("请输入角色名称");
        }
        if (roleCode == null || roleCode.isBlank()) {
            throw new BusinessException("请输入角色编码");
        }
        if (id == null) {
            Long exists = roleMapper.selectCount(new LambdaQueryWrapper<SysRole>()
                    .eq(SysRole::getRoleCode, roleCode));
            if (exists != null && exists > 0) {
                throw new BusinessException("角色编码已存在");
            }
            SysRole role = new SysRole();
            role.setRoleName(roleName);
            role.setRoleCode(roleCode);
            role.setDataScope(dataScope);
            role.setRemark(remark);
            role.setStatus(1);
            roleMapper.insert(role);
            return role.getId();
        }
        SysRole role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        if ("SUPER_ADMIN".equals(role.getRoleCode())) {
            throw new BusinessException("超级管理员角色不允许修改");
        }
        role.setRoleName(roleName);
        role.setDataScope(dataScope);
        role.setRemark(remark);
        roleMapper.updateById(role);
        return id;
    }

    public List<Long> roleMenuIds(Long roleId) {
        return roleMenuMapper.selectMenuIds(roleId);
    }

    @Transactional
    public void saveRoleMenus(Long roleId, List<Long> menuIds) {
        SysRole role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        if ("SUPER_ADMIN".equals(role.getRoleCode())) {
            throw new BusinessException("超级管理员默认拥有全部权限，无需配置");
        }
        roleMenuMapper.deleteByRoleId(roleId);
        if (menuIds != null) {
            for (Long menuId : menuIds) {
                roleMenuMapper.insert(roleId, menuId);
            }
        }
    }

    public List<Map<String, Object>> menuTree() {
        List<SysMenu> menus = menuMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getStatus, 1)
                .orderByAsc(SysMenu::getSort));
        Map<Long, Map<String, Object>> nodes = new LinkedHashMap<>();
        for (SysMenu menu : menus) {
            Map<String, Object> node = new LinkedHashMap<>();
            node.put("id", menu.getId());
            node.put("name", menu.getMenuName());
            node.put("parentId", menu.getParentId());
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
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> children = (List<Map<String, Object>>) parent.get("children");
                children.add(node);
            }
        }
        return roots;
    }

    private Map<String, Object> toAdminVO(SysAdmin admin) {
        List<Long> roleIds = adminRoleMapper.selectRoleIds(admin.getId());
        List<String> roleNames = new ArrayList<>();
        for (Long roleId : roleIds) {
            SysRole role = roleMapper.selectById(roleId);
            if (role != null) {
                roleNames.add(role.getRoleName());
            }
        }
        Map<String, Object> vo = new LinkedHashMap<>();
        vo.put("id", admin.getId());
        vo.put("username", admin.getUsername());
        vo.put("realName", admin.getRealName());
        vo.put("phone", admin.getPhone());
        vo.put("status", admin.getStatus());
        vo.put("lastLoginTime", admin.getLastLoginTime());
        vo.put("roleIds", roleIds);
        vo.put("roleNames", roleNames);
        return vo;
    }

    private Long toLong(Object value) {
        if (value == null || "".equals(value)) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(String.valueOf(value));
    }

    private Integer toInt(Object value) {
        if (value == null || "".equals(value)) {
            return null;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.valueOf(String.valueOf(value));
    }

    @SuppressWarnings("unchecked")
    private List<Long> toLongList(Object value) {
        if (!(value instanceof List<?> list)) {
            return null;
        }
        List<Long> result = new ArrayList<>();
        for (Object item : list) {
            Long id = toLong(item);
            if (id != null) {
                result.add(id);
            }
        }
        return result;
    }

    public String adminDisplayName(Long adminId) {
        SysAdmin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            return "系统";
        }
        return admin.getRealName() == null ? admin.getUsername() : admin.getRealName();
    }

    public boolean isSuperAdmin(Long adminId) {
        return roleMapper.selectByAdminId(adminId).stream()
                .filter(Objects::nonNull)
                .anyMatch(role -> "SUPER_ADMIN".equals(role.getRoleCode()));
    }
}
