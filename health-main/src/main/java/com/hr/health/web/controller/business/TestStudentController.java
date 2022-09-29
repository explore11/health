package com.hr.health.web.controller.business;


import com.hr.health.business.domain.Student;
import com.hr.health.business.service.TestStudentService;
import com.hr.health.common.annotation.Log;
import com.hr.health.common.core.domain.Result;
import com.hr.health.common.core.domain.entity.SysUser;
import com.hr.health.common.enums.BusinessType;
import com.hr.health.system.utils.poi.ExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
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

    /**
     * 导出
     *
     * @param response
     */
    @ApiOperation("导出")
    @GetMapping("/export")
    public void export(HttpServletResponse response) {
        testStudentService.export(response);
    }

    /**
     * 压缩导出
     *
     * @param response
     */
    @ApiOperation("压缩导出")
    @GetMapping("/compressExport")
    public void compressExport(HttpServletResponse response) {
        testStudentService.compressExport(response);
    }

    /**
     * 多文件压缩导出
     *
     * @param response
     */
    @ApiOperation("多文件压缩导出")
    @GetMapping("/multiCompressExport")
    public void multiCompressExport(HttpServletResponse response) {
        testStudentService.multiCompressExport(response);
    }




}
