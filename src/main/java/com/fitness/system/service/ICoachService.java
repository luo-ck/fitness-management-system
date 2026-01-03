package com.fitness.system.service;

import com.fitness.system.entity.Coach;
import java.util.List;

/**
 * 教练Service接口
 */
public interface ICoachService {
    /**
     * 根据教练ID查询教练
     * @param coachId 教练ID
     * @return 教练信息
     */
    Coach selectCoachByCoachId(Long coachId);

    /**
     * 根据用户名查询教练
     * @param username 用户名
     * @return 教练信息
     */
    Coach selectCoachByUsername(String username);

    /**
     * 查询所有教练
     * @return 教练列表
     */
    List<Coach> selectCoachList();

    /**
     * 查询已验证且未删除的教练（用户可见）
     * @return 教练列表
     */
    List<Coach> selectValidCoachList();

    /**
     * 新增教练
     * @param coach 教练信息
     * @return 结果
     */
    int insertCoach(Coach coach);

    /**
     * 修改教练
     * @param coach 教练信息
     * @return 结果
     */
    int updateCoach(Coach coach);

    /**
     * 删除教练
     * @param coachId 教练ID
     * @return 结果
     */
    int deleteCoachByCoachId(Long coachId);
}
