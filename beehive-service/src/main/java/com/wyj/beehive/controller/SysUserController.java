package com.wyj.beehive.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.wyj.beehive.common.MD5;
import com.wyj.beehive.common.Result;
import com.wyj.beehive.model.system.SysUser;
import com.wyj.beehive.service.SysUserService;
import com.wyj.beehive.vo.system.SysUserQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author wyj
 * @since 2023-03-31
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/beehive/sysUser")
public class SysUserController {

    @Autowired
    public SysUserService service;

    @ApiOperation("用户条件分页查询")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable("page") Integer curPage, @PathVariable("limit") Integer pageSize,
                        SysUserQueryVo vo){
        //创建page对象
        Page<SysUser> page = new Page<>(curPage,pageSize);
        //封装查询对象
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        String keyword = vo.getKeyword();
        String createTimeBegin = vo.getCreateTimeBegin();
        String createTimeEnd = vo.getCreateTimeEnd();
        if(!StringUtils.isNullOrEmpty(keyword)){
            wrapper.like(SysUser::getUsername,keyword);
        }
        if (!StringUtils.isNullOrEmpty(createTimeBegin)){
            wrapper.ge(SysUser::getCreateTime,createTimeBegin);
        }
        if (!StringUtils.isNullOrEmpty(createTimeEnd)){
            wrapper.ge(SysUser::getCreateTime,createTimeEnd);
        }
        //调用mp的方法实现条件分页查询
        Page<SysUser> userPage = service.page(page,wrapper);
        return Result.ok(userPage);
    }

    @ApiOperation(value = "获取用户")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        SysUser user = service.getById(id);
        return Result.ok(user);
    }

    @ApiOperation(value = "保存用户")
    @PostMapping("save")
    public Result save(@RequestBody SysUser user) {
        //对密码进行加密处理
        String encryptPwd = MD5.encrypt(user.getPassword());
        user.setPassword(encryptPwd);
        service.save(user);
        return Result.ok();
    }

    @ApiOperation(value = "更新用户")
    @PutMapping("update")
    public Result updateById(@RequestBody SysUser user) {
        service.updateById(user);
        return Result.ok();
    }

    @ApiOperation(value = "删除用户")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        service.removeById(id);
        return Result.ok();
    }

    @ApiOperation("更新用户状态")
    @GetMapping("updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable("id") Long id,@PathVariable("status") Integer status){
        SysUser user = service.getById(id);

        user.setStatus(status);
        this.updateById(user);
        return Result.ok();
    }

}

