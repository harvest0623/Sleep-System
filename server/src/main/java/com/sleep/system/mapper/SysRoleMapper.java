package com.sleep.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sleep.system.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    @Select("SELECT r.* FROM sys_role r "
            + "INNER JOIN sys_admin_role ar ON r.id = ar.role_id "
            + "WHERE ar.admin_id = #{adminId} AND r.deleted = 0")
    List<SysRole> selectByAdminId(@Param("adminId") Long adminId);
}
