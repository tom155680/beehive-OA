package com.wyj.beehive.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wyj.beehive.common.Result;
import com.wyj.beehive.model.process.ProcessType;
import com.wyj.beehive.service.OaProcessTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 审批类型 前端控制器
 * </p>
 *
 * @author wyj
 * @since 2023-04-04
 */
@Api(tags = "审批设置")
@RestController
@RequestMapping("/beehive/process/processType")
public class OaProcessTypeController {

    @Autowired
    private OaProcessTypeService processTypeService;

    @ApiOperation("查询所有的审批分类")
    @GetMapping("findAll")
    public Result findAll(){
        List<ProcessType> list = processTypeService.list();
        return Result.ok(list);
    }

    @ApiOperation("分页查询")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable("page") Long page,@PathVariable("limit") Long limit){
        Page<ProcessType> pageParam = new Page<>(page, limit);
        Page<ProcessType> processTypePage = processTypeService.page(pageParam);
        return Result.ok(processTypePage);
    }

//    @PreAuthorize("hasAuthority('bnt.processType.list')")
    @ApiOperation(value = "获取")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        ProcessType processType = processTypeService.getById(id);
        return Result.ok(processType);
    }

//    @PreAuthorize("hasAuthority('bnt.processType.add')")
    @ApiOperation(value = "新增")
    @PostMapping("save")
    public Result save(@RequestBody ProcessType processType) {
        processTypeService.save(processType);
        return Result.ok();
    }

//    @PreAuthorize("hasAuthority('bnt.processType.update')")
    @ApiOperation(value = "修改")
    @PutMapping("update")
    public Result updateById(@RequestBody ProcessType processType) {
        processTypeService.updateById(processType);
        return Result.ok();
    }

//    @PreAuthorize("hasAuthority('bnt.processType.remove')")
    @ApiOperation(value = "删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        processTypeService.removeById(id);
        return Result.ok();
    }
}

