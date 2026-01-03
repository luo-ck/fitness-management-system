package com.fitness.system.entity;

import lombok.Data;
import java.io.Serializable;

@Data
public class TrainingPlanExercise implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Long planId;
    private Long exerciseId;
    private Integer sets;
    private Integer reps;
    private Integer duration;
    private Integer orderIndex;
    private Long videoId; // 添加视频ID字段
}