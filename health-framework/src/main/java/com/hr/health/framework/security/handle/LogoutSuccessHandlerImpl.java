package com.hr.health.framework.security.handle;

import java.io.IOException;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hr.health.common.constant.Constants;
import com.hr.health.common.core.domain.model.LoginUser;
import com.hr.health.common.utils.ServletUtils;
import com.hr.health.common.utils.StringUtils;
import com.hr.health.framework.manager.AsyncManager;
import com.hr.health.framework.manager.factory.AsyncFactory;
import com.hr.health.framework.web.service.TokenService;
import com.hr.health.system.service.ISysUserService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import com.alibaba.fastjson2.JSON;
import com.hr.health.common.constant.HttpStatus;
import com.hr.health.common.core.domain.AjaxResult;

/**
 * 自定义退出处理类 返回成功
 *
 * @author swq
 */
@Configuration
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {
    @Autowired
    private TokenService tokenService;

    /**
     * 退出处理
     *
     * @return
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        Claims claims = tokenService.getClaims(request);
        if (claims != null) {
            //获取用户对象
            String loginUserStr = (String) claims.get(Constants.LOGIN_USER_KEY);
            LoginUser loginUser = JSON.parseObject(loginUserStr, LoginUser.class);
            if (StringUtils.isNotNull(loginUser)) {
                //TODO 删除header中的token  前端删除 或者后端删除

                // 记录用户退出日志
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(loginUser.getUsername(), Constants.LOGOUT, "退出成功"));
            }
        }
        ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.error(HttpStatus.SUCCESS, "退出成功")));
    }
}
