package com.fitness.system.mapper;

import com.fitness.system.entity.Coach;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 教练Mapper接口
 */
@Mapper
public interface CoachMapper {

    /**
     * 根据教练ID查询教练
     * @param coachId 教练ID
     * @return 教练信息
     */
    Coach selectCoachByCoachId(@Param("coachId") Long coachId);

    /**
     * 根据用户名查询教练
     * @param username 用户名
     * @return 教练信息
     */
    Coach selectCoachByUsername(@Param("username") String username);

    /**
     * 查询所有教练
     * @return 教练列表
     */
    List<Coach> selectCoachList();

    /**
     * 查询已验证且未删除的教练
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
    int deleteCoachByCoachId(@Param("coachId") Long coachId);

}