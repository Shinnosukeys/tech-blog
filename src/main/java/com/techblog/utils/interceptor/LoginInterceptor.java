package com.techblog.utils.interceptor;

import cn.hutool.core.bean.BeanUtil;
import com.techblog.dto.UserDTO;
import com.techblog.utils.UserHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从UserHolder中拿取用户信息
        UserDTO user = UserHolder.getUser();
        if (user == null) {
            response.setStatus(401);
            return false;
        }
        return true;
    }
}
