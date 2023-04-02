package com.wyj.beehive.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.wyj.beehive.common.Result;
import com.wyj.beehive.common.exception.BeehiveException;
import com.wyj.beehive.model.system.SysRole;
import com.wyj.beehive.service.SysRoleService;
import com.wyj.beehive.vo.system.AssginRoleVo;
import com.wyj.beehive.vo.system.SysRoleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author yongjianWang
 * @date 2023年03月25日 22:07
 */
@Api(tags = "角色管理")
@RestController
@RequestMapping("/sysRole")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @ApiOperation("根据用户id获取角色")
    @GetMapping("/toAssign/{id}")
    public Result assign(@PathVariable("id") Long userId){
        Map<String,Object> map = sysRoleService.findRoleListByUserId(userId);
        return Result.ok(map);
    }

    @ApiOperation("分配角色")
    @PostMapping("doAssign")
    public Result doAssign(@RequestBody AssginRoleVo assginRoleVo){
        sysRoleService.doAssign(assginRoleVo);
        return Result.ok();
    }

    @ApiOperation(value = "获取全部角色")
    @RequestMapping(value = "/findAll",method = RequestMethod.GET)
    public Result findAll(){
        List<SysRole> roleList = sysRoleService.list();
        return Result.ok(roleList);
    }

    @ApiOperation("根据Id查询")
    @GetMapping("/getById/{id}")
    public Result getById(@PathVariable("id") Long id){
        SysRole sysRole = sysRoleService.getById(id);
        return Result.ok(sysRole);
    }
    
    @ApiOperation(value = "条件分页查询")
    @GetMapping("{page}/{limit}")
    public Result pageQuery(@PathVariable("page") Integer curPage,
                            @PathVariable("limit") Integer pageSize,
                            SysRoleQueryVo queryVo){
        //创建page对象,传递分页相关参数
        Page<SysRole> pageParam = new Page<>(curPage,pageSize);
        //封装条件,判断条件是否为空
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        String roleName = queryVo.getRoleName();
        if (!StringUtils.isNullOrEmpty(roleName)){
            wrapper.like("role_name",roleName);
        }
        Page<SysRole> page = sysRoleService.page(pageParam, wrapper);
        return Result.ok(page);
    }

    @ApiOperation("添加角色")
    @PostMapping("save")
    public Result save(@RequestBody SysRole role){
        boolean is_success = sysRoleService.save(role);
        if (is_success){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    @ApiOperation("修改角色")
    @PutMapping("modify")
    public Result modify(@RequestBody SysRole sysRole){
        boolean is_success = sysRoleService.updateById(sysRole);
        if (is_success){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    @ApiOperation(value = "根据Id删除")
    @DeleteMapping("delete/{id}")
    public Result deleteById(@PathVariable Long id){
        boolean is_success = sysRoleService.removeById(id);
        if (is_success){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    @ApiOperation("批量删除")
    @DeleteMapping("delete")
    public Result deleteBatch(@RequestBody List<Long> ids){
        boolean is_success = sysRoleService.removeByIds(ids);
        if (is_success){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }


}
