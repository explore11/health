package com.hr.health.framework.web.service;

import com.alibaba.fastjson2.JSON;
import com.hr.health.common.constant.CacheConstants;
import com.hr.health.common.constant.Constants;
import com.hr.health.common.core.domain.model.LoginUser;
import com.hr.health.common.utils.ServletUtils;
import com.hr.health.common.utils.StringUtils;
import com.hr.health.common.utils.ip.AddressUtils;
import com.hr.health.common.utils.ip.IpUtils;
import com.hr.health.system.service.ISysUserService;
import eu.bitwalker.useragentutils.UserAgent;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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

    private static final Long MILLIS_MINUTE_TEN = 20 * 60 * 1000L;

//    @Autowired
//    private RedisCache redisCache;

    @Resource
    private ISysUserService iSysUserService;

    @Resource
    private SysPermissionService sysPermissionService;

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public Claims getClaims(HttpServletRequest request) {
        // 获取请求携带的令牌
        String token = getToken(request);
        System.out.println("token"+token);
        if (StringUtils.isNotEmpty(token)) {
            try {


                Claims claims = parseToken(token);
                // 解析对应的权限以及用户信息
//                String uuid = (String) claims.get(Constants.LOGIN_USER_KEY);
//                String userKey = getTokenKey(uuid);
//                LoginUser user = redisCache.getCacheObject(userKey);
                return claims;
            } catch (ExpiredJwtException e) {
                //
                return e.getClaims();
            }
        }
        return null;
    }

//    /**
//     * 设置用户身份信息
//     */
//    public void setLoginUser(LoginUser loginUser) {
//        if (StringUtils.isNotNull(loginUser) && StringUtils.isNotEmpty(loginUser.getToken())) {
//            refreshToken(loginUser);
//        }
//    }

//    /**
//     * 删除用户身份信息
//     */
//    public void delLoginUser(String token) {
//        if (StringUtils.isNotEmpty(token)) {
//            String userKey = getTokenKey(token);
//            redisCache.deleteObject(userKey);
//        }
//    }

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

//        //根据用户名查询用户信息
//        SysUser sysUser = iSysUserService.selectUserByUserName(username);
//        //根据用户查询权限信息
//        Set<String> permissions = sysPermissionService.getPermissionsByRole(sysUser);
//        //设置权限信息
//        claims.put(Constants.PERMISSION, JSON.toJSONString(permissions));

        return createToken(claims);
    }

//    /**
//     * 验证令牌有效期，相差不足20分钟，自动刷新缓存
//     *
//     * @param loginUser
//     * @return 令牌
//     */
//    public void verifyToken(LoginUser loginUser) {
//        long expireTime = loginUser.getExpireTime();
//        long currentTime = System.currentTimeMillis();
//        if (expireTime - currentTime <= MILLIS_MINUTE_TEN) {
//            refreshToken(loginUser);
//        }
//    }

//    /**
//     * 刷新令牌有效期
//     *
//     * @param loginUser 登录信息
//     */
//    public void refreshToken(LoginUser loginUser) {
//        loginUser.setLoginTime(System.currentTimeMillis());
//        loginUser.setExpireTime(loginUser.getLoginTime() + expireTime * MILLIS_MINUTE);
//        // 根据uuid将loginUser缓存
//        String userKey = getTokenKey(loginUser.getToken());
//        redisCache.setCacheObject(userKey, loginUser, expireTime, TimeUnit.MINUTES);
//    }

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
