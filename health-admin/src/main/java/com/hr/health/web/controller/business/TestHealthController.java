package com.hr.health.web.controller.business;

import com.hr.health.common.core.domain.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/health")
public class TestHealthController {
    @ApiOperation("获取Health列表")
    @GetMapping("/list")
    public R userList() {
        System.out.println("测试接口");
        return R.ok();
    }
}
