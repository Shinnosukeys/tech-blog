package com.techblog.utils.constants;

public class JwtConstants {
    // 密钥（生产环境需从配置中心获取，建议长度 >= 256 位）
    public static final String SECRET_KEY = "V09aWnRqY0NtWkZ5Y01hY3R5cFZ4Y0tXaSd5U09wY3BmS054b3N0Y3RjT0x4Y25yZw==";
    // Token 有效期（30 分钟）
    public static final long TOKEN_TTL = 30 * 60 * 1000; // 毫秒
    // 请求头字段
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // Bearer 前缀
    public static final String BEARER_PREFIX = "Bearer ";
}
