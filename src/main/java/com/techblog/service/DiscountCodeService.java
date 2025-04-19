package com.techblog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.techblog.dto.Result;
import com.techblog.entity.DiscountCode;

public interface DiscountCodeService extends IService<DiscountCode> {
    Result getById(Integer id);

    Result getByCode(String code);

    Result listAll();

    Result create(DiscountCode discountCode);

    Result update(DiscountCode discountCode);

    Result deleteById(Integer id);

    Result updateStatus(Integer id, Integer status);

    Result claimDiscountCode(Integer userId, Integer discountCodeId);

    Result useDiscountCode(Integer userId, Integer discountCodeId);

    Result hasClaimedDiscountCode(Integer userId, Integer discountCodeId);
}