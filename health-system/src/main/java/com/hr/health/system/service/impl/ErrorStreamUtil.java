package com.hr.health.system.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

public class ErrorStreamUtil implements Callable<Boolean> {

    private final static Logger logger = LoggerFactory.getLogger(ErrorStreamUtil.class);

    private Process process;

    public ErrorStreamUtil (Process inputStream) {
        this.process = inputStream;
    }

    @Override
    public Boolean call() throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), "utf-8"));
        String s = null;
        StringBuffer errorSb = new StringBuffer();
        boolean flag = true;
        // 备份过程中出现错误就直接返回
        while ((s = bufferedReader.readLine()) != null) {
            logger.error("数据库备份/恢复错误流信息：" + s);
            // mysql由于对明文展示密码来执行备份会写出warning警告，我们用flag做标识来排除第一行内容
            if (flag) {
                flag = false;
            } else {
                errorSb.append(s);
            }
        }
        int ret = process.waitFor();
        if (ret == 0) {
            if (errorSb.length() > 0) {
                String error = errorSb.toString();
                logger.error("cmd命令出现错误，请仔细核对语句: " + error);
                bufferedReader.close();
                return true;
            }
            bufferedReader.close();
            return false;
        }
        return true;
    }
}