package com.hr.health.system.service.impl;


import com.alibaba.fastjson2.JSON;
import com.hr.health.system.service.BackupSqlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.FutureTask;

@Service
@Slf4j
public class BackupSqlServiceImpl implements BackupSqlService {

    public static final String MYSQL_USER_NAME = "root";
    public static final String MYSQL_PASSWORD = "root";
    public static final String MYSQL_HOST = "127.0.0.1";
    public static final String MYSQL_DATA_BASENAME = "health";
    public static final String MYSQL_URL = "d:\\health.sql";

    /**
     * mysql的还原方法
     *
     * @param command 命令行
     * @return
     */
    public static boolean recover(String[] command) {
        for (int i = 0; i < command.length; i++) {
            log.info("恢复命令：" + command[i]);
        }
        Runtime runtime = Runtime.getRuntime();
        try {
            log.info("开始恢复数据库中...  command: " + JSON.toJSONString(command));
            Process process = runtime.exec(command);
            log.info("数据库恢复结束...");
            // 新增一个线程用于处理错误流,如果有错误就返回true，没有就返回false
            ErrorStreamUtil errThread = new ErrorStreamUtil(process);
            FutureTask<Boolean> result = new FutureTask<Boolean>(errThread);
            new Thread(result).start();
            if (result.get()) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Mysql恢复出错: {}", e);
            return false;
        }
    }

    /**
     * 恢复数据库语句
     *
     * @return
     */
    private static String[] getCommand(String userName, String psw, String ip, String db, String url) {
        String[] cmd = new String[3];
        String os = System.getProperties().getProperty("os.name");
        if (os.startsWith("Win")) {
            cmd[0] = "cmd.exe";
            cmd[1] = "/c";
        } else {
            cmd[0] = "/bin/sh";
            cmd[1] = "-c";
        }
        //拼接命令
        String arg = "mysql " +
                "-h" + ip + " " +
                "-u" + userName + " " +
                "-p" + psw + " " +
                db + " < " +
                url;
        cmd[2] = arg;
        return cmd;
    }

    /**
     * 恢复数据
     *
     * @return
     */
    @Override
    public boolean recoverBackupData() {
        String[] recoverBackUpCmd = getCommand(MYSQL_USER_NAME, MYSQL_PASSWORD, MYSQL_HOST, MYSQL_DATA_BASENAME, MYSQL_URL);
        return recover(recoverBackUpCmd);
    }

    /**
     * 备份方法
     *
     * @throws IOException
     */
    public boolean backupData() {

        //获取备份sql参数
        String[] command = this.getBackUpCommand(MYSQL_HOST, MYSQL_USER_NAME, MYSQL_PASSWORD, MYSQL_DATA_BASENAME, MYSQL_URL);

        log.info("备份文件地址：" + MYSQL_URL);
        for (int i = 0; i < command.length; i++) {
            log.info("备份命令：" + command[i]);
        }
        final Process process;
        try {
            /**
             *  ##############特别关注的点#############
             *
             *  创建一个线程用户获取错误流，并且这个错误流是有返回结果的，所以用的是Callable来实现
             */
            //-u后面是用户名，-p是密码-p后面最好不要有空格，-test是数据库的名字
            Runtime runtime = Runtime.getRuntime();
            log.info("开始备份数据库中...  command: " + JSON.toJSONString(command));
            process = runtime.exec(command);
            log.info("数据库备份结束...");
            // 新增一个线程用于处理错误流,如果有错误就返回true，没有就返回false
            ErrorStreamUtil errThread = new ErrorStreamUtil(process);
            FutureTask<Boolean> result = new FutureTask<Boolean>(errThread);
            new Thread(result).start();
            if (result.get()) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 备份数据库语句
     *
     * @param hostIP
     * @param userName
     * @param password
     * @param databaseName
     * @return
     */
    private String[] getBackUpCommand(String hostIP, String userName, String password, String databaseName, String url) {
        String[] cmd = new String[3];
        String os = System.getProperties().getProperty("os.name");
        if (os.startsWith("Win")) {
            cmd[0] = "cmd.exe";
            cmd[1] = "/c";
        } else {
            cmd[0] = "/bin/sh";
            cmd[1] = "-c";
        }
        //拼接命令
        String stringBuilder = "mysqldump" + " -h" + hostIP +
                " -u " + userName + " -p" + password +
                " --databases " + databaseName +
                " > " + url;
        cmd[2] = stringBuilder;
        return cmd;
    }
}
