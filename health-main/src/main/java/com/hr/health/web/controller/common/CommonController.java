package com.hr.health.web.controller.common;

import com.hr.health.common.core.domain.Result;
import com.hr.health.system.service.CommonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 通用请求处理
 *
 * @author swq
 */
@Api(tags = "上传下载")
@RestController
@RequestMapping("/common/Resource")
public class CommonController {
    private static final Logger log = LoggerFactory.getLogger(CommonController.class);
    @Resource
    private CommonService commonService;

    /**
     * 通用下载请求
     *
     * @param fileName 文件名称
     * @param delete   是否删除
     */
    @GetMapping("/download")
    @ApiOperation("通用下载请求")
    public void fileDownload(String fileName, Boolean delete, HttpServletResponse response, HttpServletRequest request) {
        commonService.fileDownload(fileName, delete, request, response);
    }

    /**
     * 通用上传请求（单个）
     */
    @PostMapping("/upload")
    @ApiOperation("通用上传请求（单个）")
    public Result uploadFile(MultipartFile file) throws Exception {
        return commonService.uploadFile(file);
    }

    /**
     * 通用上传请求（多个）
     */
    @PostMapping("/uploads")
    @ApiOperation("通用上传请求（多个）")
    public Result uploadFiles(List<MultipartFile> files) throws Exception {
        return commonService.uploadFiles(files);
    }

    /**
     * 本地资源通用下载
     */
    @GetMapping("/download/resource")
    @ApiOperation("本地资源通用下载")
    public void resourceDownload(String resource, HttpServletRequest request, HttpServletResponse response) throws Exception {
        commonService.resourceDownload(resource, request, response);
    }
}
