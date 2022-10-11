package com.hr.health.system.utils.word;


import com.hr.health.system.domain.word.Picture;
import com.hr.health.system.domain.word.Report;
import com.hr.health.system.domain.word.Score;
import com.hr.health.system.domain.word.Word;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

public class DataUtils {

    public static Report getData(Map<String, File> map) {
        int capacity = 10;

        Report report = new Report();
        report.setUserName("张三丰");
        report.setSex("男");
        report.setNation("蒙古族");

        Map<String, Picture> pictureMap = new HashMap<>();
        Picture header = getFileToPicture(map.get("0"));
        Picture image = getFileToPicture(map.get("1"));
        Picture image2 = getFileToPicture(map.get("2"));

        report.setHeader(header.getPictureName());
        report.setImage(image.getPictureName());

        pictureMap.put(report.getHeader(), header);
        pictureMap.put(report.getImage(), image);
        pictureMap.put(image2.getPictureName(),image2);

        report.setPictureMap(pictureMap);

        List<String> picList =new ArrayList<>();
        picList.add(header.getPictureName());
        picList.add(image.getPictureName());
        picList.add(image2.getPictureName());
        report.setPicList(picList);


        List<Score> scoreList = new ArrayList<>(capacity);

        Score score = new Score();
        score.setSort("1");
        score.setSubject("语文");
        score.setScore("90");
        scoreList.add(0, score);

        score = new Score();
        score.setSort("2");
        score.setSubject("数学");
        score.setScore("120");
        scoreList.add(1, score);

        score = new Score();
        score.setSort("3");
        score.setSubject("英语");
        score.setScore("110");
        scoreList.add(2, score);

        score = new Score();
        score.setSort("4");
        score.setSubject("物理");
        score.setScore("110");
        scoreList.add(3, score);

        score = new Score();
        score.setSort("5");
        score.setSubject("化学");
        score.setScore("110");
        scoreList.add(4, score);

        score = new Score();
        score.setSort("6");
        score.setSubject("生物");
        score.setScore("110");
        scoreList.add(5, score);

        score = new Score();
        score.setSort("7");
        score.setSubject("政治");
        score.setScore("110");
        scoreList.add(6, score);

        score = new Score();
        score.setSort("8");
        score.setSubject("历史");
        score.setScore("110");
        scoreList.add(7, score);

        score = new Score();
        score.setSort("9");
        score.setSubject("地理");
        score.setScore("110");
        scoreList.add(8, score);

        report.setScoreList(scoreList);
        report.setTotalScore("320");
        report.setSelfJudge("勤学好问，乐于助人");
        return report;
    }

    /**
     * 获取图片数据
     *
     * @return
     */
    private static Picture getFileToPicture(File file) {
        try {
            String filename = file.getName();
            Picture picture = new Picture();
            int start = filename.lastIndexOf("/");
            int end = filename.lastIndexOf(".");
            InputStream in = new FileInputStream(file);
            byte[] data = new byte[in.available()];
            in.read(data);
            in.close();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.write(data);
            bos.close();
            picture.setPictureName(filename.substring(start + 1, end));
            picture.setFileName(filename.substring(start + 1));
            picture.setFos(bos);
            return picture;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Report getData(String fileExt) {
        int capacity = 10;

        Report report = new Report();
        report.setUserName("张三丰");
        report.setSex("男");
        report.setNation("蒙古族");

        Map<String, Picture> pictureMap = new HashMap<>();
        Picture header = (Picture) getImage("D:\\pic\\aaaa.jpeg", 2);
        Picture image = (Picture) getImage("D:\\pic\\bbbb.jpeg", 2);
        report.setHeader(header.getPictureName());
        report.setImage(image.getPictureName());
        pictureMap.put(report.getHeader(), header);
        pictureMap.put(report.getImage(), image);
        report.setPictureMap(pictureMap);


        List<Score> scoreList = new ArrayList<>(capacity);

        Score score = new Score();
        score.setSort("1");
        score.setSubject("语文");
        score.setScore("90");
        scoreList.add(0, score);

        score = new Score();
        score.setSort("2");
        score.setSubject("数学");
        score.setScore("120");
        scoreList.add(1, score);

        score = new Score();
        score.setSort("3");
        score.setSubject("英语");
        score.setScore("110");
        scoreList.add(2, score);

        score = new Score();
        score.setSort("4");
        score.setSubject("物理");
        score.setScore("110");
        scoreList.add(3, score);

        score = new Score();
        score.setSort("5");
        score.setSubject("化学");
        score.setScore("110");
        scoreList.add(4, score);

        score = new Score();
        score.setSort("6");
        score.setSubject("生物");
        score.setScore("110");
        scoreList.add(5, score);

        score = new Score();
        score.setSort("7");
        score.setSubject("政治");
        score.setScore("110");
        scoreList.add(6, score);

        score = new Score();
        score.setSort("8");
        score.setSubject("历史");
        score.setScore("110");
        scoreList.add(7, score);

        score = new Score();
        score.setSort("9");
        score.setSubject("地理");
        score.setScore("110");
        scoreList.add(8, score);

        report.setScoreList(scoreList);
        report.setTotalScore("320");
        report.setSelfJudge("勤学好问，乐于助人");
        return report;
    }

    /**
     * 填充数据
     *
     * @param fileName
     * @return
     */
    public static Word getWord(String fileName) {
        //获取后缀名
        String extName = fileName.substring(fileName.lastIndexOf(".") + 1);
        //创建对象
        Word word = new Word();
        word.setFileExt(extName);
        word.setTemplatePath("/templates/docx");
        word.setTemplateName("docx.ftl");
        word.setTemplateZipName("docx.zip");
        word.setDocName(fileName);
        word.setData(DataUtils.getData(word.getFileExt()));
        return word;
    }

    public static String getImageBase64(String filename) {
        try {
            InputStream in = new FileInputStream(filename);
            byte[] data = new byte[in.available()];
            in.read(data);
            in.close();
            String base64 = Base64.getEncoder().encodeToString(data);
            //System.out.println(base64);
            return Base64.getEncoder().encodeToString(data);
        } catch (Exception e) {
            e.printStackTrace();
            return "图片未找到";
        }
    }

    /**
     * 准备图片数据
     *
     * @param filename
     * @param type
     * @return
     */
    public static Object getImage(String filename, int type) {
        try {
            if (1 == type) {
                return getImageBase64(filename);
            } else if (2 == type) {
                Picture picture = new Picture();
                int start = filename.lastIndexOf("/");
                int end = filename.lastIndexOf(".");
                InputStream in = new FileInputStream(filename);
                byte[] data = new byte[in.available()];
                in.read(data);
                in.close();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bos.write(data);
                bos.close();
                picture.setPictureName(filename.substring(start + 1, end));
                picture.setFileName(filename.substring(start + 1));
                picture.setFos(bos);
                return picture;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "图片未找到";
        }
        return null;
    }
}
