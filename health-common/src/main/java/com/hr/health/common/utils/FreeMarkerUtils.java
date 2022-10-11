package com.hr.health.common.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.*;

/**
 * .docx文档使用
 */
public class FreeMarkerUtils {
    private static String charset = "UTF-8";
    private static int bufferCapacity = 1048576;

    /**
     * 返回Configuration配置
     * @param templatePath
     * @return
     */
    public static Configuration getConfiguration(String templatePath) {
        //创建配置实例
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_30);
        //设置编码
        configuration.setDefaultEncoding(charset);
        configuration.setClassForTemplateLoading(FreeMarkerUtils.class, templatePath);
        return configuration;
    }

    /**
     * 返回模板数据输出流
     * @param templatePath
     * @param templateName
     * @param data
     * @return
     */
    public static ByteArrayOutputStream getFreemarkerContentOutputStream(String templatePath, String templateName, Object data) {
        ByteArrayOutputStream bos = null;
        try {
            //获取模板
            Template template = getConfiguration(templatePath).getTemplate(templateName);
            bos = new ByteArrayOutputStream(bufferCapacity);
            Writer out = new BufferedWriter(new OutputStreamWriter(bos, charset));
            //生成文件
            template.process(data, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bos;
    }

    /**
     * 返回模板数据输入流
     * @param templatePath
     * @param templateName
     * @param data
     * @return
     */
    public static ByteArrayInputStream getFreemarkerContentInputStream(String templatePath, String templateName, Object data) {
        ByteArrayInputStream bis = null;
        try {
            ByteArrayOutputStream bos = getFreemarkerContentOutputStream(templatePath, templateName, data);
            bis = new ByteArrayInputStream(bos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bis;
    }

    /**
     * 返回模板数据字节
     * @param templatePath
     * @param templateName
     * @param data
     * @return
     */
    public static byte[] getFreemarkerContentBytes(String templatePath, String templateName, Object data) {
        try {
            ByteArrayOutputStream bos = getFreemarkerContentOutputStream(templatePath, templateName, data);
            return bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
