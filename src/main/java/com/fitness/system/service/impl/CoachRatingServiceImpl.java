package com.fitness.system.service.impl;

import com.fitness.system.entity.CoachRating;
import com.fitness.system.mapper.CoachRatingMapper;
import com.fitness.system.service.ICoachRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoachRatingServiceImpl implements ICoachRatingService {
    
    @Autowired
    private CoachRatingMapper coachRatingMapper;
    
    @Override
    public int submitCoachRating(CoachRating coachRating) {
        return coachRatingMapper.insertCoachRating(coachRating);
    }
    
    @Override
    public List<CoachRating> getCoachRatings(Long coachId) {
        return coachRatingMapper.selectCoachRatingsByCoachId(coachId);
    }
    
    @Override
    public Double getAverageRating(Long coachId) {
        return coachRatingMapper.selectAverageRatingByCoachId(coachId);
    }
}