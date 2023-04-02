package com.wyj.beehive.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wyj.beehive.model.system.SysRole;
import com.wyj.beehive.vo.system.AssginRoleVo;

import java.util.Map;

/**
 * @author yongjianWang
 * @date 2023年03月25日 22:03
 */
public interface SysRoleService extends IService<SysRole> {
    Map<String, Object> findRoleListByUserId(Long userId);

    void doAssign(AssginRoleVo assginRoleVo);
}
