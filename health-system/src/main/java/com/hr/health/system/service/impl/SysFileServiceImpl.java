package com.hr.health.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hr.health.common.config.HealthConfig;
import com.hr.health.common.constant.Constants;
import com.hr.health.common.constant.UploadConstants;
import com.hr.health.common.core.domain.Result;
import com.hr.health.common.enums.ResultCode;
import com.hr.health.common.exception.MicroServiceException;
import com.hr.health.common.exception.file.FileNameLengthLimitExceededException;
import com.hr.health.common.exception.file.FileSizeLimitExceededException;
import com.hr.health.common.exception.file.InvalidExtensionException;
import com.hr.health.common.utils.*;
import com.hr.health.common.utils.file.MimeTypeUtils;
import com.hr.health.common.utils.uuid.Seq;
import com.hr.health.system.config.ServerConfig;
import com.hr.health.system.domain.SysFileInfo;
import com.hr.health.system.mapper.SysFileMapper;
import com.hr.health.system.service.SysFileService;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, SysFileInfo> implements SysFileService {
    private static final Logger log = LoggerFactory.getLogger(SysFileServiceImpl.class);

    @Resource
    private SysFileMapper sysFileMapper;

    @Resource
    private ServerConfig serverConfig;


    @Value("${upload.maxSize}")
    private Integer maxSize;

    @Value("${upload.fileNameLength}")
    private Integer fileNameLength;

    /**
     * 获取新的文件名
     *
     * @param fileName
     * @return
     */
    private static String getNewFileName(String fileName) {
        return fileName.substring(fileName.lastIndexOf("/") + 1);
    }

    public static void main(String[] args) {
        System.out.println(File.separator);
        String newFileName = getNewFileName("/profile/upload/2022/09/28/美团打车行程单_20220928151413A006.pdf");
        System.out.println(newFileName);
    }

    /**
     * 创建文件的存储路径
     *
     * @param uploadDir
     * @param fileName
     * @return
     * @throws IOException
     */
    public static final File getAbsoluteFile(String uploadDir, String fileName) throws IOException {
        File desc = new File(uploadDir + File.separator + fileName);

        if (!desc.exists()) {
            if (!desc.getParentFile().exists()) {
                desc.getParentFile().mkdirs();
            }
        }
        return desc;
    }

    /**
     * 本地资源通用下载
     *
     * @param resource
     * @param request
     * @param response
     */
    @Override
    public void resourceDownload(String resource, HttpServletRequest request, HttpServletResponse response) {

    }

    /**
     * 文件上传请求（多个）
     *
     * @param files
     * @return
     */
    @Override
    public Result uploadMultiFiles(List<MultipartFile> files) {
        if (CollectionUtils.isEmpty(files)) {
            return Result.failure(ResultCode.PARAM_IS_BLANK.code(), ResultCode.PARAM_IS_BLANK.message());
        }
        // 获取上传文件路径
        String filePath = HealthConfig.getUploadPath();
        if (files.size() > 1) { //多个文件上传
            List<SysFileInfo> sysFileInfoList = new ArrayList<>();
            for (MultipartFile file : files) {
                SysFileInfo sysFileInfo = processSingleFile(filePath, file);
                sysFileInfoList.add(sysFileInfo);
            }
            //插入数据
            this.saveBatch(sysFileInfoList);
            //返回
            return Result.success(sysFileInfoList);
        } else {// 单个文件上传
            MultipartFile file = files.get(0);
            //处理单个文件
            SysFileInfo sysFileInfo = processSingleFile(filePath, file);
            // 插入数据
            sysFileMapper.insert(sysFileInfo);
            return Result.success(sysFileInfo);
        }
    }

    /**
     * 处理单个文件
     *
     * @param filePath
     * @param file
     */
    private SysFileInfo processSingleFile(String filePath, MultipartFile file) {
        //创建文件信息对象
        SysFileInfo sysFileInfo = new SysFileInfo();
        try {
            //获取文件名
            Map<String, String> uploadMap = this.upload(filePath, file);
            //后缀
            sysFileInfo.setFileSuffix(uploadMap.get(UploadConstants.SUFFIX));
            //存储路径
            sysFileInfo.setStorageFilePath(uploadMap.get(UploadConstants.ABS_PATH));
            //下载路径
            sysFileInfo.setDownloadPath(uploadMap.get(UploadConstants.DOWNLOAD_PATH));
            //访问路径
            sysFileInfo.setAccessUrl(serverConfig.getUrl() + uploadMap.get(UploadConstants.DOWNLOAD_PATH));
            //原始文件名称
            sysFileInfo.setOriginalFileName(uploadMap.get(UploadConstants.ORIGINAL_FILE_NAME));
            //新的文件名称
            sysFileInfo.setNewFileName(uploadMap.get(UploadConstants.NEW_FILE_NAME));
            //文件大小
            sysFileInfo.setFileSize(uploadMap.get(UploadConstants.FILE_SIZE));
            //设置创建人
            sysFileInfo.setCreateBy(SecurityUtils.getUserId());
            //创建时间
            sysFileInfo.setCreateTime(LocalDateTime.now());
            //更新时间
            sysFileInfo.setUpdateTime(LocalDateTime.now());
        } catch (IOException e) {
            log.error("文件上传失败", e);
            e.printStackTrace();
            throw new MicroServiceException(ResultCode.SPECIFIED_UPLOAD_FILE_FAILURE.code(), ResultCode.SPECIFIED_UPLOAD_FILE_FAILURE.message());
        }

        return sysFileInfo;
    }

    /**
     * 根据文件路径上传
     *
     * @param baseDir 相对应用的基目录
     * @param file    上传的文件
     * @return 文件名称
     * @throws IOException
     */
    public Map<String, String> upload(String baseDir, MultipartFile file) throws IOException {
        try {
            return upload(baseDir, file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * 文件上传
     *
     * @param baseDir          相对应用的基目录
     * @param file             上传的文件
     * @param allowedExtension 上传文件类型
     * @return 返回上传成功的文件名
     * @throws FileSizeLimitExceededException       如果超出最大大小
     * @throws FileNameLengthLimitExceededException 文件名太长
     * @throws IOException                          比如读写文件出错时
     * @throws InvalidExtensionException            文件校验异常
     */
    public Map<String, String> upload(String baseDir, MultipartFile file, String[] allowedExtension) throws Exception {
        int fileNameLength = Objects.requireNonNull(file.getOriginalFilename()).length();
        //校验文件民称长度
        if (fileNameLength > this.fileNameLength) {
            throw new FileNameLengthLimitExceededException(this.fileNameLength);
        }

        //文件大小校验
        Map<String, String> basicFileMap = checkFileSize(file, allowedExtension);
        // 重新编码文件名称
        String fileName = this.extractFilename(file);

        // 获取新的文件名
        String newFileName = getNewFileName(fileName);
        // 获取存储文件的绝对路径
        String absPath = getAbsoluteFile(baseDir, fileName).getAbsolutePath();
        // 写入数据
        file.transferTo(Paths.get(absPath));
        // 下载路径
        String downloadPath = this.getPathFileName(baseDir, fileName);
        //相应数据
        basicFileMap.put(UploadConstants.NEW_FILE_NAME, newFileName);
        basicFileMap.put(UploadConstants.ABS_PATH, absPath);
        basicFileMap.put(UploadConstants.DOWNLOAD_PATH, downloadPath);
        return basicFileMap;
    }

    /**
     * @param uploadDir
     * @param fileName
     * @return
     * @throws IOException
     */
    public String getPathFileName(String uploadDir, String fileName) throws IOException {
        int dirLastIndex = HealthConfig.getProfile().length() + 1;
        String currentDir = StringUtils.substring(uploadDir, dirLastIndex);
        return Constants.RESOURCE_PREFIX + "/" + currentDir + "/" + fileName;
    }

    /**
     * 重新编码文件名
     */
    public String extractFilename(MultipartFile file) {
        return StringUtils.format("{}/{}_{}.{}", DateUtils.datePath(), FilenameUtils.getBaseName(file.getOriginalFilename()), Seq.getId(Seq.uploadSeqType), getExtension(file));
    }


    /**
     * 文件大小校验
     *
     * @param file 上传的文件
     * @return
     * @throws FileSizeLimitExceededException 如果超出最大大小
     * @throws InvalidExtensionException
     */
    public Map<String, String> checkFileSize(MultipartFile file, String[] allowedExtension) throws FileSizeLimitExceededException, InvalidExtensionException {
        //定义返回数据格式
        Map<String, String> map = new HashMap<>();

        //获取文件大小
        long size = file.getSize();
        // 获取文件限制上传的大小
        long maxFileLimitSize = this.maxSize * 1024 * 1024;
        //校验文件大小
        if (size > maxFileLimitSize) {
            throw new FileSizeLimitExceededException(this.maxSize, FileUnit.M);
        }
        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        // 获取后缀名
        String suffix = this.getExtension(file);

        //校验文件格式
        if (allowedExtension != null && !this.isAllowedExtension(suffix, allowedExtension)) {
            if (allowedExtension == MimeTypeUtils.IMAGE_EXTENSION) {
                throw new InvalidExtensionException.InvalidImageExtensionException(allowedExtension, suffix, originalFilename);
            } else if (allowedExtension == MimeTypeUtils.FLASH_EXTENSION) {
                throw new InvalidExtensionException.InvalidFlashExtensionException(allowedExtension, suffix, originalFilename);
            } else if (allowedExtension == MimeTypeUtils.MEDIA_EXTENSION) {
                throw new InvalidExtensionException.InvalidMediaExtensionException(allowedExtension, suffix, originalFilename);
            } else if (allowedExtension == MimeTypeUtils.VIDEO_EXTENSION) {
                throw new InvalidExtensionException.InvalidVideoExtensionException(allowedExtension, suffix, originalFilename);
            } else {
                throw new InvalidExtensionException(allowedExtension, suffix, originalFilename);
            }
        }

        //响应数据
        map.put(UploadConstants.ORIGINAL_FILE_NAME, originalFilename);
        map.put(UploadConstants.SUFFIX, suffix);
        map.put(UploadConstants.FILE_SIZE, String.valueOf(size));
        return map;
    }

    /**
     * 获取文件名的后缀
     *
     * @param file 表单文件
     * @return 后缀名
     */
    public String getExtension(MultipartFile file) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (StringUtils.isEmpty(extension)) {
            extension = MimeTypeUtils.getExtension(Objects.requireNonNull(file.getContentType()));
        }
        return extension;
    }


    /**
     * 判断MIME类型是否是允许的MIME类型
     *
     * @param extension
     * @param allowedExtension
     * @return
     */
    public boolean isAllowedExtension(String extension, String[] allowedExtension) {
        for (String str : allowedExtension) {
            if (str.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 查询单个
     *
     * @param fileId
     * @return
     */
    @Override
    public SysFileInfo queryById(Long fileId) {
        return sysFileMapper.selectById(fileId);
    }

    /**
     * 删除
     *
     * @param fileId
     * @return
     */
    @Override
    public int remove(Long fileId) {
        return sysFileMapper.deleteById(fileId);
    }

    /**
     * 编辑
     *
     * @param sysFileInfo
     * @return
     */
    @Override
    public int edit(SysFileInfo sysFileInfo) {
        sysFileInfo.setUpdateTime(LocalDateTime.now());
        return sysFileMapper.updateById(sysFileInfo);
    }

    /**
     * 添加
     *
     * @param sysFileInfo
     * @return
     */
    @Override
    public int add(SysFileInfo sysFileInfo) {
        sysFileInfo.setCreateBy(SecurityUtils.getUserId());
        sysFileInfo.setCreateTime(LocalDateTime.now());
        sysFileInfo.setUpdateTime(LocalDateTime.now());
        return sysFileMapper.insert(sysFileInfo);
    }

    /**
     * 获取文件列表
     *
     * @return
     */
    @Override
    public IPage<SysFileInfo> list(SysFileInfo sysFileInfo) {
        String fileName = sysFileInfo.getOriginalFileName();

        QueryWrapper<SysFileInfo> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(fileName)) {
            queryWrapper.like(SysFileInfo.ORIGINAL_FILE_NAME, fileName);
        }

        //获取当前页码和每页记录数
        Page<SysFileInfo> sysFileInfoPage = new Page<>(GetPageParamUtils.getPageNum(), GetPageParamUtils.getPageSize());

        //查询
        return sysFileMapper.selectPage(sysFileInfoPage, queryWrapper);
    }
}
