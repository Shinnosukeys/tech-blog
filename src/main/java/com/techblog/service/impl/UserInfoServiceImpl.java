package com.techblog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.techblog.dto.Result;
import com.techblog.dto.UserDTO;
import com.techblog.entity.user.UserInfo;
import com.techblog.mapper.UserInfoMapper;
import com.techblog.service.IUserInfoService;
import com.techblog.utils.UserHolder;
import org.springframework.stereotype.Service;


@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

    @Override
    public Result addUserInfo(UserInfo userInfo) {
        UserDTO userDto = UserHolder.getUser();
        if (userDto != null) {
            userInfo.setUserId(userDto.getId());
        }
        try {
            // 尝试保存 UserInfo 信息
            boolean saveResult = save(userInfo);
            if (saveResult) {
                // 保存成功，返回成功结果
                return Result.ok();
            } else {
                // 保存失败，返回自定义失败信息
                return Result.fail("保存用户信息失败，请稍后重试。");
            }
        } catch (Exception e) {
            // 捕获异常，返回异常信息
            return Result.fail("保存用户信息时出现异常：" + e.getMessage());
        }
    }

    @Override
    public Result updateUserInfo(UserInfo userInfo) {
        UserDTO userDto = UserHolder.getUser();
        userInfo.setUserId(userDto.getId());
        try {
            // 尝试保存 UserInfo 信息
            boolean saveResult = updateById(userInfo);
            if (saveResult) {
                // 保存成功，返回成功结果
                return Result.ok();
            } else {
                // 保存失败，返回自定义失败信息
                return Result.fail("更新用户信息失败，请稍后重试。");
            }
        } catch (Exception e) {
            // 捕获异常，返回异常信息
            return Result.fail("更新用户信息时出现异常：" + e.getMessage());
        }
    }
}
