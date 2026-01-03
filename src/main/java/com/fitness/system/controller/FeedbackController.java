package com.fitness.system.controller;

import com.fitness.system.entity.Feedback;
import com.fitness.system.service.IFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 训练反馈Controller
 */
@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private IFeedbackService feedbackService;

    /**
     * 根据ID查询训练反馈
     * @param feedbackId 训练反馈ID
     * @return 训练反馈信息
     */
    @GetMapping("/{feedbackId}")
    public ResponseEntity<Feedback> getFeedbackById(@PathVariable Long feedbackId) {
        Feedback feedback = feedbackService.selectFeedbackById(feedbackId);
        if (feedback == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(feedback);
    }

    /**
     * 根据学员ID查询训练反馈列表
     * @param userId 学员ID
     * @return 训练反馈列表
     */
    @GetMapping("/student/{userId}")
    public ResponseEntity<List<Feedback>> getFeedbacksByStudentId(@PathVariable Long userId) {
        List<Feedback> feedbacks = feedbackService.selectFeedbacksByStudentId(userId);
        return ResponseEntity.ok(feedbacks);
    }

    /**
     * 根据训练计划ID查询训练反馈列表
     * @param planId 训练计划ID
     * @return 训练反馈列表
     */
    @GetMapping("/plan/{planId}")
    public ResponseEntity<List<Feedback>> getFeedbacksByPlanId(@PathVariable Long planId) {
        List<Feedback> feedbacks = feedbackService.selectFeedbacksByPlanId(planId);
        return ResponseEntity.ok(feedbacks);
    }

    /**
     * 根据教练ID查询训练反馈列表
     * @param coachId 教练ID
     * @return 训练反馈列表
     */
    @GetMapping("/coach/{coachId}")
    public ResponseEntity<List<Feedback>> getFeedbacksByCoachId(@PathVariable Long coachId) {
        List<Feedback> feedbacks = feedbackService.selectFeedbacksByCoachId(coachId);
        return ResponseEntity.ok(feedbacks);
    }

    /**
     * 查询所有训练反馈
     * @return 训练反馈列表
     */
    @GetMapping
    public ResponseEntity<List<Feedback>> getAllFeedbacks() {
        List<Feedback> feedbacks = feedbackService.selectFeedbackList();
        return ResponseEntity.ok(feedbacks);
    }

    /**
     * 创建训练反馈
     * @param feedback 训练反馈信息
     * @return 结果
     */
    @PostMapping
    public ResponseEntity<Integer> createFeedback(@RequestBody Feedback feedback) {
        int result = feedbackService.insertFeedback(feedback);
        return ResponseEntity.status(201).body(result);
    }

    /**
     * 更新训练反馈
     * @param feedbackId 训练反馈ID
     * @param feedback 训练反馈信息
     * @return 结果
     */
    @PutMapping("/{feedbackId}")
    public ResponseEntity<Integer> updateFeedback(@PathVariable Long feedbackId, @RequestBody Feedback feedback) {
        feedback.setFeedbackId(feedbackId);
        int result = feedbackService.updateFeedback(feedback);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除训练反馈
     * @param feedbackId 训练反馈ID
     * @return 结果
     */
    @DeleteMapping("/{feedbackId}")
    public ResponseEntity<Integer> deleteFeedback(@PathVariable Long feedbackId) {
        int result = feedbackService.deleteFeedbackById(feedbackId);
        return ResponseEntity.ok(result);
    }

    /**
     * 批量删除训练反馈
     * @param feedbackIds 需要删除的训练反馈ID列表
     * @return 结果
     */
    @DeleteMapping
    public ResponseEntity<Integer> deleteFeedbacks(@RequestBody Long[] feedbackIds) {
        int result = feedbackService.deleteFeedbackByIds(feedbackIds);
        return ResponseEntity.ok(result);
    }
}
