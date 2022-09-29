package com.hr.health.business.service;



import com.hr.health.business.domain.Student;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    /**
     * 下载压缩导入模板
     * @param response
     */
    void compressImportTemplate(HttpServletResponse response);

    /**
     * 解析压缩包导入数据
     * @param file
     */
    void parseCompressImportData(MultipartFile file) throws IOException;
}
