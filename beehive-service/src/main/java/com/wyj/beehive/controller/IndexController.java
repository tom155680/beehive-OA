package com.wyj.beehive.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wyj.beehive.common.JwtHelper;
import com.wyj.beehive.common.MD5;
import com.wyj.beehive.common.Result;
import com.wyj.beehive.common.exception.BeehiveException;
import com.wyj.beehive.model.system.SysUser;
import com.wyj.beehive.service.SysMenuService;
import com.wyj.beehive.service.SysUserService;
import com.wyj.beehive.vo.system.LoginVo;
import com.wyj.beehive.vo.system.RouterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yongjianWang
 * @date 2023年03月26日 15:54
 */
@RestController
@RequestMapping("/index")
public class IndexController {

    @Autowired
    private SysUserService userService;

    @Autowired
    private SysMenuService menuService;

    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo){
//        Map<String,Object> map = new HashMap<>();
//        map.put("token","admin-token");
        //获取用户名和密码
        String username = loginVo.getUsername();
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername,username);
        SysUser user = userService.getOne(wrapper);
        //判断用户是否存在
        if (user==null){
            throw new BeehiveException(201,"用户不存在!");
        }
        //判断密码是否正确
        String password_db = user.getPassword();
        String password_input = MD5.encrypt(loginVo.getPassword());
        if (!password_db.equals(password_input)){
            throw new BeehiveException(201,"输入密码错误!");
        }
        //判断用户是否禁用
        if (user.getStatus()==0){
            throw new BeehiveException(201,"用户禁用,请联系管理员!");
        }
        //根据用户名和id生成token字符串
        String token = JwtHelper.createToken(user.getId(), user.getUsername());
        Map<String,Object> map = new HashMap<>();
        map.put("token",token);
        return Result.ok(map);
    }

    @GetMapping("info")
    public Result info(HttpServletRequest request){
        //从请求头里获取用户信息token
        String token = request.getHeader("token");
        //从token字符串中获取用户名称或者id
        Long userId = JwtHelper.getUserId(token);
        //根据用户id查询数据库，获取用户的信息
        SysUser user = userService.getById(userId);
        //根据用户id获取用户可以获取的操作列表，根据数据库动态构建路由结构
        List<RouterVo> routeList =  menuService.findMenuListByUserId(userId);

        //根据用户id获取可以操作的按钮列表
        List<String> permsList = menuService.findUserPermsByUserId(userId);
        Map<String, Object> map = new HashMap<>();
        map.put("roles","[admin]");
        map.put("name",user.getUsername());
        map.put("avatar","https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
        //TODO 返回用户可以操作菜单
        map.put("routers",routeList);
        //TODO 返回用户可以操作按钮
       map.put("buttons",permsList);
        return Result.ok(map);
    }

    @PostMapping("logout")
    public Result logout(){
        return Result.ok();
    }
}
