package com.fitness.system.controller;

import com.fitness.system.entity.Coach;
import com.fitness.system.service.ICoachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教练Controller
 */
@RestController
@RequestMapping("/coaches")
public class CoachController {

    @Autowired
    private ICoachService coachService;

    /**
     * 根据ID查询教练
     * @param coachId 教练ID
     * @return 教练信息
     */
    @GetMapping("/{coachId}")
    public ResponseEntity<Coach> getCoachById(@PathVariable Long coachId) {
        Coach coach = coachService.selectCoachByCoachId(coachId);
        if (coach == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(coach);
    }

    /**
     * 根据用户名查询教练
     * @param username 用户名
     * @return 教练信息
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<Coach> getCoachByUsername(@PathVariable String username) {
        Coach coach = coachService.selectCoachByUsername(username);
        if (coach == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(coach);
    }

    /**
     * 查询所有教练（管理员用）
     * @return 教练列表
     */
    @GetMapping
    public ResponseEntity<List<Coach>> getAllCoaches() {
        List<Coach> coaches = coachService.selectCoachList();
        return ResponseEntity.ok(coaches);
    }

    /**
     * 查询已验证且未删除的教练（用户可见）
     * @return 教练列表
     */
    @GetMapping("/valid")
    public ResponseEntity<List<Coach>> getValidCoaches() {
        List<Coach> coaches = coachService.selectValidCoachList();
        return ResponseEntity.ok(coaches);
    }

    /**
     * 创建教练
     * @param coach 教练信息
     * @return 结果
     */
    @PostMapping
    public ResponseEntity<Integer> createCoach(@RequestBody Coach coach) {
        int result = coachService.insertCoach(coach);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * 更新教练
     * @param coachId 教练ID
     * @param coach 教练信息
     * @return 结果
     */
    @PutMapping("/{coachId}")
    public ResponseEntity<Integer> updateCoach(@PathVariable Long coachId, @RequestBody Coach coach) {
        coach.setCoachId(coachId);
        int result = coachService.updateCoach(coach);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除教练
     * @param coachId 教练ID
     * @return 结果
     */
    @DeleteMapping("/{coachId}")
    public ResponseEntity<Integer> deleteCoach(@PathVariable Long coachId) {
        int result = coachService.deleteCoachByCoachId(coachId);
        return ResponseEntity.ok(result);
    }
}
