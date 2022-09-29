package com.hr.health.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hr.health.common.core.domain.Result;
import com.hr.health.system.domain.SysFileInfo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface SysFileService extends IService<SysFileInfo> {
    /**
     * 获取文件列表
     *
     * @param sysFileInfo
     * @return
     */
    IPage<SysFileInfo> list(SysFileInfo sysFileInfo);

    /**
     * 添加
     *
     * @param sysFileInfo
     * @return
     */
    int add(SysFileInfo sysFileInfo);

    /**
     * 编辑
     *
     * @param sysFileInfo
     * @return
     */
    int edit(SysFileInfo sysFileInfo);

    /**
     * 删除
     *
     * @param fileId
     * @return
     */
    int remove(Long fileId);

    /**
     * 查询单个
     *
     * @param fileId
     * @return
     */
    SysFileInfo queryById(Long fileId);

    /**
     * 文件上传请求（多个）
     *
     * @param files
     * @return
     */
    Result uploadMultiFiles(List<MultipartFile> files);

    /**
     * @param resource
     * @param request
     * @param response
     */
    void resourceDownload(String resource, HttpServletRequest request, HttpServletResponse response);

    void downloadFile(String resource, HttpServletRequest request, HttpServletResponse response);
}
