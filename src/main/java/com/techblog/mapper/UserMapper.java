package com.techblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.techblog.entity.user.User;

// save(T entity)：将单个实体对象保存到数据库，若对象主键为空则执行插入操作。

// saveBatch(Collection<T> entityList)：批量保存实体对象到数据库，提高插入效率。

// removeById(Serializable id)：根据主键 ID 删除数据库中的记录。

// remove(Wrapper<T> queryWrapper)：根据条件删除数据库中的记录，queryWrapper 可用于构建复杂的查询条件。
// QueryWrapper<User> queryWrapper = new QueryWrapper<>();
// queryWrapper.eq("age", 25);
// boolean success = userService.remove(queryWrapper);

// removeByIds(Collection<? extends Serializable> idList)：根据主键 ID 列表批量删除数据库中的记录。
// List<Long> idList = Arrays.asList(1L, 2L, 3L);
// boolean success = userService.removeByIds(idList);

// updateById(T entity)：根据实体对象的主键 ID 更新数据库中的记录，只更新实体对象中不为空的字段。

// update(T entity, Wrapper<T> updateWrapper)：根据条件更新数据库中的记录，entity 为要更新的字段值，updateWrapper 用于指定更新条件。
// User user = new User();
// user.setAge(26);
// QueryWrapper<User> updateWrapper = new QueryWrapper<>();
// updateWrapper.eq("name", "John");
// boolean success = userService.update(user, updateWrapper);

// getById(Serializable id)：根据主键 ID 查询数据库中的记录，返回单个实体对象。

// listByIds(Collection<? extends Serializable> idList)：根据主键 ID 列表查询数据库中的记录，返回实体对象列表。

// list(Wrapper<T> queryWrapper)：根据条件查询数据库中的记录，返回实体对象列表，queryWrapper 可用于构建复杂的查询条件。
// QueryWrapper<User> queryWrapper = new QueryWrapper<>();
// queryWrapper.gt("age", 20);
// List<User> userList = userService.list(queryWrapper);

// page(IPage<T> page, Wrapper<T> queryWrapper)：根据条件进行分页查询，返回分页结果对象，page 用于指定分页信息，queryWrapper 用于指定查询条件。
// Page<User> page = new Page<>(1, 10); // 第一页，每页 10 条记录
// QueryWrapper<User> queryWrapper = new QueryWrapper<>();
// queryWrapper.like("name", "J");
// IPage<User> userPage = userService.page(page, queryWrapper);

public interface UserMapper extends BaseMapper<User> {

}

