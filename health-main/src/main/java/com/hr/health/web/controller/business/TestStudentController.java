package com.hr.health.web.controller.business;


import com.hr.health.business.domain.Student;
import com.hr.health.business.service.TestStudentService;
import com.hr.health.common.core.domain.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Api(tags = "学生测试")
@RequestMapping("/student")
public class TestStudentController {

    @Resource
    private TestStudentService testStudentService;

    @ApiOperation("获取Health列表")
    @GetMapping("/getStudentList")
    public Result getStudentList() {
        List<Student> list = testStudentService.getStudentList();
        return Result.success(list);
    }

    @ApiOperation("添加学生")
    @GetMapping("/addStudent")
    public Result addStudent() {
        boolean flag = testStudentService.addStudent();
        return Result.success(flag);
    }


}
