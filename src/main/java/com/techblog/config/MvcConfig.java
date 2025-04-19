package com.techblog.config;

import com.techblog.utils.interceptor.JwtAuthenticationInterceptor;
import com.techblog.utils.interceptor.LoginInterceptor;
import com.techblog.utils.interceptor.RefreshTokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
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
        // JWT 攔截器
        registry.addInterceptor(jwtAuthenticationInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/user/code",    // 驗證碼接口
                        "/user/login",   // 登錄接口
                        "/user/sign",    // 簽到接口
                        "/user/sign/count", // 簽到統計接口
                        "/*.html",       // 排除所有HTML文件
                        "/css/**",       // 排除CSS文件
                        "/js/**",        // 排除JS文件
                        "/images/**",    // 排除圖片文件
                        "/favicon.ico",  // 排除網站圖標
                        "/error"         // 排除錯誤頁面
                ).order(0);

        // 登錄攔截器
        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns(
                        "/user/code",
                        "/user/login",
                        "/user/sign",
                        "/user/sign/count",
                        "/*.html",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/favicon.ico",
                        "/error"
                ).order(1);

        // token刷新攔截器
        registry.addInterceptor(refreshTokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/user/code",
                        "/user/login",
                        "/user/sign",
                        "/user/sign/count",
                        "/*.html",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/favicon.ico",
                        "/error"
                ).order(2);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置靜態資源映射
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600) // 設置緩存時間為1小時
                .resourceChain(true); // 開啟資源鏈
    }
}
