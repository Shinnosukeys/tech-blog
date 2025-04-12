package com.techblog.utils;

import com.techblog.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserHolder {
    private static final ThreadLocal<UserDTO> tl = new ThreadLocal<>();

    public static void saveUser(UserDTO user){
        log.info(Thread.currentThread().getName());
        tl.set(user);
    }

    public static UserDTO getUser(){
        log.info(Thread.currentThread().getName());
        return tl.get();
    }

    public static void removeUser(){
        tl.remove();
    }
}
