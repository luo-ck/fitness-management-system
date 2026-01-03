package com.fitness.system.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class Coach implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long coachId;
    private String username;
    private String name;
    private String specialty;
    private String contact;
    private String intro;
    private String passwordHash;
    private Boolean isVerified;
    private Boolean isDeleted;
    private Date createdAt;
}