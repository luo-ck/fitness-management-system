package com.fitness.system.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class CoachApplication implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Long userId;
    private String username;
    private String name;
    private String specialty;
    private String contact;
    private String intro;
    private String status; // 申请状态：pending（待审核）、approved（通过）、rejected（拒绝）
    private Date applyTime;
    private Date approveTime;
    private Long adminId;
    private String adminName;
    private String feedback;
}