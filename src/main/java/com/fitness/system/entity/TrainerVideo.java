package com.fitness.system.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class TrainerVideo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long videoId;
    private Long coachId;
    private String title;
    private String description;
    private String videoUrl;
    private Date uploadDate;
}