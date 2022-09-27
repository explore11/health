package com.hr.health.system.service.impl;

import com.hr.health.common.config.HealthConfig;
import com.hr.health.common.constant.Constants;
import com.hr.health.common.core.domain.AjaxResult;
import com.hr.health.common.core.domain.Result;
import com.hr.health.common.enums.ResultCode;
import com.hr.health.common.utils.StringUtils;
import com.hr.health.common.utils.file.FileUploadUtils;
import com.hr.health.common.utils.file.FileUtils;
import com.hr.health.system.config.ServerConfig;
import com.hr.health.system.service.CommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommonServiceImpl implements CommonService {
    private static final Logger log = LoggerFactory.getLogger(CommonServiceImpl.class);
    private static final String FILE_SEPARATOR = ",";

    @Autowired
    private ServerConfig serverConfig;


    /**
     * 本地资源通用下载
     *
     * @param resource
     * @param request
     * @param response
     */
    @Override
    public void resourceDownload(String resource, HttpServletRequest request, HttpServletResponse response) {
        try {
            if (!FileUtils.checkAllowDownload(resource)) {
                throw new Exception(StringUtils.format("资源文件({})非法，不允许下载。 ", resource));
            }
            // 本地资源路径
            String localPath = HealthConfig.getProfile();
            // 数据库资源地址
            String downloadPath = localPath + StringUtils.substringAfter(resource, Constants.RESOURCE_PREFIX);
            // 下载名称
            String downloadName = StringUtils.substringAfterLast(downloadPath, "/");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, downloadName);
            FileUtils.writeBytes(downloadPath, response.getOutputStream());
        } catch (Exception e) {
            log.error("下载文件失败", e);
        }
    }

    /**
     * 通用上传请求（多个）
     *
     * @param files
     * @return
     */
    @Override
    public Result uploadFiles(List<MultipartFile> files) {
        try {
            // 上传文件路径
            String filePath = HealthConfig.getUploadPath();
            List<String> urls = new ArrayList<>();
            List<String> fileNames = new ArrayList<>();
            List<String> newFileNames = new ArrayList<>();
            List<String> originalFilenames = new ArrayList<>();
            for (MultipartFile file : files) {
                // 上传并返回新文件名称
                String fileName = FileUploadUtils.upload(filePath, file);
                String url = serverConfig.getUrl() + fileName;
                urls.add(url);
                fileNames.add(fileName);
                newFileNames.add(FileUtils.getName(fileName));
                originalFilenames.add(file.getOriginalFilename());
            }

            Map<String, Object> map = new HashMap<>();
            map.put("urls", StringUtils.join(urls, FILE_SEPARATOR));
            map.put("fileNames", StringUtils.join(fileNames, FILE_SEPARATOR));
            map.put("newFileNames", StringUtils.join(newFileNames, FILE_SEPARATOR));
            map.put("originalFilenames", StringUtils.join(originalFilenames, FILE_SEPARATOR));
            return Result.success(map);
        } catch (Exception e) {
            return Result.failure(ResultCode.SPECIFIED_UPLOAD_FILE_FAILURE.code(), ResultCode.SPECIFIED_UPLOAD_FILE_FAILURE.message());
        }
    }

    /**
     * 通用上传请求（单个）
     *
     * @param file
     * @return
     */
    @Override
    public Result uploadFile(MultipartFile file) {
        try {
            HashMap<String, Object> map = new HashMap<>();
            // 上传文件路径
            String filePath = HealthConfig.getUploadPath();
            // 上传并返回新文件名称
            String fileName = FileUploadUtils.upload(filePath, file);
            String url = serverConfig.getUrl() + fileName;
            //组装数据
            map.put("url", url);
            map.put("fileName", fileName);
            map.put("newFileName", FileUtils.getName(fileName));
            map.put("originalFilename", file.getOriginalFilename());

            return Result.success(map);
        } catch (Exception e) {
            return Result.failure(ResultCode.SPECIFIED_UPLOAD_FILE_FAILURE.code(), ResultCode.SPECIFIED_UPLOAD_FILE_FAILURE.message());
        }
    }

    /**
     * 通用下载请求
     *
     * @param fileName
     * @param delete
     * @param request
     * @param response
     */
    @Override
    public void fileDownload(String fileName, Boolean delete, HttpServletRequest request, HttpServletResponse response) {
        try {
            if (!FileUtils.checkAllowDownload(fileName)) {
                throw new Exception(StringUtils.format("文件名称({})非法，不允许下载。 ", fileName));
            }
            String realFileName = System.currentTimeMillis() + fileName.substring(fileName.indexOf("_") + 1);
            String filePath = HealthConfig.getDownloadPath() + fileName;

            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, realFileName);
            FileUtils.writeBytes(filePath, response.getOutputStream());
            if (delete) {
                FileUtils.deleteFile(filePath);
            }
        } catch (Exception e) {
            log.error("下载文件失败", e);
        }
    }
}
