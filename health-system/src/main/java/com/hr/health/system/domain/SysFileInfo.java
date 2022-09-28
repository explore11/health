package com.hr.health.system.domain;


import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础文件上传
 */
@TableName("sys_file_info")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysFileInfo implements Serializable {
    public static final String ORIGINAL_FILE_NAME = "original_file_name";
    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 原始文件名称
     */
    @TableField("original_file_name")
    private String originalFileName;
    /**
     * 新文件名称
     */
    @TableField("new_file_name")
    private String newFileName;
    /**
     * 下载路径
     */
    @TableField("download_path")
    private String downloadPath;
    /**
     * 存储的文件路径
     */
    @TableField("storage_file_path")
    private String storageFilePath;
    /**
     * 访问路径
     */
    @TableField("access_url")
    private String accessUrl;
    /**
     * 文件后缀
     */
    @TableField("file_suffix")
    private String fileSuffix;
    /**
     * 文件大小
     */
    @TableField("file_size")
    private String fileSize;
    /**
     * 创建人
     */
    @TableField("create_by")
    private Long createBy;
    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
    /**
     * 数据有效性
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
