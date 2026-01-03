package com.fitness.system.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class WorkoutLog implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long logId;
    private Long userId;
    private Date logDate;
    private String exerciseType;
    private Integer duration;
    private Integer caloriesBurned;
}