package com.techblog.controller;

import com.techblog.dto.Result;
import com.techblog.dto.UserDTO;
import com.techblog.entity.UserInfo;
import com.techblog.service.IUserInfoService;
import com.techblog.utils.UserHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/userInfo")
public class UserInfoController {

    @Resource
    private IUserInfoService userInfoService;

    @PostMapping("/add")
    public Result addUserInfo(@RequestBody UserInfo userInfo) {
        return userInfoService.addUserInfo(userInfo);
    }

    @DeleteMapping("/delete/{userId}")
    public Result deleteUserInfo(@PathVariable Long userId) {
        return Result.ok(userInfoService.removeById(userId));
    }

    @PutMapping("/update")
    public Result updateUserInfo(@RequestBody UserInfo userInfo) {
        return userInfoService.updateUserInfo(userInfo);
    }

    @GetMapping("/get/{userId}")
    public UserInfo getUserInfo(@PathVariable Long userId) {
        return userInfoService.getById(userId);
    }

    @GetMapping("/list")
    public List<UserInfo> listUserInfo() {
        return userInfoService.list();
    }
}