package com.wyj.beehive.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wyj.beehive.mapper.OaProcessMapper;
import com.wyj.beehive.model.process.Process;
import com.wyj.beehive.model.process.ProcessRecord;
import com.wyj.beehive.model.process.ProcessTemplate;
import com.wyj.beehive.model.system.SysUser;
import com.wyj.beehive.service.OaProcessRecordService;
import com.wyj.beehive.service.OaProcessService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wyj.beehive.service.OaProcessTemplateService;
import com.wyj.beehive.service.SysUserService;
import com.wyj.beehive.utils.LoginUserInfoHelper;
import com.wyj.beehive.vo.process.ApprovalVo;
import com.wyj.beehive.vo.process.ProcessFormVo;
import com.wyj.beehive.vo.process.ProcessQueryVo;
import com.wyj.beehive.vo.process.ProcessVo;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author wyj
 * @since 2023-04-05
 */
@Service
public class OaProcessServiceImpl extends ServiceImpl<OaProcessMapper, Process> implements OaProcessService {

    @Autowired
    private RepositoryService repositoryService;


    @Autowired
    private SysUserService userService;

    @Autowired
    private OaProcessTemplateService processTemplateService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private OaProcessRecordService processRecordService;

    @Autowired
    private HistoryService historyService;

    @Override
    public IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, ProcessQueryVo processQueryVo) {
        IPage<ProcessVo> page = baseMapper.selectPage(pageParam, processQueryVo);
        return page;
    }

    @Override
    public void deployByZip(String deployPath) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(deployPath);

        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        Deployment deployment = repositoryService.createDeployment().addZipInputStream(zipInputStream).deploy();
        System.out.println(deployment.getId());
        System.out.println(deployment.getName());
    }

    @Override
    @Transactional
    public void startUp(ProcessFormVo processFormVo) {
        //1. 根据用户id获取用户信息
        SysUser user = userService.getById(LoginUserInfoHelper.getUserId());
        //2. 根据模板id获取模板信息
        ProcessTemplate processTemplate = processTemplateService.getById(processFormVo.getProcessTemplateId());
        //3. 保存提交的审批信息到业务表
        Process process = new Process();
        BeanUtils.copyProperties(processFormVo,process);
        process.setStatus(1);
        String workNo = System.currentTimeMillis() + "";
        process.setProcessCode(workNo);
        process.setUserId(LoginUserInfoHelper.getUserId());
        process.setFormValues(processFormVo.getFormValues());
        process.setTitle(user.getName() + "发起" + processTemplate.getName() + "申请");
        baseMapper.insert(process);
        //4. 启动一个流程实例 runtimeService
        //4.1 流程定义的key 业务key(processId) 流程参数form表单数据
        String processDefinitionKey = processTemplate.getProcessDefinitionKey();
        String businessId = String.valueOf(process.getId());
        String formValues = processFormVo.getFormValues();
        JSONObject jsonObject = JSON.parseObject(formValues);
        JSONObject formData = jsonObject.getJSONObject("formData");
        Map<String,Object> map = new HashMap<>();
        for (Map.Entry<String,Object> entry:formData.entrySet()){
            map.put(entry.getKey(),entry.getValue());
        }
        map.put("assign1","zhangsan");
        map.put("assign2","lisi");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessId, map);
        //5. 查询下一个审批人
        List<Task> taskList = this.getCurrentTaskList(processInstance.getId());
        List<String> nameList = new ArrayList<>();
        for (Task task:taskList){
            String assignee = task.getAssignee();
            SysUser loginUser = userService.getByUserName(assignee);
            String username = loginUser.getName();
            nameList.add(username);
        }
        //6. 推送消息
        process.setProcessInstanceId(processInstance.getId());
        process.setDescription("等待"+ StringUtils.join(nameList.toArray(),",")+"审批");
        //7. 业务和流程进行关联
        baseMapper.updateById(process);

        processRecordService.record(process.getId(),1,user.getName()+"发起申请");
    }

    @Override
    public Page<ProcessVo> findPending(Page<Process> pageParam) {
        //1. 封装查询条件，根据当前登录用户名称查询
        TaskQuery query = taskService.createTaskQuery().taskAssignee(LoginUserInfoHelper.getUsername()).orderByTaskCreateTime().desc();
        //2.调用分页查询，返回list集合
        int begin = (int) ((pageParam.getCurrent() - 1) * pageParam.getSize());
        int size = (int) pageParam.getSize();
        List<Task> tasks = query.listPage(begin, size);
        //3.封装返回list集合数据List<ProcessVo>
        List<ProcessVo> processVoList = new ArrayList<>();
        for (Task task:tasks){
            //从task中获取流程实例id
            String processInstanceId = task.getProcessInstanceId();
            //根据流程实例id获取实例对象
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            //从流程实例对象获取业务key
            String businessKey = processInstance.getBusinessKey();
            if (businessKey==null){
                continue;
            }
            //根据业务key查询process对象
            Process process = baseMapper.selectById(businessKey);

            ProcessVo processVo = new ProcessVo();
            BeanUtils.copyProperties(process,processVo);
            processVo.setTaskId(task.getId());
            processVoList.add(processVo);
        }
        Page<ProcessVo> page = new Page<>(pageParam.getCurrent(),pageParam.getSize(),query.count());
        page.setRecords(processVoList);
        return page;
    }

    @Override
    public Map<String, Object> show(Long id) {
        //1. 根据流程id获取流程信息process
        Process process = baseMapper.selectById(id);
        //2. 根据流程id获得流程记录信息
        LambdaQueryWrapper<ProcessRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProcessRecord::getProcessId,id);
        List<ProcessRecord> recordList = processRecordService.list(wrapper);
        //3. 根据模板id查询模板信息
        ProcessTemplate processTemplate = processTemplateService.getById(process.getProcessTemplateId());
        //4. 判断当前用户是否可以进行审批
        boolean isApprove = false;
        List<Task> currentTaskList = this.getCurrentTaskList(process.getProcessInstanceId());
        for (Task task:currentTaskList){
            //判断任务审批人是否是当前用户
            Long userId = LoginUserInfoHelper.getUserId();
            SysUser user = userService.getById(userId);
            if (task.getAssignee().equals(user.getName())){
                isApprove = true;
            }
        }
        //5. 封装数据
        Map<String,Object> map = new HashMap<>();
        map.put("isApprove",isApprove);
        map.put("process",process);
        map.put("processRecordList",recordList);
        map.put("processTemplate",processTemplate);
        return map;
    }

    @Override
    public void approve(ApprovalVo approvalVo) {
        //1. 从approvalVo获取任务id,根据任务id获取流程变量
        String taskId = approvalVo.getTaskId();
        Map<String, Object> variables = taskService.getVariables(taskId);
        for (Map.Entry<String,Object> entry:variables.entrySet()){
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        }
        //2. 判断审批状态值
        if (approvalVo.getStatus()==1){
            //状态值=1，审批通过
            taskService.complete(taskId);
        }else{
            //状态值=-1，驳回,流程结束
            this.endTask(taskId);
        }
        //3. 记录审批相关过程信息
        String description = approvalVo.getStatus()==1?"已通过":"驳回";
        processRecordService.record(approvalVo.getProcessId(),approvalVo.getStatus(),description);
        //4. 查询下一个审批人,更新流程表记录
        Process process = baseMapper.selectById(approvalVo.getProcessId());
        List<Task> currentTaskList = this.getCurrentTaskList(process.getProcessInstanceId());
        if (!CollectionUtils.isEmpty(currentTaskList)){
            List<String> assignList = new ArrayList<>();
            for (Task task:currentTaskList){
                String assignee = task.getAssignee();
                SysUser user = userService.getByUserName(assignee);
                assignList.add(user.getName());
            }
            process.setDescription("等待"+ StringUtils.join(assignList.toArray(),",")+"审批");
            //7. 业务和流程进行关联
            baseMapper.updateById(process);
        }else{
            if (approvalVo.getStatus()==1){
                process.setDescription("审批完成(通过)");
                process.setStatus(2);
            }else {
                process.setDescription("审批完成(驳回)");
                process.setStatus(-1);
            }
        }
        baseMapper.updateById(process);
    }

    @Override
    public Object findProcessed(Page<Process> pageParam) {

        // 根据当前人的ID查询
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery().taskAssignee(LoginUserInfoHelper.getUsername()).finished().orderByTaskCreateTime().desc();
        List<HistoricTaskInstance> list = query.listPage((int) ((pageParam.getCurrent() - 1) * pageParam.getSize()), (int) pageParam.getSize());
        long totalCount = query.count();

        List<ProcessVo> processList = new ArrayList<>();
        for (HistoricTaskInstance item : list) {
            String processInstanceId = item.getProcessInstanceId();
            Process process = this.getOne(new LambdaQueryWrapper<Process>().eq(Process::getProcessInstanceId, processInstanceId));
            ProcessVo processVo = new ProcessVo();
            BeanUtils.copyProperties(process, processVo);
            processVo.setTaskId("0");
            processList.add(processVo);
        }
        IPage<ProcessVo> page = new Page<ProcessVo>(pageParam.getCurrent(), pageParam.getSize(), totalCount);
        page.setRecords(processList);
        return page;
    }

    @Override
    public Object findStarted(Page<ProcessVo> pageParam) {
        ProcessQueryVo processQueryVo = new ProcessQueryVo();
        processQueryVo.setUserId(LoginUserInfoHelper.getUserId());
        IPage<ProcessVo> page = baseMapper.selectPage(pageParam, processQueryVo);
        for (ProcessVo item : page.getRecords()) {
            item.setTaskId("0");
        }
        return page;
    }

    private void endTask(String taskId) {
        //  当前任务
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        List endEventList = bpmnModel.getMainProcess().findFlowElementsOfType(EndEvent.class);
        // 并行任务可能为null
        if(CollectionUtils.isEmpty(endEventList)) {
            return;
        }
        FlowNode endFlowNode = (FlowNode) endEventList.get(0);
        FlowNode currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(task.getTaskDefinitionKey());

        //  临时保存当前活动的原始方向
        List originalSequenceFlowList = new ArrayList<>();

        originalSequenceFlowList.addAll(currentFlowNode.getOutgoingFlows());
        //  清理活动方向
        currentFlowNode.getOutgoingFlows().clear();

        //  建立新方向
        SequenceFlow newSequenceFlow = new SequenceFlow();
        newSequenceFlow.setId("newSequenceFlowId");
        newSequenceFlow.setSourceFlowElement(currentFlowNode);
        newSequenceFlow.setTargetFlowElement(endFlowNode);
        List newSequenceFlowList = new ArrayList<>();
        newSequenceFlowList.add(newSequenceFlow);
        //  当前节点指向新的方向
        currentFlowNode.setOutgoingFlows(newSequenceFlowList);

        //  完成当前任务
        taskService.complete(task.getId());
    }

    //获取当前任务列表
    private List<Task> getCurrentTaskList(String id) {
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(id).list();
        return taskList;
    }
}
