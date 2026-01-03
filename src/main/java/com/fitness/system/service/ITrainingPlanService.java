package com.fitness.system.service;

import com.fitness.system.entity.TrainingPlan;
import com.fitness.system.entity.TrainingPlanExercise;
import java.util.List;

/**
 * 训练计划Service接口
 */
public interface ITrainingPlanService {
    /**
     * 根据训练计划ID查询训练计划
     * @param planId 训练计划ID
     * @return 训练计划信息
     */
    TrainingPlan selectTrainingPlanById(Long planId);

    /**
     * 根据学员ID查询训练计划列表
     * @param userId 学员ID
     * @return 训练计划列表
     */
    List<TrainingPlan> selectTrainingPlansByStudentId(Long userId);

    /**
     * 根据教练ID查询训练计划列表
     * @param coachId 教练ID
     * @return 训练计划列表
     */
    List<TrainingPlan> selectTrainingPlansByCoachId(Long coachId);

    /**
     * 查询所有训练计划
     * @return 训练计划列表
     */
    List<TrainingPlan> selectTrainingPlanList();

    /**
     * 新增训练计划
     * @param plan 训练计划信息
     * @return 结果
     */
    int insertTrainingPlan(TrainingPlan plan);

    /**
     * 修改训练计划
     * @param plan 训练计划信息
     * @return 结果
     */
    int updateTrainingPlan(TrainingPlan plan);

    /**
     * 删除训练计划
     * @param planId 训练计划ID
     * @return 结果
     */
    int deleteTrainingPlanById(Long planId);

    /**
     * 批量删除训练计划
     * @param planIds 需要删除的训练计划ID列表
     * @return 结果
     */
    int deleteTrainingPlanByIds(Long[] planIds);
    
    /**
     * 根据训练计划ID查询运动项列表
     * @param planId 训练计划ID
     * @return 运动项列表
     */
    List<TrainingPlanExercise> selectExercisesByPlanId(Long planId);
    
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
    int batchInsertTrainingPlanExercises(List<TrainingPlanExercise> exercises);
    
    /**
     * 根据训练计划ID删除运动项
     * @param planId 训练计划ID
     * @return 结果
     */
    int deleteExercisesByPlanId(Long planId);
}
