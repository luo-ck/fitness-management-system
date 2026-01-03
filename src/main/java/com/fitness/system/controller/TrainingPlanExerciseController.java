package com.fitness.system.controller;

import com.fitness.system.entity.TrainingPlanExercise;
import com.fitness.system.service.ITrainingPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 训练计划运动项Controller
 */
@RestController
@RequestMapping("/training-plan-exercises")
public class TrainingPlanExerciseController {

    @Autowired
    private ITrainingPlanService trainingPlanService;

    /**
     * 根据训练计划ID删除所有运动项
     * @param planId 训练计划ID
     * @return 结果
     */
    @DeleteMapping("/plan/{planId}")
    public ResponseEntity<Integer> deleteExercisesByPlanId(@PathVariable Long planId) {
        int result = trainingPlanService.deleteExercisesByPlanId(planId);
        return ResponseEntity.ok(result);
    }

    /**
     * 批量添加训练计划运动项
     * @param request 请求体，包含计划ID和运动项列表
     * @return 结果
     */
    @PostMapping("/batch")
    public ResponseEntity<Integer> batchInsertExercises(@RequestBody BatchInsertRequest request) {
        // 为每个运动项设置计划ID
        for (TrainingPlanExercise exercise : request.getExercises()) {
            exercise.setPlanId(request.getPlanId());
        }
        int result = trainingPlanService.batchInsertTrainingPlanExercises(request.getExercises());
        return ResponseEntity.ok(result);
    }

    /**
     * 批量添加请求体
     */
    static class BatchInsertRequest {
        private Long planId;
        private List<TrainingPlanExercise> exercises;

        public Long getPlanId() {
            return planId;
        }

        public void setPlanId(Long planId) {
            this.planId = planId;
        }

        public List<TrainingPlanExercise> getExercises() {
            return exercises;
        }

        public void setExercises(List<TrainingPlanExercise> exercises) {
            this.exercises = exercises;
        }
    }
}