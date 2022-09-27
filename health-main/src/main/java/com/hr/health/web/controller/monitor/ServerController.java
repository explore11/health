package com.hr.health.web.controller.monitor;

import com.hr.health.common.core.domain.Result;
import com.hr.health.framework.web.domain.Server;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.hr.health.common.core.domain.AjaxResult;

/**
 * 服务器监控
 *
 * @author swq
 */
@Api(tags = "服务器监控")
@RestController
@RequestMapping("/monitor/server")
public class ServerController {


    @PreAuthorize("@ss.hasPermi('monitor:server:list')")
    @GetMapping()
    @ApiOperation("清除")
    public Result getInfo() throws Exception {
        Server server = new Server();
        server.copyTo();
        return Result.success(server);
    }
}
