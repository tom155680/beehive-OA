package com.wyj.beehive.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wyj.beehive.model.process.Process;
import com.wyj.beehive.vo.process.ProcessQueryVo;
import com.wyj.beehive.vo.process.ProcessVo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 审批类型 Mapper 接口
 * </p>
 *
 * @author wyj
 * @since 2023-04-05
 */
public interface OaProcessMapper extends BaseMapper<Process> {
    public IPage<ProcessVo> selectPage(Page<ProcessVo> page, @Param("vo") ProcessQueryVo processQueryVo);
}
