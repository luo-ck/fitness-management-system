package com.fitness.system.service.impl;

import com.fitness.system.entity.Coach;
import com.fitness.system.mapper.CoachMapper;
import com.fitness.system.service.ICoachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 教练Service实现类
 */
@Service
public class CoachServiceImpl implements ICoachService {

    @Autowired
    private CoachMapper coachMapper;

    @Override
    public Coach selectCoachByCoachId(Long coachId) {
        return coachMapper.selectCoachByCoachId(coachId);
    }

    @Override
    public Coach selectCoachByUsername(String username) {
        return coachMapper.selectCoachByUsername(username);
    }

    @Override
    public List<Coach> selectCoachList() {
        return coachMapper.selectCoachList();
    }

    @Override
    public List<Coach> selectValidCoachList() {
        return coachMapper.selectValidCoachList();
    }

    @Override
    public int insertCoach(Coach coach) {
        return coachMapper.insertCoach(coach);
    }

    @Override
    public int updateCoach(Coach coach) {
        return coachMapper.updateCoach(coach);
    }

    @Override
    public int deleteCoachByCoachId(Long coachId) {
        return coachMapper.deleteCoachByCoachId(coachId);
    }
}
