package com.hr.health.web.controller.common;

import com.hr.health.common.core.domain.Result;
import com.hr.health.system.service.BackupSqlService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 通用请求处理
 *
 * @author swq
 */
@Api(tags = "数据备份")
@RestController
@RequestMapping("/common/backup")
public class BackupSqlController {
    @Resource
    private BackupSqlService backupSqlService;


    /**
     * 备份数据
     *
     * @return
     */
    @PreAuthorize("@ss.hasPermi('common:backup:backupData')")
    @ApiOperation("备份数据")
    @GetMapping("/backupData")
    public Result backupData() {
        boolean flag = backupSqlService.backupData();
        return Result.success(flag);
    }

    /**
     * 恢复数据
     *
     * @return
     */
    @PreAuthorize("@ss.hasPermi('common:backup:recoverBackupData')")
    @ApiOperation("恢复数据")
    @GetMapping("/recoverBackupData")
    public Result recoverBackupData() {
        boolean flag = backupSqlService.recoverBackupData();
        return Result.success(flag);
    }

}
