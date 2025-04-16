package com.techblog.controller.user;

import com.techblog.dto.Result;
import com.techblog.dto.Request;
import com.techblog.entity.user.UserInfo;
import com.techblog.service.IUserInfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/userInfo")
public class UserInfoController {

    @Resource
    private IUserInfoService userInfoService;

    @PostMapping("/add")
    public Result addUserInfo(@RequestBody Request request) {
        return userInfoService.addUserInfo(request.getUserInfo());
    }

    @DeleteMapping("/delete/{userId}")
    public Result deleteUserInfo(@PathVariable Long userId) {
        return Result.ok(userInfoService.removeById(userId));
    }

    @PutMapping("/update")
    public Result updateUserInfo(@RequestBody Request request) {
        return userInfoService.updateUserInfo(request.getUserInfo());
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