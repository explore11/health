package com.hr.health.framework.web.service;

import com.hr.health.common.constant.Constants;
import com.hr.health.common.constant.UserConstants;
import com.hr.health.common.core.domain.entity.SysUser;
import com.hr.health.common.core.domain.model.RegisterBody;
import com.hr.health.common.utils.MessageUtils;
import com.hr.health.common.utils.SecurityUtils;
import com.hr.health.common.utils.StringUtils;
import com.hr.health.framework.manager.AsyncManager;
import com.hr.health.framework.manager.factory.AsyncFactory;
import com.hr.health.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 注册校验方法
 *
 * @author swq
 */
@Component
public class SysRegisterService {
    @Autowired
    private ISysUserService userService;

    /**
     * 注册
     */
    public String register(RegisterBody registerBody) {
        String msg = "", username = registerBody.getUsername(), password = registerBody.getPassword();

        if (StringUtils.isEmpty(username)) {
            msg = "用户名不能为空";
        } else if (StringUtils.isEmpty(password)) {
            msg = "用户密码不能为空";
        } else if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            msg = "账户长度必须在2到20个字符之间";
        } else if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            msg = "密码长度必须在5到20个字符之间";
        } else if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(username))) {
            msg = "保存用户'" + username + "'失败，注册账号已存在";
        } else {
            SysUser sysUser = new SysUser();
            sysUser.setUserName(username);
            sysUser.setNickName(username);
            sysUser.setPassword(SecurityUtils.encryptPassword(registerBody.getPassword()));
            boolean regFlag = userService.registerUser(sysUser);
            if (!regFlag) {
                msg = "注册失败,请联系系统管理人员";
            } else {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.REGISTER,
                        MessageUtils.message("user.register.success")));
            }
        }
        return msg;
    }
}
