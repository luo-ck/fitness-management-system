package com.fitness.system.mapper;

import com.fitness.system.entity.TrainingPlan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 训练计划Mapper接口
 */
@Mapper
public interface TrainingPlanMapper {
    /**
     * 根据训练计划ID查询训练计划
     * @param planId 训练计划ID
     * @return 训练计划信息
     */
    TrainingPlan selectTrainingPlanById(@Param("planId") Long planId);

    /**
     * 根据学员ID查询训练计划列表
     * @param userId 学员ID
     * @return 训练计划列表
     */
    List<TrainingPlan> selectTrainingPlansByStudentId(@Param("userId") Long userId);

    /**
     * 根据教练ID查询训练计划列表
     * @param coachId 教练ID
     * @return 训练计划列表
     */
    List<TrainingPlan> selectTrainingPlansByCoachId(@Param("coachId") Long coachId);

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
     * 根据训练计划ID删除训练计划
     * @param planId 训练计划ID
     * @return 结果
     */
    int deleteTrainingPlanById(@Param("planId") Long planId);

    /**
     * 批量删除训练计划
     * @param planIds 需要删除的训练计划ID列表
     * @return 结果
     */
    int deleteTrainingPlanByIds(@Param("planIds") Long[] planIds);
}
