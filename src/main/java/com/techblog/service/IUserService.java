package com.techblog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.techblog.dto.LoginFormDTO;
import com.techblog.dto.Result;
import com.techblog.entity.User;

import javax.servlet.http.HttpServletRequest;

public interface IUserService extends IService<User> {

    Result sendCode(String phone);

    Result login(LoginFormDTO loginForm);

    Result loginByJWT(LoginFormDTO loginForm);

    Result logout(HttpServletRequest request);

    Result sign();

    Result signCount();

}
