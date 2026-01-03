package com.fitness.system.service.impl;

import com.fitness.system.entity.Message;
import com.fitness.system.entity.TrainingPlan;
import com.fitness.system.entity.TrainingPlanExercise;
import com.fitness.system.mapper.TrainingPlanExerciseMapper;
import com.fitness.system.mapper.TrainingPlanMapper;
import com.fitness.system.service.IMessageService;
import com.fitness.system.service.ITrainingPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 训练计划Service实现类
 */
@Service
public class TrainingPlanServiceImpl implements ITrainingPlanService {

    @Autowired
    private TrainingPlanMapper trainingPlanMapper;
    
    @Autowired
    private TrainingPlanExerciseMapper trainingPlanExerciseMapper;
    
    @Autowired
    private IMessageService messageService;

    @Override
    public TrainingPlan selectTrainingPlanById(Long planId) {
        return trainingPlanMapper.selectTrainingPlanById(planId);
    }

    @Override
    public List<TrainingPlan> selectTrainingPlansByStudentId(Long userId) {
        return trainingPlanMapper.selectTrainingPlansByStudentId(userId);
    }

    @Override
    public List<TrainingPlan> selectTrainingPlansByCoachId(Long coachId) {
        return trainingPlanMapper.selectTrainingPlansByCoachId(coachId);
    }

    @Override
    public List<TrainingPlan> selectTrainingPlanList() {
        return trainingPlanMapper.selectTrainingPlanList();
    }

    @Override
    public int insertTrainingPlan(TrainingPlan plan) {
        System.out.println("=== 开始创建训练计划 ===");
        System.out.println("计划ID: " + plan.getPlanId());
        System.out.println("用户ID: " + plan.getUserId());
        System.out.println("教练ID: " + plan.getCoachId());
        System.out.println("计划日期: " + plan.getPlanDate());
        System.out.println("运动项数量: " + (plan.getExercises() != null ? plan.getExercises().size() : 0));
        
        try {
            int result = trainingPlanMapper.insertTrainingPlan(plan);
            System.out.println("训练计划插入结果: " + result);
            System.out.println("插入后生成的计划ID: " + plan.getPlanId());
            
            // 如果有运动项，处理运动项
            if (plan.getExercises() != null && !plan.getExercises().isEmpty()) {
                System.out.println("开始处理运动项");
                // 将生成的planId赋值给每个运动项
                for (TrainingPlanExercise exercise : plan.getExercises()) {
                    exercise.setPlanId(plan.getPlanId());
                    System.out.println("运动项详情: ExerciseId=" + exercise.getExerciseId() + ", Sets=" + exercise.getSets() + ", Reps=" + exercise.getReps());
                }
                // 批量添加新的运动项
                int exerciseResult = trainingPlanExerciseMapper.batchInsertTrainingPlanExercises(plan.getExercises());
                System.out.println("运动项批量插入结果: " + exerciseResult);
            }
            
            System.out.println("=== 训练计划创建成功 ===");
            
            // 发送训练计划消息给用户
            sendTrainingPlanMessage(plan, "创建");
            
            return result;
        } catch (Exception e) {
            System.out.println("=== 训练计划创建失败 ===");
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public int updateTrainingPlan(TrainingPlan plan) {
        System.out.println("=== 更新训练计划开始 ===");
        System.out.println("计划ID: " + plan.getPlanId());
        System.out.println("用户ID: " + plan.getUserId());
        System.out.println("教练ID: " + plan.getCoachId());
        
        int result = trainingPlanMapper.updateTrainingPlan(plan);
        
        // 如果有运动项，处理运动项
        if (plan.getExercises() != null) {
            System.out.println("运动项数量: " + plan.getExercises().size());
            
            if (!plan.getExercises().isEmpty()) {
                System.out.println("开始处理运动项...");
                // 先删除该计划下的所有现有运动项
                int deleteCount = trainingPlanExerciseMapper.deleteExercisesByPlanId(plan.getPlanId());
                System.out.println("删除现有运动项数量: " + deleteCount);
                
                // 确保每个运动项都有正确的planId
                for (TrainingPlanExercise exercise : plan.getExercises()) {
                    exercise.setPlanId(plan.getPlanId());
                    System.out.println("运动项详情: ID=" + exercise.getId() + ", ExerciseId=" + exercise.getExerciseId() + ", Sets=" + exercise.getSets() + ", Reps=" + exercise.getReps());
                }
                
                // 再批量添加新的运动项
                int insertCount = trainingPlanExerciseMapper.batchInsertTrainingPlanExercises(plan.getExercises());
                System.out.println("批量插入运动项数量: " + insertCount);
            }
        } else {
            System.out.println("运动项为null");
        }
        
        System.out.println("=== 更新训练计划结束 ===");
        
        // 发送训练计划消息给用户
        sendTrainingPlanMessage(plan, "更新");
        
        return result;
    }
    
    /**
     * 发送训练计划消息给用户
     * @param plan 训练计划
     * @param action 操作类型（创建/更新）
     */
    private void sendTrainingPlanMessage(TrainingPlan plan, String action) {
        try {
            // 创建训练计划消息
            Message message = new Message();
            message.setSenderId(plan.getCoachId());
            message.setSenderName("系统"); // 后续可以从数据库获取教练名称
            message.setReceiverId(plan.getUserId());
            message.setReceiverType("user");
            message.setMessageType("training_plan");
            message.setTitle("训练计划" + action);
            message.setContent("教练" + action + "了一份训练计划，日期：" + plan.getPlanDate());
            message.setRelatedId(plan.getPlanId());
            message.setIsRead(false);
            message.setSendTime(new Date());
            
            // 发送消息
            messageService.sendMessage(message);
            System.out.println("=== 训练计划消息发送成功 ===");
            System.out.println("消息内容: " + message.getContent());
        } catch (Exception e) {
            System.out.println("=== 训练计划消息发送失败 ===");
            e.printStackTrace();
        }
    }

    @Override
    public int deleteTrainingPlanById(Long planId) {
        // 先删除关联的运动项
        trainingPlanExerciseMapper.deleteExercisesByPlanId(planId);
        // 再删除训练计划
        return trainingPlanMapper.deleteTrainingPlanById(planId);
    }

    @Override
    public int deleteTrainingPlanByIds(Long[] planIds) {
        // 批量删除关联的运动项
        for (Long planId : planIds) {
            trainingPlanExerciseMapper.deleteExercisesByPlanId(planId);
        }
        // 批量删除训练计划
        return trainingPlanMapper.deleteTrainingPlanByIds(planIds);
    }

    @Override
    public List<TrainingPlanExercise> selectExercisesByPlanId(Long planId) {
        return trainingPlanExerciseMapper.selectExercisesByPlanId(planId);
    }

    @Override
    public int insertTrainingPlanExercise(TrainingPlanExercise exercise) {
        return trainingPlanExerciseMapper.insertTrainingPlanExercise(exercise);
    }

    @Override
    public int batchInsertTrainingPlanExercises(List<TrainingPlanExercise> exercises) {
        return trainingPlanExerciseMapper.batchInsertTrainingPlanExercises(exercises);
    }

    @Override
    public int deleteExercisesByPlanId(Long planId) {
        return trainingPlanExerciseMapper.deleteExercisesByPlanId(planId);
    }
}
