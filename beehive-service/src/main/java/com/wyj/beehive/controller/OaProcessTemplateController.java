package com.wyj.beehive.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wyj.beehive.common.Result;
import com.wyj.beehive.model.process.ProcessTemplate;
import com.wyj.beehive.service.OaProcessTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 审批模板 前端控制器
 * </p>
 *
 * @author wyj
 * @since 2023-04-04
 */
@Api("模板审批")
@RestController
@RequestMapping("/beehive/process/processTemplate")
public class OaProcessTemplateController {
    @Autowired
    private OaProcessTemplateService processTemplateService;

    @ApiOperation("分页查询模板数据")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable("page") Long page,@PathVariable("limit") Long limit){
        Page<ProcessTemplate> pageParam = new Page<>(page,limit);
        Page<ProcessTemplate> templatePage = processTemplateService.selectPage(pageParam);
        return Result.ok(templatePage);
    }


    //@PreAuthorize("hasAuthority('bnt.processTemplate.list')")
    @ApiOperation(value = "获取")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        ProcessTemplate processTemplate = processTemplateService.getById(id);
        return Result.ok(processTemplate);
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.templateSet')")
    @ApiOperation(value = "新增")
    @PostMapping("save")
    public Result save(@RequestBody ProcessTemplate processTemplate) {
        processTemplateService.save(processTemplate);
        return Result.ok();
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.templateSet')")
    @ApiOperation(value = "修改")
    @PutMapping("update")
    public Result updateById(@RequestBody ProcessTemplate processTemplate) {
        processTemplateService.updateById(processTemplate);
        return Result.ok();
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.remove')")
    @ApiOperation(value = "删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        processTemplateService.removeById(id);
        return Result.ok();
    }

    @ApiOperation("上传流程定义")
    @PostMapping("/uploadProcessDefinition")
    public Result uploadProcessDefinition(MultipartFile file) throws FileNotFoundException {
        //获取class目录位置
        String absolutePath = new File(ResourceUtils.getURL("classpath:").getPath()).getAbsolutePath();
        //设置上传文件夹
        File tmpFile = new File(absolutePath + "/processes/");
        if (!tmpFile.exists()){
            tmpFile.mkdir();
        }
        //创建空文件，实现文件写入
        String originalFilename = file.getOriginalFilename();
        File zipFile = new File(absolutePath + "/processes/" + originalFilename);
        //保存文件
        try {
            file.transferTo(zipFile);
        } catch (IOException e) {
            return Result.fail("上传失败");
        }

        Map<String,Object> map = new HashMap<>();
        map.put("processDefinitionPath","processes/"+originalFilename);
        map.put("processDefinitionKey",originalFilename.substring(0,originalFilename.lastIndexOf(".")));
        return Result.ok(map);
    }

    @ApiOperation("发布模板")
    @GetMapping("publish/{id}")
    public Result publish(@PathVariable Long id){
        processTemplateService.publish(id);
        return Result.ok();
    }

}

