package com.hr.health.system.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface WordService {
    /**
     * doc文档下载
     *
     * @param request
     * @param response
     */
    void downloadWord(HttpServletRequest request, HttpServletResponse response);

}
