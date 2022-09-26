package com.hr.health.framework.web.service;

import com.hr.health.common.constant.Constants;
import com.hr.health.common.core.domain.AjaxResult;
import com.hr.health.common.core.domain.entity.SysMenu;
import com.hr.health.common.core.domain.entity.SysUser;
import com.hr.health.common.core.domain.model.LoginUser;
import com.hr.health.common.exception.ServiceException;
import com.hr.health.common.exception.user.UserPasswordNotMatchException;
import com.hr.health.common.utils.DateUtils;
import com.hr.health.common.utils.MessageUtils;
import com.hr.health.common.utils.SecurityUtils;
import com.hr.health.common.utils.ServletUtils;
import com.hr.health.common.utils.ip.IpUtils;
import com.hr.health.framework.manager.AsyncManager;
import com.hr.health.framework.manager.factory.AsyncFactory;
import com.hr.health.framework.security.context.AuthenticationContextHolder;
import com.hr.health.system.domain.vo.RouterVo;
import com.hr.health.system.service.ISysConfigService;
import com.hr.health.system.service.ISysMenuService;
import com.hr.health.system.service.ISysUserService;
import jdk.internal.org.objectweb.asm.Handle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 登录校验方法
 *
 * @author swq
 */
@Component
public class SysLoginService {
    @Autowired
    private TokenService tokenService;

    @Resource
    private AuthenticationManager authenticationManager;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private ISysUserService userService;


    /**
     * 登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @param code     验证码
     * @param uuid     唯一标识
     * @return 结果
     */
    public String login(String username, String password, String code, String uuid) {
        // 用户验证
        Authentication authentication = null;
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
            AuthenticationContextHolder.setContext(authenticationToken);
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            if (e instanceof BadCredentialsException) {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
                throw new UserPasswordNotMatchException();
            } else {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, e.getMessage()));
                throw new ServiceException(e.getMessage());
            }
        } finally {
            AuthenticationContextHolder.clearContext();
        }
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        recordLoginInfo(loginUser.getUserId());

        //设置用户代理信息
        tokenService.setUserAgent(loginUser);

        // 设置登录时间
        loginUser.setLoginTime(System.currentTimeMillis());

        // 生成token
        return tokenService.createToken(loginUser);
    }

    /**
     * 记录登录信息
     *
     * @param userId 用户ID
     */
    public void recordLoginInfo(Long userId) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        sysUser.setLoginIp(IpUtils.getIpAddr(ServletUtils.getRequest()));
        sysUser.setLoginDate(DateUtils.getNowDate());
        userService.updateUserProfile(sysUser);
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    public Map<String, Object> getInfo() {
        Map<String, Object> map = new HashMap<>();

        SysUser user = SecurityUtils.getLoginUser().getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);

        //返回数据
        map.put("user", user);
        map.put("roles", roles);
        map.put("permissions", permissions);

        return map;
    }

    /**
     * 获取路由信息
     *
     * @return
     */
    public List<RouterVo> getRouters() {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return menuService.buildMenus(menus);
    }
}
