package com.fitness.system.service.impl;

import com.fitness.system.entity.Feedback;
import com.fitness.system.entity.Message;
import com.fitness.system.entity.TrainingPlan;
import com.fitness.system.mapper.FeedbackMapper;
import com.fitness.system.service.IMessageService;
import com.fitness.system.service.IFeedbackService;
import com.fitness.system.service.ITrainingPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 训练反馈Service实现类
 */
@Service
public class FeedbackServiceImpl implements IFeedbackService {

    @Autowired
    private FeedbackMapper feedbackMapper;
    
    @Autowired
    private IMessageService messageService;

    @Override
    public Feedback selectFeedbackById(Long feedbackId) {
        return feedbackMapper.selectFeedbackById(feedbackId);
    }

    @Override
    public List<Feedback> selectFeedbacksByStudentId(Long userId) {
        return feedbackMapper.selectFeedbacksByStudentId(userId);
    }

    @Override
    public List<Feedback> selectFeedbacksByPlanId(Long planId) {
        return feedbackMapper.selectFeedbacksByPlanId(planId);
    }

    @Override
    public List<Feedback> selectFeedbacksByCoachId(Long coachId) {
        return feedbackMapper.selectFeedbacksByCoachId(coachId);
    }

    @Override
    public List<Feedback> selectFeedbackList() {
        return feedbackMapper.selectFeedbackList();
    }

    @Autowired
    private ITrainingPlanService trainingPlanService;

    @Override
    public int insertFeedback(Feedback feedback) {
        int result = feedbackMapper.insertFeedback(feedback);
        
        // 发送反馈消息给教练
        if (result > 0) {
            sendFeedbackMessage(feedback);
        }
        
        return result;
    }
    
    /**
     * 发送反馈消息给教练
     * @param feedback 训练反馈
     */
    private void sendFeedbackMessage(Feedback feedback) {
        try {
            // 获取训练计划，从而获取教练ID
            TrainingPlan plan = trainingPlanService.selectTrainingPlanById(feedback.getPlanId());
            if (plan != null && plan.getCoachId() != null) {
                // 创建反馈消息
                Message message = new Message();
                message.setSenderId(feedback.getUserId());
                message.setSenderName("系统"); // 后续可以从数据库获取用户名
                message.setReceiverId(plan.getCoachId());
                message.setReceiverType("coach");
                message.setMessageType("training_feedback");
                message.setTitle("训练反馈");
                // 优化消息内容，包含训练计划日期
                String planDateStr = plan.getPlanDate() != null ? plan.getPlanDate().toString() : "未知日期";
                message.setContent("训练计划 " + planDateStr + " 的反馈：" + feedback.getFeeling() + "|" + feedback.getRating() + "|" + feedback.getComments());
                message.setRelatedId(feedback.getPlanId());
                message.setIsRead(false);
                message.setSendTime(new Date());
                
                // 发送消息
                messageService.sendMessage(message);
                System.out.println("=== 反馈消息发送成功 ===");
                System.out.println("消息内容: " + message.getContent());
            }
        } catch (Exception e) {
            System.out.println("=== 反馈消息发送失败 ===");
            e.printStackTrace();
        }
    }

    @Override
    public int updateFeedback(Feedback feedback) {
        return feedbackMapper.updateFeedback(feedback);
    }

    @Override
    public int deleteFeedbackById(Long feedbackId) {
        return feedbackMapper.deleteFeedbackById(feedbackId);
    }

    @Override
    public int deleteFeedbackByIds(Long[] feedbackIds) {
        return feedbackMapper.deleteFeedbackByIds(feedbackIds);
    }
}
