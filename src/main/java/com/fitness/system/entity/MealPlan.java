package com.fitness.system.entity;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class MealPlan implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long mealId;
    private Long userId;
    private Long coachId;
    private Date planDate;
    private String breakfast;
    private String lunch;
    private String dinner;
    private Integer totalCalories;
}