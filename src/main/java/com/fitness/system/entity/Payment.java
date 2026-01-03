package com.fitness.system.entity;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Payment implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long paymentId;
    private Long userId;
    private Long coachId;
    private BigDecimal amount;
    private BigDecimal platformFee;
    private Date paymentDate;
    private String status;
}