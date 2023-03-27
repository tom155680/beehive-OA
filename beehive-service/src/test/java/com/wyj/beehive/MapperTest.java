package com.wyj.beehive;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wyj.beehive.mapper.SysRoleMapper;
import com.wyj.beehive.model.system.SysRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

/**
 * @author yongjianWang
 * @date 2023年03月25日 21:40
 */
@SpringBootTest
public class MapperTest {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Test
    public void getAll(){
        List<SysRole> sysRoles = sysRoleMapper.selectList(null);
        System.out.println(sysRoles);
    }

    @Test
    public void add(){
        SysRole sysRole = new SysRole();
        sysRole.setRoleName("admin");
        sysRole.setRoleCode("role");
        sysRole.setDescription("超级管理员");

        int insert = sysRoleMapper.insert(sysRole);
        System.out.println(sysRole.getId());
    }

    @Test
    public void testUpdateById(){
        SysRole sysRole = new SysRole();
        sysRole.setId(10L);
        sysRole.setRoleName("角色管理员1");

        int result = sysRoleMapper.updateById(sysRole);
        System.out.println(result);
    }

    /**
     * 删除有两种：物理删除、逻辑删除
     */
    @Test
    public void testDeleteById(){
        int result = sysRoleMapper.deleteById(10L);
        System.out.println(result);
    }
;
    @Test
    public void testDeleteBatchIds() {
        int result = sysRoleMapper.deleteBatchIds(Arrays.asList(1, 1));
        System.out.println(result);
    }

    @Test
    public void testSelect1() {
        QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_code", "role");
        List<SysRole> users = sysRoleMapper.selectList(queryWrapper);
        System.out.println(users);
    }

    @Test
    public void testSelect2() {
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getRoleCode, "role");
        List<SysRole> users = sysRoleMapper.selectList(queryWrapper);
        System.out.println(users);
    }


}
