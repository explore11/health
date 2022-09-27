package com.hr.health.system.service;

import com.hr.health.common.core.domain.Result;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface CommonService {
    /**
     * 通用下载请求
     * @param fileName
     * @param delete
     * @param request
     * @param response
     */
    void fileDownload(String fileName, Boolean delete, HttpServletRequest request, HttpServletResponse response);

    /**
     * 通用上传请求（单个）
     * @param file
     * @return
     */
    Result uploadFile(MultipartFile file);

    /**
     * 通用上传请求（多个）
     * @param files
     * @return
     */
    Result uploadFiles(List<MultipartFile> files);

    /**
     * 本地资源通用下载
     * @param resource
     * @param request
     * @param response
     */
    void resourceDownload(String resource, HttpServletRequest request, HttpServletResponse response);
}
