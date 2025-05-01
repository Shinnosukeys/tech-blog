package com.techblog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.techblog.dto.Result;
import com.techblog.entity.SeckillVoucher;
import com.techblog.entity.Voucher;
import com.techblog.mapper.VoucherMapper;
import com.techblog.service.ISeckillVoucherService;
import com.techblog.service.IVoucherService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import static com.techblog.utils.constants.RedisConstants.ORDERED_USERS_KEY_PREFIX;
import static com.techblog.utils.constants.RedisConstants.SECKILL_STOCK_KEY;


@Service
public class VoucherServiceImpl extends ServiceImpl<VoucherMapper, Voucher> implements IVoucherService {

    @Resource
    private ISeckillVoucherService seckillVoucherService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryVoucherOfShop(Long shopId) {
        // 查询优惠券信息
        List<Voucher> vouchers = getBaseMapper().queryVoucherOfShop(shopId);
        // 返回结果
        return Result.ok(vouchers);
    }

    @Override
    @Transactional
    public void addSeckillVoucher(Voucher voucher) {
        // 保存优惠券
        save(voucher);
        // 保存秒杀信息
        SeckillVoucher seckillVoucher = new SeckillVoucher();
        seckillVoucher.setVoucherId(voucher.getId());
        seckillVoucher.setStock(voucher.getStock());
        seckillVoucher.setBeginTime(voucher.getBeginTime());
        seckillVoucher.setEndTime(voucher.getEndTime());
        seckillVoucherService.save(seckillVoucher);
        // 保存秒杀库存到Redis中
        stringRedisTemplate.opsForValue().set(SECKILL_STOCK_KEY + voucher.getId(), voucher.getStock().toString());
        // 设置一个 set，key 是 order:user:+voucher.getId(), value 是 set 结构，将已经购买的用户 id 存放在 set 中
        String orderedUsersKey = ORDERED_USERS_KEY_PREFIX + voucher.getId();
    }
}
