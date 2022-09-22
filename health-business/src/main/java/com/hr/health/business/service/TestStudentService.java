package com.hr.health.business.service;



import com.hr.health.business.domain.Student;

import java.util.List;

public interface TestStudentService {
    List<Student> getStudentList();

    boolean addStudent();


}
