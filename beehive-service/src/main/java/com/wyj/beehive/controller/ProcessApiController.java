package com.wyj.beehive.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wyj.beehive.common.Result;
import com.wyj.beehive.model.process.Process;
import com.wyj.beehive.model.process.ProcessTemplate;
import com.wyj.beehive.model.process.ProcessType;
import com.wyj.beehive.service.OaProcessService;
import com.wyj.beehive.service.OaProcessTemplateService;
import com.wyj.beehive.service.OaProcessTypeService;
import com.wyj.beehive.service.SysUserService;
import com.wyj.beehive.vo.process.ApprovalVo;
import com.wyj.beehive.vo.process.ProcessFormVo;
import com.wyj.beehive.vo.process.ProcessVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author yongjianWang
 * @date 2023年04月05日 16:41
 */
@Api(tags = "审批流程接口")
@RestController
@RequestMapping("/beehive/process/api")
@CrossOrigin
public class ProcessApiController {

    @Autowired
    private OaProcessTypeService processTypeService;

    @Autowired
    private OaProcessTemplateService processTemplateService;

    @Autowired
    private OaProcessService processService;

    @Autowired
    private SysUserService userService;

    @ApiOperation("查询所有审批分类和所有审批模板")
    @GetMapping("findProcessType")
    public Result findProcessType(){
        List<ProcessType> processTypeList = processTypeService.findProcessType();
        return Result.ok(processTypeList);
    }


    @ApiOperation("获取审批模板的数据")
    @GetMapping("getProcessTemplate/{processTemplateId}")
    public Result getProcessTemplate(@PathVariable("processTemplateId") Long processTemplateId){
        ProcessTemplate processTemplate = processTemplateService.getById(processTemplateId);
        return Result.ok(processTemplate);
    }

    @ApiOperation("启动流程")
    @PostMapping("/startUp")
    public Result start(@RequestBody ProcessFormVo processFormVo){
        processService.startUp(processFormVo);
        return Result.ok();
    }

    @ApiOperation(value = "待处理")
    @GetMapping("/findPending/{page}/{limit}")
    public Result findPending(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,

            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit) {
        Page<Process> pageParam = new Page<>(page, limit);
        Page<ProcessVo> pageModel = processService.findPending(pageParam);
        return Result.ok(pageModel);
    }

    @ApiOperation("查看审批详情")
    @GetMapping("show/{id}")
    public Result show(@PathVariable("id") Long id){
        Map<String,Object> map = processService.show(id);
        return Result.ok(map);
    }

    @ApiOperation(value = "审批")
    @PostMapping("approve")
    public Result approve(@RequestBody ApprovalVo approvalVo) {
        processService.approve(approvalVo);
        return Result.ok();
    }
    @ApiOperation(value = "已处理")
    @GetMapping("/findProcessed/{page}/{limit}")
    public Result findProcessed(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,
            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit) {
        Page<Process> pageParam = new Page<>(page, limit);
        return Result.ok(processService.findProcessed(pageParam));
    }

    @ApiOperation(value = "已发起")
    @GetMapping("/findStarted/{page}/{limit}")
    public Result findStarted(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,

            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit) {
        Page<ProcessVo> pageParam = new Page<>(page, limit);
        return Result.ok(processService.findStarted(pageParam));
    }

    @ApiOperation(value = "获取当前用户基本信息")
    @GetMapping("getCurrentUser")
    public Result getCurrentUser() {
        return Result.ok(userService.getCurrentUser());
    }
}
