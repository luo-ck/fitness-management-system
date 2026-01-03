package com.fitness.system.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Long senderId;
    private String senderName;
    private Long receiverId;
    private String receiverType; // 接收者类型：user/coach/admin
    private String messageType; // 消息类型：message/training_plan/feedback
    private Long relatedId; // 关联ID：训练计划ID或反馈ID
    private String title;
    private String content;
    private Boolean isRead;
    private Date sendTime;
    private Date readTime;
}