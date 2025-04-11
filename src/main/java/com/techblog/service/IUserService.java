package com.techblog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.techblog.dto.LoginFormDTO;
import com.techblog.dto.Result;
import com.techblog.entity.User;

import javax.servlet.http.HttpSession;

public interface IUserService extends IService<User> {

    Result sendCode(String phone, HttpSession session);

    Result sendCode2(String phone, HttpSession session);

    Result login(LoginFormDTO loginForm, HttpSession session);

    Result login2(LoginFormDTO loginForm, HttpSession session);

    Result sign();

    Result signCount();

}
