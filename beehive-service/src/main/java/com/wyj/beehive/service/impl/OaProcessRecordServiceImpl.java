package com.wyj.beehive.service.impl;


import com.wyj.beehive.mapper.OaProcessRecordMapper;
import com.wyj.beehive.model.process.Process;
import com.wyj.beehive.model.process.ProcessRecord;
import com.wyj.beehive.model.system.SysUser;
import com.wyj.beehive.service.OaProcessRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wyj.beehive.service.SysUserService;
import com.wyj.beehive.utils.LoginUserInfoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 审批记录 服务实现类
 * </p>
 *
 * @author wyj
 * @since 2023-04-05
 */
@Service
public class OaProcessRecordServiceImpl extends ServiceImpl<OaProcessRecordMapper, ProcessRecord> implements OaProcessRecordService {

    @Autowired
    private SysUserService userService;

    @Override
    public void record(Long processId, Integer status, String description) {
        Long userId = LoginUserInfoHelper.getUserId();
        SysUser user = userService.getById(userId);
        ProcessRecord processRecord = new ProcessRecord();
        processRecord.setProcessId(processId);
        processRecord.setStatus(status);
        processRecord.setDescription(description);
        processRecord.setOperateUser(user.getName());
        processRecord.setOperateUserId(userId);
        baseMapper.insert(processRecord);
    }
}
