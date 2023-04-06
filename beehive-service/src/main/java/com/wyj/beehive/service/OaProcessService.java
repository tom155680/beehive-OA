package com.wyj.beehive.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wyj.beehive.model.process.Process;
import com.wyj.beehive.vo.process.ApprovalVo;
import com.wyj.beehive.vo.process.ProcessFormVo;
import com.wyj.beehive.vo.process.ProcessQueryVo;
import com.wyj.beehive.vo.process.ProcessVo;

import java.util.Map;

/**
 * <p>
 * 审批类型 服务类
 * </p>
 *
 * @author wyj
 * @since 2023-04-05
 */
public interface OaProcessService extends IService<Process> {

    IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, ProcessQueryVo processQueryVo);

    //部署流程定义
    void deployByZip(String deployPath);

    void startUp(ProcessFormVo processFormVo);

    //查询待处理任务列表
    Page<ProcessVo> findPending(Page<Process> pageParam);

    Map<String, Object> show(Long id);

    void approve(ApprovalVo approvalVo);

    Object findProcessed(Page<Process> pageParam);

    Object findStarted(Page<ProcessVo> pageParam);
}
