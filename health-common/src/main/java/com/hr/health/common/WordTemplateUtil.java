package com.hr.health.common;

import freemarker.template.*;
import org.springframework.core.io.ClassPathResource;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public class WordTemplateUtil {
    public static Logger log = Logger.getLogger(WordTemplateUtil.class.toString());

    /**
     * 创建doc文档
     *
     * @param response
     */
    public static void createDoc(HttpServletResponse response, Map<String, Object> dataMap) {
        try {
            //初始化配置
            Template template = initConfiguration();
            //设置文件名
            String filename = (String) dataMap.get("fileName");
            //设置响应参数
            setResponseParam(response, filename);
            //填充数据，生成Word文档
            template.process(dataMap, response.getWriter());
        } catch (TemplateException e) {
            log.info("填充数据错误");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 设置响应参数
     */
    private static void setResponseParam(HttpServletResponse response, String fileName) throws IOException {
        // 告诉浏览器用什么软件可以打开此文件
        response.setHeader("content-Type", "application/msword");
        response.setContentType("application/octet-stream");

        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        response.setCharacterEncoding("utf-8");//处理乱码问题
        response.flushBuffer();

    }

    /**
     * 初始化FreeMark配置
     */
    private static Template initConfiguration() throws IOException {
        //创建配置实例对象
        Configuration configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        //设置编码
        configuration.setDefaultEncoding("UTF-8");
        //加载需要装填的模板
        ClassPathResource classPathResource = new ClassPathResource("/templates");
        configuration.setDirectoryForTemplateLoading(classPathResource.getFile());

        //设置对象包装器
        configuration.setObjectWrapper(new DefaultObjectWrapper(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS));

        //设置异常处理器
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
        //获取ftl模板对象
        Template template = configuration.getTemplate("success.ftl", "utf-8");
//        Template template = configuration.getTemplate("success_2003.ftl", "utf-8");

        return template;
    }

    /**
     * 本地图片转换Base64的方法
     *
     * @param imgPath
     */
    public static String imageToBase64(String imgPath) {
        byte[] data = null;
        // 读取图片字节数组
        try {
            File file = new File(imgPath);
            InputStream in = new FileInputStream(file);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        // 返回Base64编码过的字节数组字符串
        return encoder.encode(Objects.requireNonNull(data));
    }


}
