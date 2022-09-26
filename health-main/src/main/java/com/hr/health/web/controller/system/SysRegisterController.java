package com.hr.health.web.controller.system;

import com.hr.health.common.core.domain.Result;
import com.hr.health.common.utils.StringUtils;
import com.hr.health.framework.web.service.SysRegisterService;
import com.hr.health.system.service.ISysConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.hr.health.common.core.controller.BaseController;
import com.hr.health.common.core.domain.AjaxResult;
import com.hr.health.common.core.domain.model.RegisterBody;

/**
 * 注册验证
 *
 * @author swq
 */
@Api(tags = "注册")
@RestController
@RequestMapping("/system/user")
public class SysRegisterController extends BaseController {
    @Autowired
    private SysRegisterService registerService;

    /**
     * 注册
     *
     * @param user
     * @return
     */
    @ApiOperation("注册")
    @PostMapping("/register")
    public Result register(@RequestBody RegisterBody user) {
        String msg = registerService.register(user);
        return StringUtils.isEmpty(msg) ? Result.success() : Result.failure();
    }
}
