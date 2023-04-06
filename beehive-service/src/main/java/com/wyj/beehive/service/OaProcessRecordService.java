package com.wyj.beehive.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wyj.beehive.model.process.ProcessRecord;

/**
 * <p>
 * 审批记录 服务类
 * </p>
 *
 * @author wyj
 * @since 2023-04-05
 */
public interface OaProcessRecordService extends IService<ProcessRecord> {
    void record(Long processId,Integer status,String description);
}
