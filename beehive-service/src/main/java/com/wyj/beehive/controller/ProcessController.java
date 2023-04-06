package com.wyj.beehive.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wyj.beehive.common.Result;
import com.wyj.beehive.model.process.Process;
import com.wyj.beehive.service.OaProcessService;
import com.wyj.beehive.vo.process.ProcessQueryVo;
import com.wyj.beehive.vo.process.ProcessVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 审批类型 前端控制器
 * </p>
 *
 * @author wyj
 * @since 2023-04-05
 */
@Api(tags = "审批流管理")
@RestController
@RequestMapping(value = "/beehive/process")
@CrossOrigin
public class ProcessController {

    @Autowired
    private OaProcessService processService;

    //    @PreAuthorize("hasAuthority('bnt.process.list')")
    @ApiOperation(value = "获取分页列表")
    @GetMapping("{page}/{limit}")
    public Result index(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,

            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit,

            @ApiParam(name = "processQueryVo", value = "查询对象", required = false)
                    ProcessQueryVo processQueryVo) {
        Page<ProcessVo> pageParam = new Page<>(page, limit);
        IPage<ProcessVo> pageModel = processService.selectPage(pageParam, processQueryVo);
        return Result.ok(pageModel);
    }
}
