package com.fitness.system.mapper;

import com.fitness.system.entity.TrainingPlanExercise;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 训练计划运动项Mapper接口
 */
@Mapper
public interface TrainingPlanExerciseMapper {
    /**
     * 根据训练计划ID查询运动项列表
     * @param planId 训练计划ID
     * @return 运动项列表
     */
    List<TrainingPlanExercise> selectExercisesByPlanId(@Param("planId") Long planId);
    
    /**
     * 新增训练计划运动项
     * @param exercise 训练计划运动项信息
     * @return 结果
     */
    int insertTrainingPlanExercise(TrainingPlanExercise exercise);
    
    /**
     * 批量新增训练计划运动项
     * @param exercises 训练计划运动项列表
     * @return 结果
     */
    int batchInsertTrainingPlanExercises(@Param("exercises") List<TrainingPlanExercise> exercises);
    
    /**
     * 根据训练计划ID删除运动项
     * @param planId 训练计划ID
     * @return 结果
     */
    int deleteExercisesByPlanId(@Param("planId") Long planId);
}