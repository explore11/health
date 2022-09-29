package com.hr.health.business.service;



import com.hr.health.business.domain.Student;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface TestStudentService {
    /**
     * 查询列表
     * @return
     */
    List<Student> getStudentList();

    /**
     * 添加数据
     * @return
     */
    boolean addStudent();


    /**
     * 导出
     * @param response
     */
    void export(HttpServletResponse response);

    /**
     * 压缩导出
     * @param response
     */
    void compressExport(HttpServletResponse response);

    /**
     * 多文件压缩导出
     * @param response
     */
    void multiCompressExport(HttpServletResponse response);
}
