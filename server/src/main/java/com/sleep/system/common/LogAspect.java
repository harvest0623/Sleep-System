package com.sleep.system.common;

import com.sleep.system.entity.SysOperationLog;
import com.sleep.system.mapper.SysOperationLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

/**
 * 操作日志切面：记录后台敏感操作的执行情况
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final SysOperationLogMapper operationLogMapper;

    @Around("@annotation(logOperation)")
    public Object around(ProceedingJoinPoint point, LogOperation logOperation) throws Throwable {
        long start = System.currentTimeMillis();
        boolean success = true;
        try {
            return point.proceed();
        } catch (Throwable throwable) {
            success = false;
            throw throwable;
        } finally {
            try {
                saveLog(logOperation, System.currentTimeMillis() - start, success);
            } catch (Exception exception) {
                log.warn("操作日志写入失败", exception);
            }
        }
    }

    private void saveLog(LogOperation logOperation, long cost, boolean success) {
        SysOperationLog entity = new SysOperationLog();
        entity.setAdminId(UserContext.currentId());
        entity.setModule(logOperation.module());
        entity.setAction(logOperation.action());
        entity.setResult(success ? 1 : 0);
        entity.setCostMs((int) cost);
        entity.setCreatedAt(LocalDateTime.now());
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            entity.setIp(resolveIp(request));
            entity.setTarget(request.getMethod() + " " + request.getRequestURI());
        }
        operationLogMapper.insert(entity);
    }

    private String resolveIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
