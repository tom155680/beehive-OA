package com.wyj.beehive.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wyj.beehive.model.process.ProcessType;

import java.util.List;

/**
 * <p>
 * 审批类型 服务类
 * </p>
 *
 * @author wyj
 * @since 2023-04-04
 */
public interface OaProcessTypeService extends IService<ProcessType> {

    List<ProcessType> findProcessType();
}
