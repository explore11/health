package com.hr.health.web.controller.business;

import com.hr.health.system.service.WordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@Api(tags = "word测试")
@RequestMapping("/wordTest")
public class WordController {
    @Resource
    private WordService wordService;

    /**
     * doc文档下载
     *
     * @param request
     * @param response
     */
    @ApiOperation("doc文档下载")
    @GetMapping(value = "/downloadWordByDoc/export")
    public void downloadWord(HttpServletRequest request, HttpServletResponse response) {
        wordService.downloadWord(request, response);
    }


    /**
     * docx文档下载更改
     *
     * @param response
     */
    @ApiOperation("docx文档下载")
    @GetMapping(value = "/downloadWordByDocx/export")
    public void downloadWordByDocx(Map<String, MultipartFile> map, HttpServletResponse response) {
        wordService.downloadWordByDocx(map, response);
    }


}
