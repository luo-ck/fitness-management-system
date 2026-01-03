package com.fitness.system.mapper;

import com.fitness.system.entity.Feedback;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 训练反馈Mapper接口
 */
@Mapper
public interface FeedbackMapper {
    /**
     * 根据训练反馈ID查询训练反馈
     * @param feedbackId 训练反馈ID
     * @return 训练反馈信息
     */
    Feedback selectFeedbackById(@Param("feedbackId") Long feedbackId);

    /**
     * 根据学员ID查询训练反馈列表
     * @param userId 学员ID
     * @return 训练反馈列表
     */
    List<Feedback> selectFeedbacksByStudentId(@Param("userId") Long userId);

    /**
     * 根据训练计划ID查询训练反馈列表
     * @param planId 训练计划ID
     * @return 训练反馈列表
     */
    List<Feedback> selectFeedbacksByPlanId(@Param("planId") Long planId);

    /**
     * 根据教练ID查询训练反馈列表
     * @param coachId 教练ID
     * @return 训练反馈列表
     */
    List<Feedback> selectFeedbacksByCoachId(@Param("coachId") Long coachId);

    /**
     * 查询所有训练反馈
     * @return 训练反馈列表
     */
    List<Feedback> selectFeedbackList();

    /**
     * 新增训练反馈
     * @param feedback 训练反馈信息
     * @return 结果
     */
    int insertFeedback(Feedback feedback);

    /**
     * 修改训练反馈
     * @param feedback 训练反馈信息
     * @return 结果
     */
    int updateFeedback(Feedback feedback);

    /**
     * 根据训练反馈ID删除训练反馈
     * @param feedbackId 训练反馈ID
     * @return 结果
     */
    int deleteFeedbackById(@Param("feedbackId") Long feedbackId);

    /**
     * 批量删除训练反馈
     * @param feedbackIds 需要删除的训练反馈ID列表
     * @return 结果
     */
    int deleteFeedbackByIds(@Param("feedbackIds") Long[] feedbackIds);
}
