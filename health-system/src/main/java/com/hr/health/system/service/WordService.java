package com.hr.health.system.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface WordService {
    /**
     * doc文档下载
     *
     * @param request
     * @param response
     */
    void downloadWord(HttpServletRequest request, HttpServletResponse response);

    /**
     * docx文档下载更改
     *
     * @param map
     * @param response
     */
    void downloadWordByDocx(Map<String, MultipartFile> map, HttpServletResponse response);
}
