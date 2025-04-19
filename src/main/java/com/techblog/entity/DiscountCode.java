package com.techblog.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DiscountCode {
    private Integer id;
    private String code;
    private String title;
    private String subTitle;
    private String rules;
    private BigDecimal discountRate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 