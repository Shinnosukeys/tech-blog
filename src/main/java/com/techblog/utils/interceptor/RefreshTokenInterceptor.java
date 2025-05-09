package com.techblog.utils.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.techblog.dto.UserDTO;
import com.techblog.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.techblog.utils.constants.RedisConstants.LOGIN_USER_KEY;
import static com.techblog.utils.constants.RedisConstants.LOGIN_USER_TTL;


@Component
public class RefreshTokenInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 1.获取请求头中的token
        String tokenWithPrefix = request.getHeader("Authorization");
        if (StrUtil.isBlank(tokenWithPrefix)) {
            return true;
        }

        String pureToken = StrUtil.removePrefixIgnoreCase(tokenWithPrefix, "Bearer ");
        if (StrUtil.isBlank(pureToken)) {
            // 无效格式：缺少空格或纯 token
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }

        // 2.基于TOKEN获取redis中的用户
        String key = LOGIN_USER_KEY + pureToken;
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(key);
        // 3.判断用户是否存在
        if (userMap.isEmpty()) {
            return true;
        }
        // 5.将查询到的hash数据转为UserDTO
        UserDTO userDTO = BeanUtil.fillBeanWithMap(userMap, new UserDTO(), false);
        // 7.刷新token有效期
        stringRedisTemplate.expire(key, LOGIN_USER_TTL, TimeUnit.MINUTES);
        // 6.存在，保存用户信息到 ThreadLocal
        UserHolder.saveUser(userDTO);

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 移除用户
        UserHolder.removeUser();
    }
}
