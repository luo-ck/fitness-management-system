package com.fitness.system.mapper;

import com.fitness.system.entity.UserCoach;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户-教练关联Mapper接口
 */
@Mapper
public interface UserCoachMapper {
    /**
     * 根据用户ID查询关联的教练
     * @param userId 用户ID
     * @return 教练列表
     */
    List<UserCoach> selectCoachesByUserId(@Param("userId") Long userId);

    /**
     * 根据教练ID查询关联的用户
     * @param coachId 教练ID
     * @return 用户列表
     */
    List<UserCoach> selectUsersByCoachId(@Param("coachId") Long coachId);

    /**
     * 检查用户和教练是否已经关联
     * @param userId 用户ID
     * @param coachId 教练ID
     * @return 关联信息
     */
    UserCoach checkUserCoachRelation(@Param("userId") Long userId, @Param("coachId") Long coachId);

    /**
     * 新增用户-教练关联
     * @param userCoach 关联信息
     * @return 结果
     */
    int insertUserCoach(UserCoach userCoach);

    /**
     * 删除用户-教练关联
     * @param userId 用户ID
     * @param coachId 教练ID
     * @return 结果
     */
    int deleteUserCoach(@Param("userId") Long userId, @Param("coachId") Long coachId);
}
