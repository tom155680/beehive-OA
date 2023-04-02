package com.wyj.beehive.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wyj.beehive.common.exception.BeehiveException;
import com.wyj.beehive.mapper.SysMenuMapper;
import com.wyj.beehive.model.system.SysMenu;
import com.wyj.beehive.model.system.SysRoleMenu;
import com.wyj.beehive.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wyj.beehive.service.SysRoleMenuService;
import com.wyj.beehive.util.MenuHelper;
import com.wyj.beehive.vo.system.AssginMenuVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author wyj
 * @since 2023-04-02
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    @Override
    public List<SysMenu> findNodes() {
        //1. 查询所有菜单数据
        List<SysMenu> sysMenus = baseMapper.selectList(null);
        //2. 构建成树形结构
        List<SysMenu> menuTreeList = MenuHelper.buildTree(sysMenus);
        return menuTreeList;
    }

    @Override
    public void removeMenuById(Long id) {
        //判断当前菜单是否有下一层菜单
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId,id);
        Integer selectCount = baseMapper.selectCount(wrapper);
        if (selectCount>0){
            throw new BeehiveException(201,"该菜单包含子菜单，无法删除!");
        }
        else{
            baseMapper.deleteById(id);
        }
    }

    @Override
    public List<SysMenu> findSysMenuByRoleId(Long roleId) {
        //1.查询所有的菜单,状态为1
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper();
        wrapper.eq(SysMenu::getStatus,1);
        List<SysMenu> sysMenus = baseMapper.selectList(wrapper);
        //2.根据角色id查询角色已分配的菜单id
        LambdaQueryWrapper<SysRoleMenu> wrapper_role_menu = new LambdaQueryWrapper<>();
        wrapper_role_menu.eq(SysRoleMenu::getRoleId,roleId);
        List<SysRoleMenu> sysRoleMenuList = sysRoleMenuService.list(wrapper_role_menu);
        List<Long> menuIdList = sysRoleMenuList.stream().map(c -> c.getMenuId()).collect(Collectors.toList());
        //3.根据菜单id获取菜单名称
        for (SysMenu menu:sysMenus){
            if (menuIdList.contains(menu.getId())){
                menu.setSelect(true);
            }else{
                menu.setSelect(false);
            }
        }
        //返回树形结构的数据
        List<SysMenu> menuList = MenuHelper.buildTree(sysMenus);
        return menuList;
    }

    @Override
    public void doAssign(AssginMenuVo assignMenuVo) {
        //1. 根据角色id删除角色中分配的数据
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId,assignMenuVo.getRoleId());
        sysRoleMenuService.remove(wrapper);
        //2. 重新给角色分配菜单
        List<Long> menuIdList = assignMenuVo.getMenuIdList();
        for (Long id:menuIdList){
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setMenuId(id);
            sysRoleMenu.setRoleId(assignMenuVo.getRoleId());
            sysRoleMenuService.save(sysRoleMenu);
        }
    }
}
