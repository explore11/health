package com.hr.health.web.controller.business;

import com.hr.health.system.service.WordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@Api(tags = "word测试")
@RequestMapping("/wordTest")
public class WordController {
    @Resource
    private WordService wordService;

    /**
     * doc文档下载
     * @param request
     * @param response
     */
    @ApiOperation("doc文档下载")
    @GetMapping(value = "/word/export")
    public void downloadWord(HttpServletRequest request, HttpServletResponse response) {
        wordService.downloadWord(request,response);
    }

}
