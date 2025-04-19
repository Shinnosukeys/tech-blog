package com.techblog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.techblog.dto.LoginFormDTO;
import com.techblog.dto.Result;
import com.techblog.dto.UserDTO;
import com.techblog.entity.user.User;
import com.techblog.entity.user.UserInfo;
import com.techblog.mapper.UserInfoMapper;
import com.techblog.mapper.UserMapper;
import com.techblog.service.IUserInfoService;
import com.techblog.service.IUserService;
import com.techblog.utils.RedisConstants;
import com.techblog.utils.RegexUtils;
import com.techblog.utils.UserHolder;
import com.techblog.utils.constants.JwtConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.Date;

import static com.techblog.utils.RedisConstants.*;
import static com.techblog.utils.SystemConstants.USER_NICK_NAME_PREFIX;


@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private IUserInfoService userInfoService;

    @Override
    public Result sendCode(String phone) {
        // 1.校验手机号
        if (RegexUtils.isPhoneInvalid(phone)) {
            // 2.如果不符合，返回错误信息
            return Result.fail("手机号格式错误！");
        }
        // 3.符合，生成验证码
        String code = RandomUtil.randomNumbers(6);

        // 4.保存验证码+phone到 redis
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + phone, code, LOGIN_CODE_TTL, TimeUnit.MINUTES);

        // 5.发送验证码
        log.debug("发送短信验证码成功，验证码：{}", code);
        // 返回ok并包含验证码
        Result result = Result.ok();
        result.setData(code);
        return result;
    }

    @Override
    public Result login(LoginFormDTO loginForm) {
        // 1. 校验手机号+验证码，获取用户
        Result verifyResult = verifyPhoneAndUser(loginForm);
        if (!verifyResult.getSuccess()) {
            return verifyResult; // 直接返回校验失败结果
        }

        User user = (User) verifyResult.getData(); // 从 Result 中获取用户
        // 8.返回token
        return Result.ok(generateToken(user));
    }


    @Override
    public Result loginByJWT(LoginFormDTO loginForm) {
        // 檢查 loginForm 是否為 null
        if (loginForm == null) {
            return Result.fail("登錄參數不能為空");
        }
        
        // 1. 校驗手機號+驗證碼，獲取用戶
        Result verifyResult = verifyPhoneAndUser(loginForm);
        if (!verifyResult.getSuccess()) {
            return verifyResult; // 直接返回校驗失敗結果
        }
        User user = (User) verifyResult.getData(); // 從 Result 中獲取用戶

        // 2. 生成 JWT（包含用戶 ID 和必要信息）
        String jwtToken = Jwts.builder()
                .setSubject(String.valueOf(user.getId())) // 主題：用戶 ID
                .claim("nickName", user.getNickName())   // 自定義載荷：暱稱
                .claim("icon", user.getIcon())           // 自定義載荷：頭像
                .setExpiration(new Date(System.currentTimeMillis() + JwtConstants.TOKEN_TTL)) // 過期時間
                .signWith(SignatureAlgorithm.HS256, JwtConstants.SECRET_KEY) // 簽名算法
                .compact(); // 生成完整 JWT

        // 3. 返回 JWT（帶 Bearer 前綴）
        return Result.ok(JwtConstants.BEARER_PREFIX + jwtToken);
    }

    @Override
    public Result logout(HttpServletRequest request) {
        // 1. 从请求头获取 Token（格式：Bearer <token>）
        String tokenWithPrefix = request.getHeader("Authorization");
        if (StrUtil.isBlank(tokenWithPrefix)) {
            return Result.ok(); // 无 Token 直接视为注销成功（可能已过期）
        }

        // 2. 剥离 "Bearer " 前缀
        String pureToken = StrUtil.removePrefixIgnoreCase(tokenWithPrefix, "Bearer ");
        if (StrUtil.isBlank(pureToken)) {
            return Result.ok(); // 无效格式，静默处理（或返回错误）
        }

        // 3. 构建 Redis 键并删除
        String key = RedisConstants.LOGIN_USER_KEY + pureToken;
        stringRedisTemplate.delete(key); // 移除 Redis 中的用户登录状态

        // 4. 清除 ThreadLocal 中的用户信息（确保当前请求后续逻辑无残留）
        UserHolder.removeUser();

        return Result.ok(); // 返回注销成功
    }

    public String generateToken(User user) {

        // 7.保存用户信息到 redis中
        // 7.1.随机生成token，作为登录令牌
        String token = UUID.randomUUID().toString(true);
        // 7.2.将User对象转为HashMap存储
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        Map<String, Object> userMap = BeanUtil.beanToMap(userDTO, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));
        // 7.3.存储
        String tokenKey = LOGIN_USER_KEY + token;
        stringRedisTemplate.opsForHash().putAll(tokenKey, userMap);
        // 7.4.设置token有效期
        stringRedisTemplate.expire(tokenKey, LOGIN_USER_TTL, TimeUnit.MINUTES);
        return token;
    }


    @Override
    public Result sign() {
        // 1.获取当前登录用户
        Integer userId = UserHolder.getUser().getId();
        // 2.获取日期
        LocalDateTime now = LocalDateTime.now();
        // 3.拼接key
        String keySuffix = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        String key = USER_SIGN_KEY + userId + keySuffix;
        // 4.获取今天是本月的第几天
        int dayOfMonth = now.getDayOfMonth();
        // 5.写入Redis SETBIT key offset 1
        stringRedisTemplate.opsForValue().setBit(key, dayOfMonth - 1, true);
        return Result.ok();
    }

    @Override
    public Result signCount() {
        // 1.获取当前登录用户
        Integer userId = UserHolder.getUser().getId();
        // 2.获取日期
        LocalDateTime now = LocalDateTime.now();
        // 3.拼接key
        String keySuffix = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        String key = USER_SIGN_KEY + userId + keySuffix;
        // 4.获取今天是本月的第几天
        int dayOfMonth = now.getDayOfMonth();
        // 5.获取本月截止今天为止的所有的签到记录，返回的是一个十进制的数字 BITFIELD sign:5:202203 GET u14 0
        List<Long> result = stringRedisTemplate.opsForValue().bitField(
                key,
                BitFieldSubCommands.create()
                        .get(BitFieldSubCommands.BitFieldType.unsigned(dayOfMonth)).valueAt(0)
        );
        if (result == null || result.isEmpty()) {
            // 没有任何签到结果
            return Result.ok(0);
        }
        Long num = result.get(0);
        if (num == null || num == 0) {
            return Result.ok(0);
        }
        // 6.循环遍历
        int count = 0;
        while (true) {
            // 6.1.让这个数字与1做与运算，得到数字的最后一个bit位  // 判断这个bit位是否为0
            if ((num & 1) == 0) {
                // 如果为0，说明未签到，结束
                break;
            }else {
                // 如果不为0，说明已签到，计数器+1
                count++;
            }
            // 把数字右移一位，抛弃最后一个bit位，继续下一个bit位
            num >>>= 1;
        }
        return Result.ok(count);
    }

    @Transactional
    public User createUserWithPhone(String phone) {
        User user = new User();
        user.setPhone(phone);
        user.setNickName(USER_NICK_NAME_PREFIX + RandomUtil.randomString(10));
        save(user);

        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfoService.addUserInfo(userInfo);

        return user;
    }


    private Result verifyPhoneAndUser(LoginFormDTO loginForm) {
        // 檢查 loginForm 是否為 null
        if (loginForm == null) {
            return Result.fail("登錄參數不能為空");
        }
        
        // 1. 校驗手機號
        String phone = loginForm.getPhone();
        if (StrUtil.isBlank(phone)) {
            return Result.fail("手機號不能為空");
        }
        
        if (RegexUtils.isPhoneInvalid(phone)) {
            // 2. 如果不符合，返回錯誤信息
            return Result.fail("手機號格式錯誤！");
        }
        
        // 3. 根據手機號查詢用戶
        User user = query().eq("phone", phone).one();
        
        // 4. 判斷用戶是否存在
        if (user == null) {
            // 5. 不存在，創建新用戶並保存
            user = createUserWithPhone(phone);
        }
        
        // 6. 判斷是驗證碼登錄還是密碼登錄
        String code = loginForm.getCode();
        String password = loginForm.getPassword();
        
        if (StrUtil.isNotBlank(code)) {
            // 驗證碼登錄
            // 從 redis 獲取驗證碼並校驗
            String cacheCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + phone);
            if (cacheCode == null || !cacheCode.equals(code)) {
                // 不一致，報錯
                return Result.fail("驗證碼錯誤");
            }
            // 清理已使用的驗證碼
            stringRedisTemplate.delete(LOGIN_CODE_KEY + phone);
        } else if (StrUtil.isNotBlank(password)) {
            // 密碼登錄
            // 這裡需要實現密碼驗證邏輯
            // 如果您的系統沒有存儲密碼，可以跳過密碼驗證
            // 或者實現一個簡單的密碼驗證邏輯
            if (!password.equals("123456")) { // 假設默認密碼是 123456
                return Result.fail("密碼錯誤");
            }
        } else {
            // 既沒有驗證碼也沒有密碼
            return Result.fail("請提供驗證碼或密碼");
        }
        
        // 7. 返回成功結果，攜帶用戶信息
        return Result.ok(user);
    }
}
