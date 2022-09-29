package com.hr.health.business.service.impl;


import cn.hutool.core.util.ZipUtil;
import com.hr.health.business.domain.Student;
import com.hr.health.business.mapper.TestStudentMapper;
import com.hr.health.business.service.TestStudentService;
import com.hr.health.common.core.domain.entity.SysUser;
import com.hr.health.common.utils.CompressUtil;
import com.hr.health.system.utils.poi.ExcelUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;

@Service
public class TestStudentServiceImpl implements TestStudentService {

    @Resource
    private TestStudentMapper testStudentMapper;


    /**
     * 多文件压缩导出
     * @param response
     */
    @Override
    public void multiCompressExport(HttpServletResponse response) {

    }

    /**
     * 压缩导出
     *
     * @param response
     */
    @Override
    public void compressExport(HttpServletResponse response) {
        List<Student> list = this.getStudentList();
        ExcelUtil<Student> util = new ExcelUtil<>(Student.class);
        //生成本地的excel文件，返回绝对路径
        String excelPath = util.createExcelToLocal(list, "学生数据");
        //进行文件压缩
        File zipFile = ZipUtil.zip(excelPath);
        CompressUtil.downloadZip(response, "压缩包.zip", zipFile);
    }

    /**
     * 导出
     *
     * @param response
     */
    @Override
    public void export(HttpServletResponse response) {
        List<Student> list = this.getStudentList();
        ExcelUtil<Student> util = new ExcelUtil<>(Student.class);
        util.exportExcel(response, list, "学生数据");
    }

    /**
     * 查询列表
     *
     * @return
     */
    @Override
    public List<Student> getStudentList() {
        return testStudentMapper.selectList(null);
    }

    /**
     * 添加数据
     *
     * @return
     */
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
