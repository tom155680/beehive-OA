package com.wyj.beehive.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.wyj.beehive.mapper.OaProcessTemplateMapper;
import com.wyj.beehive.model.process.ProcessTemplate;
import com.wyj.beehive.model.process.ProcessType;
import com.wyj.beehive.service.OaProcessService;
import com.wyj.beehive.service.OaProcessTemplateService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wyj.beehive.service.OaProcessTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 审批模板 服务实现类
 * </p>
 *
 * @author wyj
 * @since 2023-04-04
 */
@Service
public class OaProcessTemplateServiceImpl extends ServiceImpl<OaProcessTemplateMapper, ProcessTemplate> implements OaProcessTemplateService {

    @Autowired
    private OaProcessTypeService processTypeService;

    @Autowired
    private OaProcessService processService;

    /**
     * 分页查询,用于封装processTypeName
     * @param pageParam
     * @return
     */
    @Override
    public Page<ProcessTemplate> selectPage(Page<ProcessTemplate> pageParam) {
        //1. 先分页查询出所有的模板数据
        Page<ProcessTemplate> processTemplatePage = baseMapper.selectPage(pageParam, null);
        List<ProcessTemplate> records = processTemplatePage.getRecords();
        //2. 遍历模板数据，取出模板类型id值
        for (ProcessTemplate template:records){
            //3. 根据id值查询模板类型对应的名称
            Long processTypeId = template.getProcessTypeId();
            LambdaQueryWrapper<ProcessType> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProcessType::getId,processTypeId);
            ProcessType processType = processTypeService.getOne(wrapper);
            //4. 封装对象
            if (processType==null){
                continue;
            }
            template.setProcessTypeName(processType.getName());
        }

        return processTemplatePage;
    }

    /**
     * 发布模板
     * @param id
     */
    @Override
    @Transactional
    public void publish(Long id) {
        LambdaQueryWrapper<ProcessTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProcessTemplate::getId,id);
        ProcessTemplate processTemplate = baseMapper.selectOne(wrapper);
        processTemplate.setStatus(1);
        baseMapper.updateById(processTemplate);

        //流程定义部署
        if (!StringUtils.isNullOrEmpty(processTemplate.getProcessDefinitionPath()))
            processService.deployByZip(processTemplate.getProcessDefinitionPath());

    }
}
