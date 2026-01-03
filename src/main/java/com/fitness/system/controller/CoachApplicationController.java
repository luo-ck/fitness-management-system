package com.fitness.system.controller;

import com.fitness.system.entity.CoachApplication;
import com.fitness.system.service.ICoachApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教练申请Controller
 */
@RestController
@RequestMapping("/coach-applications")
public class CoachApplicationController {

    @Autowired
    private ICoachApplicationService coachApplicationService;

    /**
     * 提交教练申请
     * @param application 申请信息
     * @return 结果
     */
    @PostMapping
    public ResponseEntity<Boolean> submitApplication(CoachApplication application) {
        boolean result = coachApplicationService.submitApplication(application);
        if (result) {
            return ResponseEntity.status(HttpStatus.CREATED).body(true);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    /**
     * 根据用户ID查询申请
     * @param userId 用户ID
     * @return 申请信息
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<CoachApplication> getApplicationByUserId(@PathVariable Long userId) {
        CoachApplication application = coachApplicationService.getApplicationByUserId(userId);
        if (application == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(application);
    }

    /**
     * 查询所有申请
     * @return 申请列表
     */
    @GetMapping
    public ResponseEntity<List<CoachApplication>> getAllApplications() {
        List<CoachApplication> applications = coachApplicationService.getAllApplications();
        return ResponseEntity.ok(applications);
    }

    /**
     * 根据ID查询申请详情
     * @param id 申请ID
     * @return 申请信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<CoachApplication> getApplicationById(@PathVariable Long id) {
        CoachApplication application = coachApplicationService.getApplicationById(id);
        if (application == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(application);
    }

    /**
     * 审批通过申请
     * @param id 申请ID
     * @param application 审批信息
     * @return 结果
     */
    @PutMapping("/approve/{id}")
    public ResponseEntity<Boolean> approveApplication(@PathVariable Long id, @RequestBody CoachApplication application) {
        application.setId(id);
        boolean result = coachApplicationService.approveApplication(application);
        return ResponseEntity.ok(result);
    }

    /**
     * 审批拒绝申请
     * @param id 申请ID
     * @param application 审批信息
     * @return 结果
     */
    @PutMapping("/reject/{id}")
    public ResponseEntity<Boolean> rejectApplication(@PathVariable Long id, @RequestBody CoachApplication application) {
        application.setId(id);
        boolean result = coachApplicationService.rejectApplication(application);
        return ResponseEntity.ok(result);
    }
}