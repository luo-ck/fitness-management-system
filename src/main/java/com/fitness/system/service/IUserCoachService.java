package com.fitness.system.service;

import com.fitness.system.entity.Coach;
import com.fitness.system.entity.User;
import com.fitness.system.entity.UserCoach;

import java.util.List;

/**
 * 用户-教练关联Service接口
 */
public interface IUserCoachService {
    /**
     * 根据用户ID查询关联的教练
     * @param userId 用户ID
     * @return 教练列表
     */
    List<Coach> selectCoachesByUserId(Long userId);

    /**
     * 根据教练ID查询关联的学员
     * @param coachId 教练ID
     * @return 学员列表
     */
    List<User> selectUsersByCoachId(Long coachId);

    /**
     * 检查用户和教练是否已经关联
     * @param userId 用户ID
     * @param coachId 教练ID
     * @return 是否关联
     */
    boolean checkUserCoachRelation(Long userId, Long coachId);

    /**
     * 用户选择教练
     * @param userId 用户ID
     * @param coachId 教练ID
     * @return 结果
     */
    int selectCoach(Long userId, Long coachId);

    /**
     * 用户取消选择教练
     * @param userId 用户ID
     * @param coachId 教练ID
     * @return 结果
     */
    int unselectCoach(Long userId, Long coachId);
}
