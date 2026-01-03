package com.fitness.system.service.impl;

import com.fitness.system.entity.User;
import com.fitness.system.mapper.UserMapper;
import com.fitness.system.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户Service实现类
 */
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User selectUserByUserId(Long userId) {
        return userMapper.selectUserByUserId(userId);
    }

    @Override
    public User selectUserByUsername(String username) {
        return userMapper.selectUserByUsername(username);
    }

    @Override
    public List<User> selectUserList() {
        return userMapper.selectUserList();
    }

    @Override
    public int insertUser(User user) {
        return userMapper.insertUser(user);
    }

    @Override
    public int updateUser(User user) {
        return userMapper.updateUser(user);
    }

    @Override
    public int deleteUserByUserId(Long userId) {
        return userMapper.deleteUserByUserId(userId);
    }

    @Override
    public int deleteUserByUserIds(Long[] userIds) {
        return userMapper.deleteUserByUserIds(userIds);
    }

    @Override
    public boolean checkUsernameUnique(String username) {
        User user = userMapper.selectUserByUsername(username);
        return user == null;
    }

}