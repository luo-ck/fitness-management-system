package com.fitness.system.mapper;

import com.fitness.system.entity.TrainerVideo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 教练教学视频Mapper接口
 */
@Mapper
public interface TrainerVideoMapper {
    /**
     * 根据视频ID查询教练教学视频
     * @param videoId 视频ID
     * @return 教练教学视频信息
     */
    TrainerVideo selectTrainerVideoById(@Param("videoId") Long videoId);

    /**
     * 根据教练ID查询教练教学视频列表
     * @param coachId 教练ID
     * @return 教练教学视频列表
     */
    List<TrainerVideo> selectTrainerVideosByCoachId(@Param("coachId") Long coachId);

    /**
     * 新增教练教学视频
     * @param trainerVideo 教练教学视频信息
     * @return 结果
     */
    int insertTrainerVideo(TrainerVideo trainerVideo);

    /**
     * 修改教练教学视频
     * @param trainerVideo 教练教学视频信息
     * @return 结果
     */
    int updateTrainerVideo(TrainerVideo trainerVideo);

    /**
     * 根据视频ID删除教练教学视频
     * @param videoId 视频ID
     * @return 结果
     */
    int deleteTrainerVideoById(@Param("videoId") Long videoId);
}
