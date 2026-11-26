package com.sleep.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sleep.system.entity.AppUser;
import com.sleep.system.entity.SysAdmin;
import com.sleep.system.entity.SysOperationLog;
import com.sleep.system.mapper.AppUserMapper;
import com.sleep.system.mapper.SysAdminMapper;
import com.sleep.system.mapper.SysOperationLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 工作台统计与操作日志查询
 */
@Service
@RequiredArgsConstructor
public class OpsService {

    private final AppUserMapper userMapper;

    private final SysAdminMapper adminMapper;

    private final SysOperationLogMapper logMapper;

    public Map<String, Object> dashboard() {
        Long todayUsers = userMapper.selectCount(new LambdaQueryWrapper<AppUser>()
                .ge(AppUser::getCreatedAt, LocalDate.now().atStartOfDay()));
        Long totalUsers = userMapper.selectCount(null);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("todayUsers", todayUsers == null ? 0 : todayUsers);
        data.put("totalUsers", totalUsers == null ? 0 : totalUsers);
        data.put("todayAmount", 0);
        data.put("pendingAppointments", 0);
        data.put("pendingReviews", 0);
        return data;
    }

    public Map<String, Object> pageLogs(long page, long size, String keyword, String module) {
        LambdaQueryWrapper<SysOperationLog> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(condition -> condition
                    .like(SysOperationLog::getAction, keyword)
                    .or().like(SysOperationLog::getTarget, keyword)
                    .or().like(SysOperationLog::getModule, keyword));
        }
        if (module != null && !module.isBlank()) {
            wrapper.eq(SysOperationLog::getModule, module);
        }
        wrapper.orderByDesc(SysOperationLog::getId);
        Page<SysOperationLog> result = logMapper.selectPage(new Page<>(page, size), wrapper);

        Map<Long, String> adminNames = new HashMap<>();
        for (SysAdmin admin : adminMapper.selectList(null)) {
            adminNames.put(admin.getId(),
                    admin.getRealName() == null ? admin.getUsername() : admin.getRealName());
        }
        List<Map<String, Object>> records = new ArrayList<>();
        for (SysOperationLog log : result.getRecords()) {
            Map<String, Object> vo = new LinkedHashMap<>();
            vo.put("id", log.getId());
            vo.put("adminId", log.getAdminId());
            vo.put("adminName", adminNames.getOrDefault(log.getAdminId(), "系统"));
            vo.put("module", log.getModule());
            vo.put("action", log.getAction());
            vo.put("target", log.getTarget());
            vo.put("ip", log.getIp());
            vo.put("result", log.getResult());
            vo.put("costMs", log.getCostMs());
            vo.put("createdAt", log.getCreatedAt());
            records.add(vo);
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("records", records);
        data.put("total", result.getTotal());
        return data;
    }
}
