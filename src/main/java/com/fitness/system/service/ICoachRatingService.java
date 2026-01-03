package com.fitness.system.service;

import com.fitness.system.entity.CoachRating;
import java.util.List;

public interface ICoachRatingService {
    /**
     * 提交教练评价
     * @param coachRating 教练评价信息
     * @return 结果
     */
    int submitCoachRating(CoachRating coachRating);
    
    /**
     * 获取教练的评价列表
     * @param coachId 教练ID
     * @return 评价列表
     */
    List<CoachRating> getCoachRatings(Long coachId);
    
    /**
     * 获取教练的平均评分
     * @param coachId 教练ID
     * @return 平均评分
     */
    Double getAverageRating(Long coachId);
}