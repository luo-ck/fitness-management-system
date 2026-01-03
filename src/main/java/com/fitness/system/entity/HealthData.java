package com.fitness.system.entity;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class HealthData implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long dataId;
    private Long userId;
    private Date recordDate;
    private BigDecimal weight;
    private BigDecimal bodyFat;
    private BigDecimal muscleMass;
    private BigDecimal bmi;
    private BigDecimal waistCircumference;
    private String notes;
}