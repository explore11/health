package com.hr.health.web.controller.system;

import com.hr.health.common.annotation.Log;
import com.hr.health.common.config.HealthConfig;
import com.hr.health.common.core.domain.Result;
import com.hr.health.common.core.domain.entity.SysUser;
import com.hr.health.common.core.domain.model.LoginUser;
import com.hr.health.common.enums.BusinessType;
import com.hr.health.common.enums.ResultCode;
import com.hr.health.common.utils.SecurityUtils;
import com.hr.health.common.utils.StringUtils;
import com.hr.health.common.utils.file.FileUploadUtils;
import com.hr.health.common.utils.file.MimeTypeUtils;
import com.hr.health.framework.web.service.TokenService;
import com.hr.health.system.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.hr.health.common.constant.UserConstants;
import com.hr.health.common.core.controller.BaseController;
import com.hr.health.common.core.domain.AjaxResult;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 个人信息 业务处理
 *
 * @author swq
 */
@Api(tags = "个人信息")
@RestController
@RequestMapping("/system/userProfile")
public class SysProfileController extends BaseController {
    @Autowired
    private ISysUserService userService;


    /**
     * 个人信息
     */
    @GetMapping("/profile")
    @ApiOperation("获取岗位列表")
    public Result<Map<String, Object>> profile() {
        LoginUser loginUser = getLoginUser();
        SysUser user = loginUser.getUser();

        Map<String, Object> map = new HashMap<>();
        map.put("roleGroup", userService.selectUserRoleGroup(loginUser.getUsername()));
        map.put("postGroup", userService.selectUserPostGroup(loginUser.getUsername()));
        map.put("user", user);
        return Result.success(map);

    }

    /**
     *
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping("/updateProfile")
    @ApiOperation("修改用户")
    public Result updateProfile(@RequestBody SysUser user) {
        LoginUser loginUser = getLoginUser();
        SysUser sysUser = loginUser.getUser();
        user.setUserName(sysUser.getUserName());
        if (StringUtils.isNotEmpty(user.getPhonenumber()) && UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
            return Result.failure(ResultCode.USER_PHONE_EXIST.code(), ResultCode.USER_PHONE_EXIST.message());
        }

        user.setUserId(sysUser.getUserId());
        user.setPassword(null);
        user.setAvatar(null);
        user.setDeptId(null);
        return Result.judge(userService.updateUserProfile(user));
    }

    /**
     * 重置密码
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping("/updatePwd")
    @ApiOperation("重置密码")
    public Result updatePwd(String oldPassword, String newPassword) {
        LoginUser loginUser = getLoginUser();
        String userName = loginUser.getUsername();
        String password = loginUser.getPassword();
        if (!SecurityUtils.matchesPassword(oldPassword, password)) {
            return Result.failure(ResultCode.USER_UPDATE_PASSWORD_FAILURE.code(), ResultCode.USER_UPDATE_PASSWORD_FAILURE.message());
        }
        if (SecurityUtils.matchesPassword(newPassword, password)) {
            return Result.failure(ResultCode.USER_PASSWORD_NO_SAME.code(), ResultCode.USER_PASSWORD_NO_SAME.message());
        }

        return Result.judge(userService.resetUserPwd(userName, SecurityUtils.encryptPassword(newPassword)));

    }

    /**
     * 头像上传
     */
    @Log(title = "用户头像", businessType = BusinessType.UPDATE)
    @PostMapping("/avatar")
    @ApiOperation("头像上传")
    public Result avatar(@RequestParam("avatarfile") MultipartFile file) throws Exception {
        if (!file.isEmpty()) {
            LoginUser loginUser = getLoginUser();
            String avatar = FileUploadUtils.upload(HealthConfig.getAvatarPath(), file, MimeTypeUtils.IMAGE_EXTENSION);
            if (userService.updateUserAvatar(loginUser.getUsername(), avatar)) {
                return Result.success(avatar);
            }
        }
        return Result.failure();
    }
}
