package com.fitness.system.mapper;

import com.fitness.system.entity.CoachRating;
import java.util.List;

public interface CoachRatingMapper {
    /**
     * 插入教练评价
     * @param coachRating 教练评价信息
     * @return 结果
     */
    int insertCoachRating(CoachRating coachRating);
    
    /**
     * 根据教练ID查询评价列表
     * @param coachId 教练ID
     * @return 评价列表
     */
    List<CoachRating> selectCoachRatingsByCoachId(Long coachId);
    
    /**
     * 根据教练ID查询平均评分
     * @param coachId 教练ID
     * @return 平均评分
     */
    Double selectAverageRatingByCoachId(Long coachId);
}