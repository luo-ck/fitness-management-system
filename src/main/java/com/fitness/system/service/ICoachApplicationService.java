package com.fitness.system.service;

import com.fitness.system.entity.CoachApplication;
import java.util.List;

public interface ICoachApplicationService {
    /**
     * 提交教练申请
     * @param application 申请信息
     * @return 结果
     */
    boolean submitApplication(CoachApplication application);
    
    /**
     * 根据用户ID查询申请
     * @param userId 用户ID
     * @return 申请信息
     */
    CoachApplication getApplicationByUserId(Long userId);
    
    /**
     * 查询所有申请
     * @return 申请列表
     */
    List<CoachApplication> getAllApplications();
    
    /**
     * 根据ID查询申请详情
     * @param id 申请ID
     * @return 申请信息
     */
    CoachApplication getApplicationById(Long id);
    
    /**
     * 审批通过申请
     * @param application 申请信息
     * @return 结果
     */
    boolean approveApplication(CoachApplication application);
    
    /**
     * 审批拒绝申请
     * @param application 申请信息
     * @return 结果
     */
    boolean rejectApplication(CoachApplication application);
}