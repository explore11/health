package com.hr.health.web.controller.system;

import com.hr.health.common.constant.Constants;
import com.hr.health.common.core.domain.AjaxResult;
import com.hr.health.common.core.domain.Result;
import com.hr.health.common.core.domain.entity.SysMenu;
import com.hr.health.common.core.domain.entity.SysUser;
import com.hr.health.common.core.domain.model.LoginBody;
import com.hr.health.common.utils.SecurityUtils;
import com.hr.health.framework.web.service.SysLoginService;
import com.hr.health.framework.web.service.SysPermissionService;
import com.hr.health.system.domain.vo.RouterVo;
import com.hr.health.system.service.ISysMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 登录验证
 *
 * @author swq
 */
@Api(tags = "登录")
@RestController
@RequestMapping("/system/user")
public class SysLoginController {
    @Autowired
    private SysLoginService loginService;


    /**
     * 登录方法
     *
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    @ApiOperation("登录")
    public Result<String> login(@RequestBody LoginBody loginBody) {
        // 生成令牌
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(), loginBody.getUuid());
        return Result.success(token);
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @ApiOperation("获取用户信息")
    @GetMapping("getInfo")
    public Result<Map<String, Object>> getInfo() {
        Map<String, Object> map = loginService.getInfo();
        return Result.success(map);
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @ApiOperation("获取路由信息")
    @GetMapping("getRouters")
    public Result<List<RouterVo>> getRouters() {
        List<RouterVo> routers = loginService.getRouters();
        return Result.success(routers);
    }
}
