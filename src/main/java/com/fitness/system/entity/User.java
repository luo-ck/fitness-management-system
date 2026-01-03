package com.fitness.system.entity;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long userId;
    private String username;
    private Integer age;
    private String gender;
    private BigDecimal height;
    private BigDecimal weight;
    private String contact;
    private String goal;
    private String passwordHash;
    private String avatar;
    private Boolean isDeleted;
    private Date createdAt;
}