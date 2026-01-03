package com.fitness.system.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class Feedback implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long feedbackId;
    private Long userId;
    private Long planId;
    private String feeling;
    private Integer rating;
    private String comments;
    private Date feedbackDate;
    // 训练计划日期，用于教练端查看反馈对应哪个计划
    private Date planDate;
}