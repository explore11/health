package com.hr.health.framework.web.service;

import com.alibaba.fastjson2.JSON;
import com.hr.health.common.constant.CacheConstants;
import com.hr.health.common.constant.Constants;
import com.hr.health.common.core.domain.model.LoginUser;
import com.hr.health.common.utils.ServletUtils;
import com.hr.health.common.utils.StringUtils;
import com.hr.health.common.utils.ip.AddressUtils;
import com.hr.health.common.utils.ip.IpUtils;
import eu.bitwalker.useragentutils.UserAgent;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * token验证处理
 *
 * @author swq
 */
@Component
public class TokenService {
    // 令牌自定义标识
    @Value("${token.header}")
    private String header;

    // 令牌秘钥
    @Value("${token.secret}")
    private String secret;

    // 令牌有效期（默认30分钟）
    @Value("${token.expireTime}")
    private int expireTime;

    // 令牌刷新有效期（默认60分钟）
    @Value("${token.refreshTime}")
    private int refreshTime;

    protected static final long MILLIS_SECOND = 1000;

    protected static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public Claims getClaims(HttpServletRequest request) {
        // 获取请求携带的令牌
        String token = getToken(request);
        if (StringUtils.isNotEmpty(token)) {
            try {
                Claims claims = parseToken(token);
                // 解析对应的权限以及用户信息
                return claims;
            } catch (ExpiredJwtException e) {
                //
                return e.getClaims();
            }
        }
        return null;
    }


    /**
     * 创建令牌
     *
     * @param loginUser 用户
     * @return 令牌
     */
    public String createToken(LoginUser loginUser) {

        Map<String, Object> claims = new HashMap<>();
        // 设置用户信息
        claims.put(Constants.LOGIN_USER_KEY, JSON.toJSONString(loginUser));
        //设置刷新时间
        long refresh = System.currentTimeMillis() + refreshTime * MILLIS_MINUTE;
        claims.put(Constants.REFRESH_TOKEN_TIME, refresh);

        return createToken(claims);
    }

    /**
     * 设置用户代理信息
     *
     * @param loginUser 登录信息
     */
    public void setUserAgent(LoginUser loginUser) {
        UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
        String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
        loginUser.setIpaddr(ip);
        loginUser.setLoginLocation(AddressUtils.getRealAddressByIP(ip));
        loginUser.setBrowser(userAgent.getBrowser().getName());
        loginUser.setOs(userAgent.getOperatingSystem().getName());
    }

    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    private String createToken(Map<String, Object> claims) {
        String token = Jwts.builder()
                .setClaims(claims)
                //设置过期时间
                .setExpiration(new Date(System.currentTimeMillis() + (expireTime * MILLIS_MINUTE)))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
        return token;
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    private Claims parseToken(String token) {
        try {
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }

    }

    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * 获取请求token
     *
     * @param request
     * @return token
     */
    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(header);
        if (StringUtils.isNotEmpty(token) && token.startsWith(Constants.TOKEN_PREFIX)) {
            token = token.replace(Constants.TOKEN_PREFIX, "");
        }
        return token;
    }

    private String getTokenKey(String uuid) {
        return CacheConstants.LOGIN_TOKEN_KEY + uuid;
    }

    /**
     * 校验当前时间是否超过刷新时间
     *
     * @param claims
     * @return
     */
    public boolean verifyIsExpiration(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    /**
     * 校验当前时间是否过期是否超过刷新时间
     *
     * @param claims
     * @return
     */
    public boolean verifyRefreshExpiration(Claims claims) {
        //当前时间
        long currentTime = System.currentTimeMillis();
        // 刷新时间
        long refreshTime = (long) claims.get(Constants.REFRESH_TOKEN_TIME);

        if (currentTime <= refreshTime) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }


}
