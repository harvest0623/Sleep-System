package com.sleep.system.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysAdminRoleMapper {

    @Select("SELECT role_id FROM sys_admin_role WHERE admin_id = #{adminId}")
    List<Long> selectRoleIds(@Param("adminId") Long adminId);

    @Delete("DELETE FROM sys_admin_role WHERE admin_id = #{adminId}")
    int deleteByAdminId(@Param("adminId") Long adminId);

    @Insert("INSERT INTO sys_admin_role(admin_id, role_id) VALUES(#{adminId}, #{roleId})")
    int insert(@Param("adminId") Long adminId, @Param("roleId") Long roleId);
}
