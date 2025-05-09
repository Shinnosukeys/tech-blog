package com.techblog.controller.user;


import cn.hutool.core.bean.BeanUtil;
import com.techblog.dto.Result;
import com.techblog.dto.UserDTO;
import com.techblog.dto.Request;
import com.techblog.dto.LoginFormDTO;
import com.techblog.entity.user.User;
import com.techblog.entity.user.UserInfo;
import com.techblog.service.IUserInfoService;
import com.techblog.service.IUserService;
import com.techblog.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;

    @Resource
    private IUserInfoService userInfoService;

    /**
     * 发送手机验证码
     */
    @PostMapping("/code")
    public Result sendCode(@RequestParam("phone") String phone) {
        // 发送短信验证码并保存验证码
        return userService.sendCode(phone);
    }

    /**
     * 登录功能
     * @param request 登录参数，包含手机号、验证码；或者手机号、密码
     */
    @PostMapping("/login")
    public Result login(@RequestBody Request request){
        // 檢查 loginFormDTO 是否為 null
        if (request == null || request.getLoginFormDTO() == null) {
            return Result.fail("登錄參數不能為空");
        }
        
        // 實現登錄功能
        return userService.loginByJWT(request.getLoginFormDTO());
    }
    
    /**
     * 直接使用 LoginFormDTO 登錄
     */
    @PostMapping("/login/direct")
    public Result loginDirect(@RequestBody LoginFormDTO loginFormDTO) {
        // 檢查 loginFormDTO 是否為 null
        if (loginFormDTO == null) {
            return Result.fail("登錄參數不能為空");
        }
        
        // 實現登錄功能
        return userService.loginByJWT(loginFormDTO);
    }

    /**
     * 登出功能
     * @return 无
     */
    @PostMapping("/logout")
    public Result logout(HttpServletRequest request){
        return userService.logout(request);
    }

    @GetMapping("/me")
    public Result me(){
        // 获取当前登录的用户并返回
        UserDTO user = UserHolder.getUser();
        return Result.ok(user);
    }

    @GetMapping("/info/{id}")
    public Result info(@PathVariable("id") Long userId){
        // 查询详情
        UserInfo info = userInfoService.getById(userId);
        if (info == null) {
            // 没有详情，应该是第一次查看详情
            return Result.ok();
        }
        info.setCreateTime(null);
        info.setUpdateTime(null);
        // 返回
        return Result.ok(info);
    }

    @GetMapping("/{id}")
    public Result queryUserById(@PathVariable("id") Long userId){
        // 查询详情
        User user = userService.getById(userId);
        if (user == null) {
            return Result.ok();
        }
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        // 返回
        return Result.ok(userDTO);
    }

    @PostMapping("/sign")
    public Result sign(){
        return userService.sign();
    }

    @GetMapping("/sign/count")
    public Result signCount(){
        return userService.signCount();
    }
}