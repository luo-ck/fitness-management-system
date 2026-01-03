package com.fitness.system.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class Admin implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long adminId;
    private String username;
    private String name;
    private String passwordHash;
    private Date createdAt;
}