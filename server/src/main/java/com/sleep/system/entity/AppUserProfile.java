package com.sleep.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户睡眠偏好档案
 */
@Data
@TableName("app_user_profile")
public class AppUserProfile {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Integer sleepDifficulty;

    private Integer latencyMinutes;

    private String wakePeriods;

    private String preferences;

    private String remindTime;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
