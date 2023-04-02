package com.wyj.beehive.util;

import com.wyj.beehive.model.system.SysMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yongjianWang
 * @date 2023年04月02日 12:10
 */
public class MenuHelper {
    /**
     * menuList转换为树形结构（递归方式）
     * @param sysMenus
     * @return
     */
    public static List<SysMenu> buildTree(List<SysMenu> sysMenus) {
        //1.创建list集合，用于返回
        List<SysMenu> menuTreeList = new ArrayList<>();
        //遍历所有的菜单数据
        for (SysMenu sysMenu:sysMenus){
            if (sysMenu.getParentId()==0){
                //parentId = 0表示入口
                menuTreeList.add(getChildren(sysMenu,sysMenus));
            }
        }
        return menuTreeList;
    }

    private static SysMenu getChildren(SysMenu sysMenu, List<SysMenu> sysMenus) {
        sysMenu.setChildren(new ArrayList<SysMenu>());
        // 遍历所有菜单数据,判断id和parentId的对应关系
        for (SysMenu menu:sysMenus){
            if (sysMenu.getId().longValue()==menu.getParentId().longValue()){
                sysMenu.getChildren().add(getChildren(menu,sysMenus));
            }
        }
        return sysMenu;
    }
}
