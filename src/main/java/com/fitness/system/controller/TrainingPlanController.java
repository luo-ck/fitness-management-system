package com.fitness.system.controller;

import com.fitness.system.entity.TrainingPlan;
import com.fitness.system.service.ITrainingPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 训练计划Controller
 */
@RestController
@RequestMapping("/training-plans")
public class TrainingPlanController {

    @Autowired
    private ITrainingPlanService trainingPlanService;

    /**
     * 根据ID查询训练计划
     * @param planId 训练计划ID
     * @return 训练计划信息
     */
    @GetMapping("/{planId}")
    public ResponseEntity<TrainingPlan> getTrainingPlanById(@PathVariable Long planId) {
        TrainingPlan plan = trainingPlanService.selectTrainingPlanById(planId);
        if (plan == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(plan);
    }

    /**
     * 根据学员ID查询训练计划列表
     * @param userId 学员ID
     * @return 训练计划列表
     */
    @GetMapping("/student/{userId}")
    public ResponseEntity<List<TrainingPlan>> getTrainingPlansByStudentId(@PathVariable Long userId) {
        List<TrainingPlan> plans = trainingPlanService.selectTrainingPlansByStudentId(userId);
        return ResponseEntity.ok(plans);
    }

    /**
     * 根据教练ID查询训练计划列表
     * @param coachId 教练ID
     * @return 训练计划列表
     */
    @GetMapping("/coach/{coachId}")
    public ResponseEntity<List<TrainingPlan>> getTrainingPlansByCoachId(@PathVariable Long coachId) {
        List<TrainingPlan> plans = trainingPlanService.selectTrainingPlansByCoachId(coachId);
        return ResponseEntity.ok(plans);
    }

    /**
     * 查询所有训练计划
     * @return 训练计划列表
     */
    @GetMapping
    public ResponseEntity<List<TrainingPlan>> getAllTrainingPlans() {
        List<TrainingPlan> plans = trainingPlanService.selectTrainingPlanList();
        return ResponseEntity.ok(plans);
    }

    /**
     * 创建训练计划
     * @param plan 训练计划信息
     * @return 结果
     */
    @PostMapping
    public ResponseEntity<Integer> createTrainingPlan(@RequestBody TrainingPlan plan) {
        int result = trainingPlanService.insertTrainingPlan(plan);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * 更新训练计划
     * @param planId 训练计划ID
     * @param plan 训练计划信息
     * @return 结果
     */
    @PutMapping("/{planId}")
    public ResponseEntity<Integer> updateTrainingPlan(@PathVariable Long planId, @RequestBody TrainingPlan plan) {
        plan.setPlanId(planId);
        int result = trainingPlanService.updateTrainingPlan(plan);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除训练计划
     * @param planId 训练计划ID
     * @return 结果
     */
    @DeleteMapping("/{planId}")
    public ResponseEntity<Integer> deleteTrainingPlan(@PathVariable Long planId) {
        int result = trainingPlanService.deleteTrainingPlanById(planId);
        return ResponseEntity.ok(result);
    }

    /**
     * 批量删除训练计划
     * @param planIds 训练计划ID列表
     * @return 结果
     */
    @DeleteMapping
    public ResponseEntity<Integer> deleteTrainingPlans(@RequestBody Long[] planIds) {
        int result = trainingPlanService.deleteTrainingPlanByIds(planIds);
        return ResponseEntity.ok(result);
    }
}
