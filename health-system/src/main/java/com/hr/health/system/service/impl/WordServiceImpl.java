package com.hr.health.system.service.impl;

import com.hr.health.common.WordTemplateUtil;
import com.hr.health.system.domain.People;
import com.hr.health.system.service.WordService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WordServiceImpl implements WordService {

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

        String base64 = WordTemplateUtil.imageToBase64("D:\\pic\\a.jpeg");
        String image = base64;
        //输出文档
        String fileName = new String("啦啦啦啦.doc");
        dataMap.put("fileName", fileName);

        dataMap.put("image", image);
        //创建doc文件
        WordTemplateUtil.createDoc(response, dataMap);
    }
}
