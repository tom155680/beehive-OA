package com.wyj.beehive.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wyj.beehive.mapper.OaProcessTypeMapper;
import com.wyj.beehive.model.process.ProcessTemplate;
import com.wyj.beehive.model.process.ProcessType;
import com.wyj.beehive.service.OaProcessTemplateService;
import com.wyj.beehive.service.OaProcessTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author wyj
 * @since 2023-04-04
 */
@Service
public class OaProcessTypeServiceImpl extends ServiceImpl<OaProcessTypeMapper, ProcessType> implements OaProcessTypeService {

    @Autowired
    private OaProcessTemplateService processTemplateService;

    @Override
    public List<ProcessType> findProcessType() {
        //查询所有审批分类
        List<ProcessType> processTypes = baseMapper.selectList(null);
        //遍历审批分类列表，根据分类id查询对应的审批模板
        for (ProcessType processType:processTypes){
            Long id = processType.getId();
            LambdaQueryWrapper<ProcessTemplate> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProcessTemplate::getProcessTypeId,id);
            List<ProcessTemplate> processTemplates = processTemplateService.list(wrapper);
            //封装审批模板数据到审批列表中
            processType.setProcessTemplateList(processTemplates);
        }
        return processTypes;
    }
}
