package com.fitness.system.mapper;

import com.fitness.system.entity.CoachApplication;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CoachApplicationMapper {
    /**
     * 插入教练申请
     * @param application 申请信息
     * @return 结果
     */
    int insertApplication(CoachApplication application);

    /**
     * 根据用户ID查询申请
     * @param userId 用户ID
     * @return 申请信息
     */
    CoachApplication selectByUserId(Long userId);

    /**
     * 查询所有申请
     * @return 申请列表
     */
    List<CoachApplication> selectAllApplications();

    /**
     * 根据ID查询申请
     * @param id 申请ID
     * @return 申请信息
     */
    CoachApplication selectById(Long id);

    /**
     * 更新申请状态为通过
     * @param application 申请信息
     * @return 结果
     */
    int updateStatusToApproved(CoachApplication application);

    /**
     * 更新申请状态为拒绝
     * @param application 申请信息
     * @return 结果
     */
    int updateStatusToRejected(CoachApplication application);
}