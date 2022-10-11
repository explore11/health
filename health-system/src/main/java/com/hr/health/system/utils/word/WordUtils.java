package com.hr.health.system.utils.word;


import com.hr.health.common.utils.FreeMarkerUtils;
import com.hr.health.system.domain.word.Picture;
import com.hr.health.system.domain.word.Report;
import com.hr.health.system.domain.word.Word;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


/**
 * @author Administrator
 * word文档生成工具类
 */
public class WordUtils {
    private static int bufferSize = 1024;

    /**
     * 获取生成 word(doc,docx) 文件的 outputstream
     *
     * @param word
     * @return
     */
    public static ByteArrayOutputStream getWordDocOutputStream(Word word) {
        try {
            ByteArrayOutputStream bos = FreeMarkerUtils.getFreemarkerContentOutputStream(word.getTemplatePath(), word.getTemplateName(), word.getData());
            if ("doc".equals(word.getFileExt())) {
                return bos;
            }
            if (bos != null) {
                ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
                String templateZipName = word.getTemplatePath() + "/" + word.getTemplateZipName();
                Resource resource = new ClassPathResource(templateZipName);
                ZipInputStream zis = new ZipInputStream(resource.getInputStream());
                //压缩 docx 文件
                ByteArrayOutputStream zbos = new ByteArrayOutputStream();
                ZipOutputStream zos = new ZipOutputStream(zbos);
                int len = -1;
                byte[] buffer = new byte[bufferSize];
                //开始处理图片
                String picRels = "";
                Report report = (Report) word.getData();
                Map<String, Picture> pictureMap = report.getPictureMap();
                if (pictureMap != null && pictureMap.size() > 0) {
                    for (String pictureName : pictureMap.keySet()) {
                        Picture picture = pictureMap.get(pictureName);
                        InputStream picis = new ByteArrayInputStream(picture.getFos().toByteArray());
                        zos.setComment(picture.getFileName());
                        zos.putNextEntry(new ZipEntry("word/media/" + picture.getFileName()));
                        while ((len = picis.read(buffer)) != -1) {
                            zos.write(buffer, 0, len);
                        }
                        picis.close();
                        zos.closeEntry();
                        picRels = picRels +
                                "<Relationship Id=\"" + picture.getPictureName() + "\" " +
                                "Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/image\" " +
                                "Target=\"media/" + picture.getFileName() + "\"/>\n";
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
                        if (picRels != null && !"".equals(picRels)) {
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
                zis.close();
                zos.close();
                zbos.close();
                return zbos;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建 word(doc,docx) 文件
     *
     * @param word
     */
    public static void createWordDoc(Word word) {
        try {
            File docFile = new File(word.getDocName());
            FileOutputStream fos = new FileOutputStream(docFile);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bos.write(getWordDocOutputStream(word).toByteArray());
            bos.flush();
            bos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取 word(doc,docx) 文件中的表格
     *
     * @param word
     */
    public static void readWordDocTable(Word word) {
        try {
            Map<String, Object> dataMap = new HashMap<>();
            File docFile = new File(word.getDocName());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            FileInputStream fis = new FileInputStream(docFile);
            int len = -1;
            byte[] buffer = new byte[bufferSize];
            while ((len = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            fis.close();
            bos.close();
            InputStream bis = new ByteArrayInputStream(bos.toByteArray());
            if ("doc".equals(word.getFileExt())) {
                getDataFromDoc(bis, dataMap);
            } else {
                getDataFromDocx(bis, dataMap);
            }
            bis.close();
            System.out.println(dataMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getDataFromDocx(InputStream is, Map<String, Object> data) throws Exception {
        XWPFDocument document = new XWPFDocument(is);
        List<XWPFTable> tables = document.getTables();
        XWPFTable t = tables.get(0);
        int idx = t.getRow(0).getCell(0).getText().lastIndexOf("：");
        String date = t.getRow(0).getCell(0).getText().substring(idx + 1);
        String name = t.getRow(1).getCell(1).getText();
        String sex = t.getRow(1).getCell(3).getText();
        String political = t.getRow(1).getCell(5).getText();
        String org = t.getRow(2).getCell(1).getText();
        String station = t.getRow(2).getCell(3).getText();
        String work = t.getRow(3).getCell(1).getText();
        data.put("date", date);
        data.put("name", name);
        data.put("sex", sex);
        data.put("political", political);
        data.put("org", org);
        data.put("station", station);
        data.put("work", work);
    }

    private static void getDataFromDoc(InputStream is, Map<String, Object> data) throws Exception {
        HWPFDocument document = new HWPFDocument(is);
        Range range = document.getRange();
        TableIterator iterator = new TableIterator(range);
        List<String> textList = new ArrayList<>();
        while (iterator.hasNext()) {
            Table table = iterator.next();
            for (int i = 0; i < table.numRows(); i++) {
                TableRow row = table.getRow(i);
                for (int j = 0; j < row.numCells(); j++) {
                    TableCell cell = row.getCell(j);
                    StringBuffer buffer = new StringBuffer();
                    int numParagraphs = cell.numParagraphs();
                    for (int k = 0; k < numParagraphs; k++) {
                        Paragraph paragraph = cell.getParagraph(k);
                        String text = paragraph.text();
                        if (text != null && !"".equals(text)) {
                            text = text.substring(0, text.length() - 1);
                        }
                        text = text.trim();
                        //WARNING 空行跳过，根据实际业务需要来取舍是否需要跳过空行
                        if ("".equals(text)) {
                            continue;
                        }
                        buffer.append(text);
                        if (numParagraphs > 1 && k != (numParagraphs - 1)) {
                            buffer.append("\n");
                        }
                    }
                    textList.add(buffer.toString());
                }
            }
        }
        for (int i = 0; i < textList.size(); i++) {
            String text = textList.get(i);
            if (text.contains("填表日期")) {
                int idx = text.lastIndexOf("：");
                String date = text.substring(idx + 1);
                data.put("date", date);
            }
            if (text.equals("姓名")) {
                data.put("name", textList.get(i + 1));
            }
            if (text.equals("性别")) {
                data.put("sex", textList.get(i + 1));
            }
            if (text.equals("政治面貌")) {
                data.put("political", textList.get(i + 1));
            }
            if (text.equals("所在部门")) {
                data.put("org", textList.get(i + 1));
            }
            if (text.equals("岗位/职务")) {
                data.put("station", textList.get(i + 1));
            }
            if (text.contains("年度") && text.contains("工作") && text.contains("总结")) {
                data.put("work", textList.get(i + 1));
            }
        }
    }

}
