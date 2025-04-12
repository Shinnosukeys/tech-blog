package com.techblog.utils.interceptor;

import cn.hutool.core.util.StrUtil;
import com.techblog.dto.UserDTO;
import com.techblog.utils.UserHolder;
import com.techblog.utils.constants.JwtConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Slf4j
@Component
public class JwtAuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 1. 获取请求头中的 JWT（格式：Bearer <jwt>）
        String tokenWithPrefix = request.getHeader(JwtConstants.AUTHORIZATION_HEADER);
        if (StrUtil.isBlank(tokenWithPrefix)) {
            return true; // 匿名请求放行（根据业务调整）
        }

        // 2. 剥离 Bearer 前缀
        String jwtToken = tokenWithPrefix.startsWith(JwtConstants.BEARER_PREFIX) ?
                tokenWithPrefix.substring(JwtConstants.BEARER_PREFIX.length()) : tokenWithPrefix;

        try {
            // 3. 解析并验签 JWT
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(JwtConstants.SECRET_KEY)
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();

            // 4. 校验 Token 未过期
            if (claims.getExpiration().before(new Date())) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

            // 5. 提取用户信息并封装为 UserDTO
            UserDTO userDTO = new UserDTO();
            userDTO.setId(Long.valueOf(claims.getSubject()));
            userDTO.setNickName((String) claims.get("nickName"));
            userDTO.setIcon((String) claims.get("icon"));

            // 6. 存入 ThreadLocal
            UserHolder.saveUser(userDTO);

            return true;

        } catch (JwtException e) {
            // 无效签名、Token 篡改、过期等异常
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            log.error("JWT 校验失败：{}", e.getMessage());
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清除 ThreadLocal，避免内存泄漏
        UserHolder.removeUser();
    }
}
