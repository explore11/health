package com.hr.health.web.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.health.common.core.controller.BaseController;
import com.hr.health.common.core.domain.Result;
import com.hr.health.system.domain.SysFileInfo;
import com.hr.health.system.service.SysFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(tags = "文件信息")
@RestController
@RequestMapping("/system/file")
public class SysFileController extends BaseController {

    @Resource
    private SysFileService sysFileService;



    /**
     * 本地资源通用下载
     */
    @PreAuthorize("@ss.hasPermi('system:file:resourceDownload')")
    @GetMapping("/resourceDownload")
    @ApiOperation(value = "本地资源通用下载")
    public void resourceDownload(String resource, HttpServletRequest request, HttpServletResponse response) throws Exception {
        sysFileService.resourceDownload(resource, request, response);
    }

    /**
     * 文件上传请求（多个）
     */
    @PreAuthorize("@ss.hasPermi('system:file:uploadMultiFiles')")
    @PostMapping("/uploadMultiFiles")
    @ApiOperation("通用上传请求（多个）")
    public Result uploadMultiFiles(List<MultipartFile> files) {
        return sysFileService.uploadMultiFiles(files);
    }


    /**
     * 获取文件列表
     *
     * @param sysFileInfo
     * @return
     */
    @PreAuthorize("@ss.hasPermi('system:file:list')")
    @GetMapping("/list")
    @ApiOperation("获取文件列表")
    public Result<IPage<SysFileInfo>> list(SysFileInfo sysFileInfo) {
        return Result.success(sysFileService.list(sysFileInfo));
    }


    /**
     * 添加
     *
     * @param sysFileInfo
     * @return
     */
    @PreAuthorize("@ss.hasPermi('system:file:add')")
    @PostMapping("/add")
    @ApiOperation("添加")
    public Result add(@RequestBody SysFileInfo sysFileInfo) {
        return Result.judge(sysFileService.add(sysFileInfo));
    }

    /**
     * 编辑
     *
     * @param sysFileInfo
     * @return
     */
    @PreAuthorize("@ss.hasPermi('system:file:edit')")
    @PutMapping("/edit")
    @ApiOperation("编辑")
    public Result edit(@RequestBody SysFileInfo sysFileInfo) {
        return Result.judge(sysFileService.edit(sysFileInfo));
    }

    /**
     * 删除
     *
     * @param fileId
     * @return
     */
    @PreAuthorize("@ss.hasPermi('system:file:remove')")
    @DeleteMapping("/remove/{fileId}")
    @ApiOperation("删除")
    public Result remove(@PathVariable("fileId") Long fileId) {
        return Result.judge(sysFileService.remove(fileId));
    }


    /**
     * 查询单个
     *
     * @param fileId
     * @return
     */
    @PreAuthorize("@ss.hasPermi('system:file:queryById')")
    @GetMapping("/query/{fileId}")
    @ApiOperation("删除")
    public Result<SysFileInfo> queryById(@PathVariable("fileId") Long fileId) {
        return Result.success(sysFileService.queryById(fileId));
    }


}
