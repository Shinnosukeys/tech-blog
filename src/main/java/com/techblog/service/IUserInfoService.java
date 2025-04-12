package com.techblog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.techblog.dto.Result;
import com.techblog.entity.UserInfo;


public interface IUserInfoService extends IService<UserInfo> {

    Result addUserInfo(UserInfo userInfo);

    Result updateUserInfo(UserInfo userInfo);


}
