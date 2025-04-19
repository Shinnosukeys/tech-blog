package com.techblog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.techblog.dto.Result;
import com.techblog.entity.DiscountCode;
import com.techblog.entity.DiscountCodeOrder;
import com.techblog.entity.article.Comment;
import com.techblog.mapper.CommentMapper;
import com.techblog.mapper.DiscountCodeMapper;
import com.techblog.mapper.DiscountCodeOrderMapper;
import com.techblog.service.DiscountCodeService;
import com.techblog.utils.RedisUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DiscountCodeServiceImpl extends ServiceImpl<DiscountCodeMapper, DiscountCode>  implements DiscountCodeService {

    @Resource
    private DiscountCodeMapper discountCodeMapper;

    @Resource
    private DiscountCodeOrderMapper discountCodeOrderMapper;

    @Resource
    private RedisUtils redisUtils;

    private static final String DISCOUNT_CODE_LOCK_KEY = "discount_code_lock:";

    @Override
    public Result getById(Integer id) {
        DiscountCode discountCode = discountCodeMapper.getById(id);
        return discountCode != null ? Result.ok(discountCode) : Result.fail("優惠碼不存在");
    }

    @Override
    public Result getByCode(String code) {
        DiscountCode discountCode = discountCodeMapper.getByCode(code);
        return discountCode != null ? Result.ok(discountCode) : Result.fail("優惠碼不存在");
    }

    @Override
    public Result listAll() {
        List<DiscountCode> list = discountCodeMapper.listAll();
        return Result.ok(list, list.size());
    }

    @Override
    public Result create(DiscountCode discountCode) {
        return discountCodeMapper.insert(discountCode) > 0 ?
                Result.ok(discountCode) : Result.fail("創建優惠碼失敗");
    }

    @Override
    public Result update(DiscountCode discountCode) {
        return discountCodeMapper.update(discountCode) > 0 ?
                Result.ok() : Result.fail("更新優惠碼失敗");
    }

    @Override
    public Result deleteById(Integer id) {
        return discountCodeMapper.deleteById(id) > 0 ?
                Result.ok() : Result.fail("刪除優惠碼失敗");
    }

    @Override
    public Result updateStatus(Integer id, Integer status) {
        return discountCodeMapper.updateStatus(id, status) > 0 ?
                Result.ok() : Result.fail("更新優惠碼狀態失敗");
    }

    @Override
    @Transactional
    public Result claimDiscountCode(Integer userId, Integer discountCodeId) {
        // 檢查是否已領取過
        if (discountCodeOrderMapper.countByUserIdAndDiscountCodeId(userId, discountCodeId) > 0) {
            return Result.fail("您已經領取過此優惠碼");
        }

        // 使用Redis分布式鎖
        String lockKey = DISCOUNT_CODE_LOCK_KEY + discountCodeId;
        if (redisUtils.set(lockKey, "1", 10)) { // 10秒鎖
            try {
                // 檢查優惠碼狀態
                DiscountCode discountCode = discountCodeMapper.getById(discountCodeId);
                if (discountCode == null || discountCode.getStatus() != 1) {
                    return Result.fail("優惠碼不可用");
                }

                // 創建訂單
                DiscountCodeOrder order = new DiscountCodeOrder();
                order.setUserId(userId);
                order.setDiscountCodeId(discountCodeId);
                order.setStatus(1); // 已領取

                if (discountCodeOrderMapper.insert(order) > 0) {
                    // 更新優惠碼狀態
                    discountCode.setStatus(2); // 已領取
                    if (discountCodeMapper.update(discountCode) > 0) {
                        return Result.ok();
                    }
                }
                return Result.fail("領取優惠碼失敗");
            } finally {
                redisUtils.delete(lockKey);
            }
        }
        return Result.fail("系統繁忙，請稍後再試");
    }

    @Override
    @Transactional
    public Result useDiscountCode(Integer userId, Integer discountCodeId) {
        // 檢查是否已領取
        if (discountCodeOrderMapper.countByUserIdAndDiscountCodeId(userId, discountCodeId) == 0) {
            return Result.fail("您還未領取此優惠碼");
        }

        // 獲取訂單
        List<DiscountCodeOrder> orders = discountCodeOrderMapper.getByDiscountCodeId(discountCodeId);
        for (DiscountCodeOrder order : orders) {
            if (order.getUserId().equals(userId) && order.getStatus() == 1) {
                order.setStatus(2); // 已使用
                order.setUseTime(LocalDateTime.now());
                return discountCodeOrderMapper.update(order) > 0 ?
                        Result.ok() : Result.fail("使用優惠碼失敗");
            }
        }
        return Result.fail("優惠碼狀態異常");
    }

    @Override
    public Result hasClaimedDiscountCode(Integer userId, Integer discountCodeId) {
        int count = discountCodeOrderMapper.countByUserIdAndDiscountCodeId(userId, discountCodeId);
        return Result.ok(count > 0);
    }
}