package com.hr.health.framework.web.service;

import com.hr.health.common.constant.Constants;
import com.hr.health.common.core.domain.entity.SysUser;
import com.hr.health.common.exception.user.UserPasswordNotMatchException;
import com.hr.health.common.utils.SecurityUtils;
import com.hr.health.framework.manager.AsyncManager;
import com.hr.health.framework.manager.factory.AsyncFactory;
import com.hr.health.framework.security.context.AuthenticationContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * 登录密码方法
 *
 * @author swq
 */
@Component
public class SysPasswordService {

    /**
     * 校验用户
     * @param user
     */
    public void validate(SysUser user) {
        Authentication usernamePasswordAuthenticationToken = AuthenticationContextHolder.getContext();
        String username = usernamePasswordAuthenticationToken.getName();
        String password = usernamePasswordAuthenticationToken.getCredentials().toString();

        if (!matches(user, password)) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL,"登录密码不匹配"));
            throw new UserPasswordNotMatchException();
        }
    }

    /**
     * 匹配密码
     * @param user
     * @param rawPassword
     * @return
     */
    public boolean matches(SysUser user, String rawPassword) {
        return SecurityUtils.matchesPassword(rawPassword, user.getPassword());
    }

}
