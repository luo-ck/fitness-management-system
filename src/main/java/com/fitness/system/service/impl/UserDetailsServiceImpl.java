package com.fitness.system.service.impl;

import com.fitness.system.entity.Coach;
import com.fitness.system.entity.User;
import com.fitness.system.service.ICoachService;
import com.fitness.system.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private IUserService userService;
    
    @Autowired
    private ICoachService coachService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 先查询普通用户
        User user = userService.selectUserByUsername(username);
        
        // 如果不是普通用户，查询教练
        if (user == null) {
            Coach coach = coachService.selectCoachByUsername(username);
            if (coach == null) {
                throw new UsernameNotFoundException("用户不存在: " + username);
            }
            
            // 构建教练的UserDetails对象
            return new org.springframework.security.core.userdetails.User(
                    coach.getUsername(),
                    coach.getPasswordHash(),
                    Collections.emptyList()
            );
        }

        // 构建普通用户的UserDetails对象
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                Collections.emptyList()
        );
    }
}