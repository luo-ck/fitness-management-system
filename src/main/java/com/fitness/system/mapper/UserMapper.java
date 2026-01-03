package com.fitness.system.mapper;

import com.fitness.system.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    /**
     * 根据用户ID查询用户
     * @param userId 用户ID
     * @return 用户信息
     */
    User selectUserByUserId(Long userId);

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    User selectUserByUsername(String username);

    /**
     * 查询所有用户
     * @return 用户列表
     */
    List<User> selectUserList();

    /**
     * 新增用户
     * @param user 用户信息
     * @return 结果
     */
    int insertUser(User user);

    /**
     * 修改用户
     * @param user 用户信息
     * @return 结果
     */
    int updateUser(User user);

    /**
     * 删除用户
     * @param userId 用户ID
     * @return 结果
     */
    int deleteUserByUserId(Long userId);

    /**
     * 批量删除用户
     * @param userIds 需要删除的用户ID列表
     * @return 结果
     */
    int deleteUserByUserIds(Long[] userIds);

    /**
     * 检查用户名是否唯一
     * @param username 用户名
     * @return 结果
     */
    User checkUsernameUnique(String username);
}