package com.techblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.techblog.entity.User;


//BaseMapper 提供的部分常用方法如下：
//        selectById(Serializable id)：根据主键 ID 查询记录
//        insert(T entity)：插入一条记录
//        updateById(T entity)：根据主键 ID 更新记录
//        deleteById(Serializable id)：根据主键 ID 删除记录
public interface UserMapper extends BaseMapper<User> {

}
