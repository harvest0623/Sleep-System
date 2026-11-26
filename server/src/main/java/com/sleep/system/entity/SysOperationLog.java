package com.sleep.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 后台操作日志
 */
@Data
@TableName("sys_operation_log")
public class SysOperationLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long adminId;

    private String adminName;

    private String module;

    private String action;

    private String target;

    private String detail;

    private String ip;

    private Integer result;

    private Integer costMs;

    private LocalDateTime createdAt;
}
