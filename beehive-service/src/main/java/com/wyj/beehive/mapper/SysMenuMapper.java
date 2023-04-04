package com.wyj.beehive.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wyj.beehive.model.system.SysMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 菜单表 Mapper 接口
 * </p>
 *
 * @author wyj
 * @since 2023-04-02
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<SysMenu> findMenuListByUserId(@Param("userId") Long userId);
}
