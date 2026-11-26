package com.sleep.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sleep.system.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    @Select("SELECT m.* FROM sys_menu m "
            + "INNER JOIN sys_role_menu rm ON m.id = rm.menu_id "
            + "WHERE rm.role_id = #{roleId} AND m.deleted = 0 AND m.status = 1 "
            + "ORDER BY m.sort ASC")
    List<SysMenu> selectByRoleId(@Param("roleId") Long roleId);
}
