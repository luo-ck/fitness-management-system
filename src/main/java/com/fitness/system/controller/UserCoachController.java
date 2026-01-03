package com.fitness.system.controller;

import com.fitness.system.entity.Coach;
import com.fitness.system.entity.User;
import com.fitness.system.service.IUserCoachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户-教练关联Controller
 */
@RestController
@RequestMapping("/user-coaches")
public class UserCoachController {

    @Autowired
    private IUserCoachService userCoachService;

    /**
     * 查询用户关联的教练
     * @param userId 用户ID
     * @return 教练列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Coach>> getCoachesByUserId(@PathVariable Long userId) {
        List<Coach> coaches = userCoachService.selectCoachesByUserId(userId);
        return ResponseEntity.ok(coaches);
    }

    /**
     * 查询教练关联的用户
     * @param coachId 教练ID
     * @return 用户列表
     */
    @GetMapping("/coach/{coachId}")
    public ResponseEntity<List<User>> getUsersByCoachId(@PathVariable Long coachId) {
        List<User> users = userCoachService.selectUsersByCoachId(coachId);
        return ResponseEntity.ok(users);
    }

    /**
     * 检查用户和教练是否已经关联
     * @param userId 用户ID
     * @param coachId 教练ID
     * @return 是否关联
     */
    @GetMapping("/check")
    public ResponseEntity<Boolean> checkRelation(@RequestParam Long userId, @RequestParam Long coachId) {
        boolean isRelated = userCoachService.checkUserCoachRelation(userId, coachId);
        return ResponseEntity.ok(isRelated);
    }

    /**
     * 用户选择教练
     * @param userId 用户ID
     * @param coachId 教练ID
     * @return 结果
     */
    @PostMapping
    public ResponseEntity<Integer> selectCoach(@RequestParam Long userId, @RequestParam Long coachId) {
        int result = userCoachService.selectCoach(userId, coachId);
        return ResponseEntity.ok(result);
    }

    /**
     * 用户取消选择教练
     * @param userId 用户ID
     * @param coachId 教练ID
     * @return 结果
     */
    @DeleteMapping
    public ResponseEntity<Integer> unselectCoach(@RequestParam Long userId, @RequestParam Long coachId) {
        int result = userCoachService.unselectCoach(userId, coachId);
        return ResponseEntity.ok(result);
    }
}