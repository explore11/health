package com.hr.health.business.service.impl;


import com.hr.health.business.domain.Student;
import com.hr.health.business.mapper.TestStudentMapper;
import com.hr.health.business.service.TestStudentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TestStudentServiceImpl implements TestStudentService {

    @Resource
    private TestStudentMapper testStudentMapper;

    @Override
    public List<Student> getStudentList() {
        return testStudentMapper.selectList(null);
    }

    @Override
    public boolean addStudent() {
        Student student = new Student();
        student.setName("zhangsan");
        student.setCard("s001");
        student.setScore("90");
        student.setCourseId(1000L);
        return testStudentMapper.insert(student) > 0;
    }
}
