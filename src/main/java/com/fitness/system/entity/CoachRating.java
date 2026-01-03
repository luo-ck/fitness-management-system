package com.fitness.system.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class CoachRating implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long ratingId;
    private Long userId;
    private String userName;
    private Long coachId;
    private Integer rating;
    private String comment;
    private Date createTime;
}