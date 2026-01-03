package com.fitness.system.service.impl;

import com.fitness.system.entity.TrainerVideo;
import com.fitness.system.mapper.TrainerVideoMapper;
import com.fitness.system.service.ITrainerVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 教练教学视频Service实现类
 */
@Service
public class TrainerVideoServiceImpl implements ITrainerVideoService {

    @Autowired
    private TrainerVideoMapper trainerVideoMapper;

    @Override
    public TrainerVideo selectTrainerVideoById(Long videoId) {
        return trainerVideoMapper.selectTrainerVideoById(videoId);
    }

    @Override
    public List<TrainerVideo> selectTrainerVideosByCoachId(Long coachId) {
        return trainerVideoMapper.selectTrainerVideosByCoachId(coachId);
    }

    @Override
    public int insertTrainerVideo(TrainerVideo trainerVideo) {
        return trainerVideoMapper.insertTrainerVideo(trainerVideo);
    }

    @Override
    public int updateTrainerVideo(TrainerVideo trainerVideo) {
        return trainerVideoMapper.updateTrainerVideo(trainerVideo);
    }

    @Override
    public int deleteTrainerVideoById(Long videoId) {
        return trainerVideoMapper.deleteTrainerVideoById(videoId);
    }
}
