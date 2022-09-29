package com.hr.health.business.service.impl;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import com.hr.health.business.domain.Student;
import com.hr.health.business.mapper.TestStudentMapper;
import com.hr.health.business.service.TestStudentService;
import com.hr.health.common.utils.CompressUtil;
import com.hr.health.system.utils.poi.ExcelUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;

@Service
public class TestStudentServiceImpl implements TestStudentService {

    @Resource
    private TestStudentMapper testStudentMapper;


    /**
     * 解析压缩包导入数据
     * @param file
     */
    @Override
    public void parseCompressImportData(MultipartFile file) {

    }

    /**
     * 下载压缩导入模板
     * @param response
     */
    @Override
    public void compressImportTemplate(HttpServletResponse response) {
        ExcelUtil<Student> util = new ExcelUtil<>(Student.class);
        //生成本地的excel文件，返回绝对路径
        String excelPath = util.createExcelToLocal(null, "学生数据导入模板");

        //组装压缩路径
        String path = excelPath.substring(0, excelPath.lastIndexOf("/") + 1);
        String zipPath = path + "导入模板压缩包.zip";

        //进行文件压缩
        File zipFile = ZipUtil.zip(excelPath,zipPath);
        CompressUtil.downloadZip(response, zipFile.getName(), zipFile);

        //删除数据
        FileUtil.del(excelPath);
        FileUtil.del(zipPath);
    }

    /**
     * 多文件压缩导出
     *
     * @param response
     */
    @Override
    public void multiCompressExport(HttpServletResponse response) {
        List<Student> list = this.getStudentList();
        ExcelUtil<Student> util = new ExcelUtil<>(Student.class);
        //生成本地的excel文件，返回绝对路径
        String excelPathOne = util.createExcelToLocal(list, "学生数据1");
        String excelPathTwo = util.createExcelToLocal(list, "学生数据2");

        String path = excelPathOne.substring(0, excelPathOne.lastIndexOf("/") + 1);
        String zipPathName = path + "压缩包名称.zip";

        File zipFile = ZipUtil.zip(new File(zipPathName), false, new File(excelPathOne), new File(excelPathTwo));
        CompressUtil.downloadZip(response, zipFile.getName(), zipFile);

        //删除数据
        FileUtil.del(excelPathOne);
        FileUtil.del(excelPathTwo);
        FileUtil.del(zipPathName);

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

        //组装压缩路径
        String path = excelPath.substring(0, excelPath.lastIndexOf("/") + 1);
        String zipPath = path + "单个压缩包名称.zip";

        //进行文件压缩
        File zipFile = ZipUtil.zip(excelPath,zipPath);
        CompressUtil.downloadZip(response, zipFile.getName(), zipFile);

        //删除数据
        FileUtil.del(excelPath);
        FileUtil.del(zipPath);
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
