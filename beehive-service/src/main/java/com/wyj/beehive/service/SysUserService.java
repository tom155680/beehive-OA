package com.wyj.beehive.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.wyj.beehive.model.system.SysUser;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author wyj
 * @since 2023-03-31
 */
public interface SysUserService extends IService<SysUser> {

    SysUser getByUserName(String username);
}
