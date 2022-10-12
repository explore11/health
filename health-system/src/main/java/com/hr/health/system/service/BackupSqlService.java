package com.hr.health.system.service;

public interface BackupSqlService {
    /**
     * 备份数据
     *
     * @return
     */
    boolean backupData();

    /**
     * 恢复数据
     * @return
     */
    boolean recoverBackupData();


}
