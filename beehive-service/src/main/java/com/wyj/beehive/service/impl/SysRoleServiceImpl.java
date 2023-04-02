package com.wyj.beehive.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.wyj.beehive.mapper.SysRoleMapper;
import com.wyj.beehive.model.system.SysRole;
import com.wyj.beehive.model.system.SysUserRole;
import com.wyj.beehive.service.SysRoleService;
import com.wyj.beehive.service.SysUserRoleService;
import com.wyj.beehive.vo.system.AssginRoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yongjianWang
 * @date 2023年03月25日 22:03
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Autowired
    private SysUserRoleService userRoleService;

    @Override
    public Map<String, Object> findRoleListByUserId(Long userId) {
        // 查询所有角色
        List<SysRole> roleList = baseMapper.selectList(null);
        //根据用户id查询角色用户关系表，查询到userid对应的所有角色id
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId,userId);
        List<SysUserRole> userRoleList = userRoleService.list(wrapper);
        //从userRoleList集合中获取所有的角色id
        List<Long> roleIdList = userRoleList.stream().map(c -> c.getRoleId()).collect(Collectors.toList());
        //根据角色id查询对应的角色信息
        List<SysRole> assignRoleList = new ArrayList<>();
        for (SysRole role:roleList){
            if (roleIdList.contains(role.getId())){
                assignRoleList.add(role);
            }
        }
        Map<String,Object> map = new HashMap<>();
        map.put("allRolesList",roleList);
        map.put("assginRoleList",assignRoleList);
        return map;
    }

    @Override
    public void doAssign(AssginRoleVo assginRoleVo) {
        //删除用户之前分配的角色信息
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId,assginRoleVo.getUserId());
        userRoleService.remove(wrapper);

        //重新分配角色
        List<Long> roleIdList = assginRoleVo.getRoleIdList();
        for (Long id:roleIdList){
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(assginRoleVo.getUserId());
            userRole.setRoleId(id);
            userRoleService.save(userRole);
        }
    }
}
