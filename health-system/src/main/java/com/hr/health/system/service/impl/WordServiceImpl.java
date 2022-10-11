package com.hr.health.system.service.impl;

import com.hr.health.common.WordTemplateUtil;
import com.hr.health.common.enums.ResultCode;
import com.hr.health.common.exception.MicroServiceException;
import com.hr.health.common.utils.FreeMarkerUtils;
import com.hr.health.common.utils.StringUtils;
import com.hr.health.common.utils.file.FileUtils;
import com.hr.health.system.domain.People;
import com.hr.health.system.domain.word.Picture;
import com.hr.health.system.domain.word.Report;
import com.hr.health.system.domain.word.Word;
import com.hr.health.system.service.WordService;
import com.hr.health.system.utils.word.DataUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
public class WordServiceImpl implements WordService {


    /**
     * docx文档下载更新
     *
     * @param map
     * @param response
     */
    @Override
    public void downloadWordByDocx(Map<String, MultipartFile> map, HttpServletResponse response) {
        //定义输出文件名
        String outFileName = "test.docx";
        Word word = this.getWord(outFileName);

        Map<String, File> fileMap = new HashMap<>();

//        fileMap.put("0", new File("D:\\pic\\aaaa.jpeg"));
//        fileMap.put("1", new File("D:\\pic\\bbbb.jpeg"));

        fileMap.put("0", new File("D:\\pic\\哈哈.jpeg"));
        fileMap.put("1", new File("D:\\pic\\嘻嘻.jpeg"));
        //TODO 设置用户数据 暂时是假数据
        word.setData(DataUtils.getData(fileMap));

        //生成数据
        this.createWordDocxAndDownload(response, word, outFileName);
    }

    /**
     * 生成下载数据
     *
     * @param response
     * @param word
     */
    private void createWordDocxAndDownload(HttpServletResponse response, Word word, String outFileName) {
        try {
            //设置response参数
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, outFileName);
            //获取输出流
            OutputStream os = response.getOutputStream();
            //获取模板数据
            ByteArrayOutputStream bos = FreeMarkerUtils.getFreemarkerContentOutputStream(word.getTemplatePath(), word.getTemplateName(), word.getData());
            if (bos != null) {
                ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
                //获取模板路径名称
                String templateZipName = word.getTemplatePath() + File.separator + word.getTemplateZipName();
                //加载模板资源
                Resource resource = new ClassPathResource(templateZipName);
                //获取压缩包输入流
                ZipInputStream zis = new ZipInputStream(resource.getInputStream());
                //将压缩包输出流写入到response输出流中
                ZipOutputStream zos = new ZipOutputStream(os, StandardCharsets.UTF_8);
                int len = -1;
                byte[] buffer = new byte[1024];
                //开始处理图片
                StringBuilder picRels = new StringBuilder();
                Report report = (Report) word.getData();
                //获取图片信息
                Map<String, Picture> pictureMap = report.getPictureMap();
                if (!CollectionUtils.isEmpty(pictureMap)) {
                    //循环所有的key
                    for (String pictureName : pictureMap.keySet()) {
                        //获取所有的value
                        Picture picture = pictureMap.get(pictureName);
                        //获取文件的输入流，写入到压缩包中
                        InputStream picis = new ByteArrayInputStream(picture.getFos().toByteArray());
                        //设置文件注释
                        zos.setComment(picture.getFileName());
                        //写入数据到压缩中对应的文件夹
                        zos.putNextEntry(new ZipEntry("word/media/" + picture.getFileName()));
                        while ((len = picis.read(buffer)) != -1) {
                            zos.write(buffer, 0, len);
                        }
                        picis.close();
                        zos.closeEntry();
                        //创建图片与内容的关系配置
                        picRels.append("<Relationship Id=\"").append(picture.getPictureName()).append("\" ").append("Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/image\" ").append("Target=\"media/").append(picture.getFileName()).append("\"/>\n");
                    }
                }
                //开始覆盖文档------------------
                ZipEntry zipEntry = null;
                while ((zipEntry = zis.getNextEntry()) != null) {
                    zos.putNextEntry(new ZipEntry(zipEntry.getName()));
                    //如果是word/document.xml由程序输入
                    if ("word/document.xml".equals(zipEntry.getName())) {
                        while ((len = bis.read(buffer)) != -1) {
                            zos.write(buffer, 0, len);
                        }
                        bis.close();
                    } else if ("word/_rels/document.xml.rels".equals(zipEntry.getName())) {
                        //图片导出 rels 引用设置，其实是 xml 文件
                        //按照文本方式处理
                        if (!StringUtils.isEmpty(picRels)) {
                            ByteArrayOutputStream picos = new ByteArrayOutputStream();
                            while ((len = zis.read(buffer)) != -1) {
                                picos.write(buffer, 0, len);
                            }
                            picos.close();
                            String text = new String(picos.toByteArray());
                            text = text.replace("</Relationships>", picRels + "</Relationships>");
                            zos.write(text.getBytes());
                        }
                    } else {
                        while ((len = zis.read(buffer)) != -1) {
                            zos.write(buffer, 0, len);
                        }
                    }
                    zis.closeEntry();
                }
                //关闭流
                zis.close();
                zos.close();
                os.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new MicroServiceException(ResultCode.SPECIFIED_ZIP_WRITE_ERROR.code(), ResultCode.SPECIFIED_ZIP_WRITE_ERROR.message());
        }

    }


    /**
     * 填充数据
     *
     * @param fileName
     * @return
     */
    private Word getWord(String fileName) {
        //获取后缀名
        String extName = fileName.substring(fileName.lastIndexOf(".") + 1);
        //创建对象
        Word word = new Word();
        word.setFileExt(extName);
        word.setTemplatePath("/templates/docx");
        word.setTemplateName("docx.ftl");
        word.setTemplateZipName("docx.zip");
        word.setDocName(fileName);
        return word;
    }

    /**
     * doc文档下载
     *
     * @param request
     * @param response
     */
    @Override
    public void downloadWord(HttpServletRequest request, HttpServletResponse response) {

        Map<String, Object> dataMap = new HashMap<>();
        List<People> students = new ArrayList<>();

        People student1 = new People();
        student1.setName("张三1");
        student1.setAge(19);
        student1.setCard("s0011");
        People student2 = new People();
        student2.setName("张三2");
        student2.setAge(19);
        student2.setCard("s0012");
        People student3 = new People();
        student3.setName("张三3");
        student3.setAge(19);
        student3.setCard("s0013");

        students.add(student1);
        students.add(student2);
        students.add(student3);
        dataMap.put("students", students);
        dataMap.put("title", "我是测试标题");

//        String base64 = WordTemplateUtil.imageToBase64("D:\\pic\\a.jpeg");
        String base64 = WordTemplateUtil.imageToBase64("D:\\pic\\哈哈.jpeg");
        String image = base64;
        //输出文档
        String fileName = new String("啦啦啦啦.doc");
        dataMap.put("fileName", fileName);

        dataMap.put("image", image);
        //创建doc文件
        WordTemplateUtil.createDoc(response, dataMap);
    }
}
