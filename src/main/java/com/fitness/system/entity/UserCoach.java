package com.fitness.system.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class UserCoach implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Long userId;
    private Long coachId;
    private Date startDate;
    private Boolean isActive;
}