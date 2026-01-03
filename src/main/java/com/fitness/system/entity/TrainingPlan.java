package com.fitness.system.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class TrainingPlan implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long planId;
    private Long userId;
    private Long coachId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date planDate;
    
    // 运动项数量
    private Integer exerciseCount;
    
    // 关联的运动项
    private List<TrainingPlanExercise> exercises;
}