package com.fitness.system.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class Conversation implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long otherId;
    private String otherName;
    private String otherType; // 对方类型：user/coach
    private String lastMessage;
    private Date lastMessageTime;
    private Integer unreadCount;
}