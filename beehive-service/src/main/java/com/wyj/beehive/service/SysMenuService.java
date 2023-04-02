package com.wyj.beehive.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.wyj.beehive.model.system.SysMenu;
import com.wyj.beehive.vo.system.AssginMenuVo;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author wyj
 * @since 2023-04-02
 */
public interface SysMenuService extends IService<SysMenu> {

    List<SysMenu> findNodes();

    void removeMenuById(Long id);

    List<SysMenu> findSysMenuByRoleId(Long roleId);

    void doAssign(AssginMenuVo assignMenuVo);
}
