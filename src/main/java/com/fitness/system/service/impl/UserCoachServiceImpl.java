package com.fitness.system.service.impl;

import com.fitness.system.entity.Coach;
import com.fitness.system.entity.User;
import com.fitness.system.entity.UserCoach;
import com.fitness.system.mapper.CoachMapper;
import com.fitness.system.mapper.UserCoachMapper;
import com.fitness.system.mapper.UserMapper;
import com.fitness.system.service.IUserCoachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户-教练关联Service实现类
 */
@Service
public class UserCoachServiceImpl implements IUserCoachService {

    @Autowired
    private UserCoachMapper userCoachMapper;

    @Autowired
    private CoachMapper coachMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<Coach> selectCoachesByUserId(Long userId) {
        List<UserCoach> userCoaches = userCoachMapper.selectCoachesByUserId(userId);
        List<Coach> coaches = new ArrayList<>();
        for (UserCoach userCoach : userCoaches) {
            Coach coach = coachMapper.selectCoachByCoachId(userCoach.getCoachId());
            if (coach != null) {
                coaches.add(coach);
            }
        }
        return coaches;
    }

    @Override
    public boolean checkUserCoachRelation(Long userId, Long coachId) {
        UserCoach userCoach = userCoachMapper.checkUserCoachRelation(userId, coachId);
        return userCoach != null;
    }

    @Override
    public int selectCoach(Long userId, Long coachId) {
        // 检查是否已经关联
        if (checkUserCoachRelation(userId, coachId)) {
            return 0; // 已经关联，无需重复操作
        }
        UserCoach userCoach = new UserCoach();
        userCoach.setUserId(userId);
        userCoach.setCoachId(coachId);
        return userCoachMapper.insertUserCoach(userCoach);
    }

    @Override
    public int unselectCoach(Long userId, Long coachId) {
        return userCoachMapper.deleteUserCoach(userId, coachId);
    }

    @Override
    public List<User> selectUsersByCoachId(Long coachId) {
        List<UserCoach> userCoaches = userCoachMapper.selectUsersByCoachId(coachId);
        List<User> users = new ArrayList<>();
        for (UserCoach userCoach : userCoaches) {
            User user = userMapper.selectUserByUserId(userCoach.getUserId());
            if (user != null) {
                users.add(user);
            }
        }
        return users;
    }
}
