package com.hr.health.common.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 压缩工具类
 */
public class CompressUtil {

    /**
     * 生成zip压缩文件
     * @param filePaths
     * @param zipFilePath
     * @param keepDirStructure
     */
    public static void compress(List<Map<String, String>> filePaths, String zipFilePath, Boolean keepDirStructure) {
        byte[] buf = new byte[1024];
        File zipFile = new File(zipFilePath);
        try {
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
            for (int i = 0; i < filePaths.size(); i++) {
                String relativePath = filePaths.get(i).get("filePath");
                String relativeName = filePaths.get(i).get("fileName");
                if (StringUtils.isEmpty(relativePath)) {
                    continue;
                }
                File sourceFile = new File(relativePath);
                if (sourceFile == null || !sourceFile.exists()) {
                    continue;
                }
                FileInputStream fis = new FileInputStream(sourceFile);
                if (keepDirStructure != null && keepDirStructure) {
                    zos.putNextEntry(new ZipEntry(relativePath));
                } else {
                    zos.putNextEntry(new ZipEntry(i + "_" + relativeName));
                }
                int len;
                while ((len = fis.read(buf)) > 0) {
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                // zos.close();
            }
            zos.close();
            if (!zipFile.exists()) {
                zipFile.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载zip
     *
     * @param response
     * @param zipName  浏览器header中zip名称
     * @param zipFile  zipFile文件
     */
    public static void downloadZip(HttpServletResponse response, String zipName, File zipFile) {
        //下载文件
        try {
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment;FileName=" + URLEncoder.encode(zipName,"UTF-8"));
            ServletOutputStream out = response.getOutputStream();
            int len = 0;
            byte[] buffer = new byte[1024];
            FileInputStream fis = new FileInputStream(zipFile);
            while ((len = fis.read(buffer)) > 0) {
                out.write(buffer, 0, len);
                out.flush();
            }
            out.close();
            fis.close();
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

