package com.hr.health.framework.security.filter;

import java.io.IOException;
import java.util.Set;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSON;
import com.hr.health.common.constant.Constants;
import com.hr.health.common.core.domain.model.LoginUser;
import com.hr.health.common.enums.ResultCode;
import com.hr.health.common.exception.MicroServiceException;
import com.hr.health.common.exception.ServiceException;
import com.hr.health.common.utils.SecurityUtils;
import com.hr.health.common.utils.StringUtils;
import com.hr.health.framework.web.service.TokenService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * token过滤器 验证token有效性
 *
 * @author swq
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Autowired
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        //获取Claims
        Claims claims = tokenService.getClaims(request);
        //获取用户对象
        if (claims != null) {
            String loginStr = (String) claims.get(Constants.LOGIN_USER_KEY);
            LoginUser loginUser = JSON.parseObject(loginStr, LoginUser.class);
            if (StringUtils.isNotNull(loginUser) && StringUtils.isNull(SecurityUtils.getAuthentication())) {
                // 校验当是否过期
                boolean isExpiration = tokenService.verifyIsExpiration(claims);
                if (isExpiration) {
                    // 校验当前时间是否过期是否超过刷新时间
                    boolean isRefreshExpiration = tokenService.verifyRefreshExpiration(claims);
                    if (!isRefreshExpiration) {
                        //生成新的token
                        String refreshToken = tokenService.createToken(loginUser);
                        //设置header
                        response.setHeader(HttpHeaders.AUTHORIZATION, Constants.TOKEN_PREFIX + refreshToken);
                    } else {
                        // 过期提示
                        throw new MicroServiceException(ResultCode.USER_EXPIRE.code(), ResultCode.USER_EXPIRE.message());
                    }
                }
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        chain.doFilter(request, response);
    }
}
