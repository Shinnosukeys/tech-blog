package com.techblog.config;

import com.techblog.utils.interceptor.JwtAuthenticationInterceptor;
import com.techblog.utils.interceptor.LoginInterceptor;
import com.techblog.utils.interceptor.RefreshTokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

//@Configuration
//public class MvcConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        // 登录拦截器
//        registry.addInterceptor(new LoginInterceptor())
//                .excludePathPatterns(
//                        "/shop/**",
//                        "/voucher/**",
//                        "/shop-type/**",
//                        "/upload/**",
//                        "/blog/hot",
//                        "/user/code",
//                        "/user/login"
//                ).order(1);
//        // token刷新的拦截器
//        registry.addInterceptor(new RefreshTokenInterceptor()).addPathPatterns("/**").order(0);
//    }
//}


@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Resource
    public LoginInterceptor loginInterceptor;

    @Resource
    private RefreshTokenInterceptor refreshTokenInterceptor;

    @Resource
    private JwtAuthenticationInterceptor jwtAuthenticationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns(
                        "/shop/**",
                        "/voucher/**",
                        "/shop-type/**",
                        "/upload/**",
                        "/blog/hot",
                        "/user/code",
                        "/user/login"
                ).order(1);

        // 4. 直接使用注入的 Bean
//        registry.addInterceptor(refreshTokenInterceptor)
//                .addPathPatterns("/**")
//                .order(0);

        registry.addInterceptor(jwtAuthenticationInterceptor)
                .addPathPatterns("/**") // 拦截所有接口（除了公开接口）
                .excludePathPatterns(
                        "/user/code",    // 验证码接口
                        "/user/login"    // 登录接口
                );
    }
}
