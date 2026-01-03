package com.fitness.system.controller;

import com.fitness.system.entity.CoachRating;
import com.fitness.system.service.ICoachRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教练评价Controller
 */
@RestController
@RequestMapping("/coach-ratings")
public class CoachRatingController {
    
    @Autowired
    private ICoachRatingService coachRatingService;
    
    /**
     * 提交教练评价
     * @param coachRating 教练评价信息
     * @return 结果
     */
    @PostMapping
    public ResponseEntity<Integer> submitCoachRating(@RequestBody CoachRating coachRating) {
        int result = coachRatingService.submitCoachRating(coachRating);
        return ResponseEntity.status(201).body(result);
    }
    
    /**
     * 获取教练的评价列表
     * @param coachId 教练ID
     * @return 评价列表
     */
    @GetMapping("/coach/{coachId}")
    public ResponseEntity<List<CoachRating>> getCoachRatings(@PathVariable Long coachId) {
        List<CoachRating> ratings = coachRatingService.getCoachRatings(coachId);
        return ResponseEntity.ok(ratings);
    }
    
    /**
     * 获取教练的平均评分
     * @param coachId 教练ID
     * @return 平均评分
     */
    @GetMapping("/coach/{coachId}/average")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long coachId) {
        Double averageRating = coachRatingService.getAverageRating(coachId);
        return ResponseEntity.ok(averageRating);
    }
}