package com.fitness.system.entity;

import lombok.Data;
import java.io.Serializable;

@Data
public class Exercise implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long exerciseId;
    private String name;
    private String description;
    private String muscleGroup;
}