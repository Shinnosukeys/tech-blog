package com.techblog.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DiscountCodeOrder {
    private Integer id;
    private Integer userId;
    private Integer discountCodeId;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime useTime;
    private LocalDateTime updateTime;

    // 關聯的優惠碼信息
    private DiscountCode discountCode;
}