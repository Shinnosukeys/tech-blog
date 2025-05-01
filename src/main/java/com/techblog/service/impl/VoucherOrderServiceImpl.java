package com.techblog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.techblog.dto.Result;
import com.techblog.entity.SeckillVoucher;
import com.techblog.entity.Voucher;
import com.techblog.entity.VoucherOrder;
import com.techblog.mapper.SeckillVoucherMapper;
import com.techblog.mapper.VoucherMapper;
import com.techblog.mapper.VoucherOrderMapper;
import com.techblog.service.ISeckillVoucherService;
import com.techblog.service.IVoucherOrderService;
import com.techblog.utils.idWorker.RedisIdWorker;
import com.techblog.utils.SimpleRedisLock;
import com.techblog.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.techblog.utils.constants.RedisConstants.ORDERED_USERS_KEY_PREFIX;
import static com.techblog.utils.constants.RedisConstants.SECKILL_STOCK_KEY;

@Slf4j
@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {
    @Resource
    private VoucherMapper voucherMapper;
    @Resource
    private SeckillVoucherMapper seckillVoucherMapper;
    @Resource
    private ISeckillVoucherService seckillVoucherService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private RedisIdWorker redisIdWorker;
    @Resource
    private RabbitTemplate rabbitTemplate;

    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;

    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill2.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);

    }

    private final Object lock = new Object();  // 专用锁对象

    @Override
    public Result seckillVoucher(Long voucherId) {
        return null;
    }

    @Override
    @Transactional
    public Result seckillVoucherWithSynchronized(Long voucherId) {
        Voucher voucher = voucherMapper.selectById(voucherId);
        if (voucher == null) {
            return Result.fail("优惠券信息不存在");
        }
        // 优惠券使用时间信息
        SeckillVoucher seckillVoucher = seckillVoucherMapper.selectById(voucherId);
        LocalDateTime beginTime = seckillVoucher.getBeginTime();
        LocalDateTime endTime = seckillVoucher.getEndTime();

        if (beginTime.isAfter(LocalDateTime.now())) {
            return Result.fail("秒杀活动尚未开始！");
        }
        if (endTime.isBefore(LocalDateTime.now())) {
            return Result.fail("秒杀活动已结束！");
        }
        synchronized (lock) {
            // 每次都从数据库获取最新库存（而非依赖实例变量）
            SeckillVoucher latestSeckillVoucher = seckillVoucherMapper.selectById(voucherId);
            Integer stock = latestSeckillVoucher.getStock();

            if (stock <= 0) {
                return Result.fail("优惠券秒杀完毕库存不足！！！");
            }
            stock = stock - 1;
            seckillVoucher.setStock(stock);
            seckillVoucherMapper.updateById(seckillVoucher);

            VoucherOrder voucherOrder = new VoucherOrder();
            voucherOrder.setId(redisIdWorker.nextId("order"));
            voucherOrder.setUserId(UserHolder.getUser().getId());
            voucherOrder.setVoucherId(voucherId);
            voucherOrder.setUpdateTime(LocalDateTime.now());
            save(voucherOrder);
            return Result.ok("抢购成功");
        }
    }

    @Override
    public Result seckillVoucherWithStockMysql(Long voucherId) {
        return null;
    }

    @Override
    public Result seckillVoucherByUser(Long voucherId) {
        return null;
    }



/*  1.通过 voucherMapper 根据传入的 voucherId 查询普通优惠券信息
    2.根据 voucherId 查询秒杀优惠券信息,获取该优惠券秒杀活动的开始时间和结束时间
    3.从查询到的秒杀优惠券信息中提取库存数量,并检查库存是否充足
    4.使用 redissonClient 获取以用户 ID 为标识的分布式锁,从redis层简单检查用户是否重复下单（唯一的redis操作！！！！！）
    5.根据当前用户 ID 和传入的优惠券 ID 查询数据库中的订单，校验该用户针对此优惠券的订单数量
    6.使用乐观锁的方式尝试更新数据库中的库存，将库存数量减 1，更新条件为库存大于 0。（数据库层的乐观锁）
    7.若库存更新成功，创建 VoucherOrder 对象，将用户 ID、优惠券 ID 和当前时间（作为更新时间）设置到对象中。
*/

    @Override
    public Result seckillVoucherWithRedis(Long voucherId) {
        Voucher voucher = voucherMapper.selectById(voucherId);
        if (voucher == null) {
            return Result.fail("优惠券信息不存在");
        }
        // 优惠券使用时间信息
        SeckillVoucher seckillVoucher = seckillVoucherMapper.selectById(voucherId);
        LocalDateTime beginTime = seckillVoucher.getBeginTime();
        LocalDateTime endTime = seckillVoucher.getEndTime();

        if (beginTime.isAfter(LocalDateTime.now())) {
            return Result.fail("秒杀活动尚未开始！");
        }
        if (endTime.isBefore(LocalDateTime.now())) {
            return Result.fail("秒杀活动已结束！");
        }
        // 优惠券剩余库存数量
        Integer stock = seckillVoucher.getStock();
        if (stock <= 0) {
            return Result.fail("优惠券秒杀完毕库存不足！！！");
        }
        Long userId = UserHolder.getUser().getId();
        // 获取redis锁对象
        SimpleRedisLock redisLock = new SimpleRedisLock("order:" + userId, stringRedisTemplate);
        boolean tryLock = redisLock.tryLock(100);
        // 如果未获取到锁，直接返回用户不可重复下单
        if (!tryLock) {
            return Result.fail("用户不可重复下单购买，一人只能买一次");
        }
        // 获取代理对象
        // 会调用原始对象的方法，使得 AOP 的切面逻辑（例如事务管理）无法生效
        IVoucherOrderService iVoucherOrderService = (IVoucherOrderService) AopContext.currentProxy();
        try {
            return iVoucherOrderService.createVoucherOrder(voucherId);
        } finally {
            redisLock.unlock();
        }
    }

    @Override
    public Result seckillVoucherWithRedisson(Long voucherId) {
        Voucher voucher = voucherMapper.selectById(voucherId);
        if (voucher == null) {
            return Result.fail("优惠券信息不存在");
        }
        // 优惠券使用时间信息
        SeckillVoucher seckillVoucher = seckillVoucherMapper.selectById(voucherId);
        LocalDateTime beginTime = seckillVoucher.getBeginTime();
        LocalDateTime endTime = seckillVoucher.getEndTime();

        if (beginTime.isAfter(LocalDateTime.now())) {
            return Result.fail("秒杀活动尚未开始！");
        }
        if (endTime.isBefore(LocalDateTime.now())) {
            return Result.fail("秒杀活动已结束！");
        }
        // 优惠券剩余库存数量
        Integer stock = seckillVoucher.getStock();
        if (stock <= 0) {
            return Result.fail("优惠券秒杀完毕库存不足！！！");
        }
        Long userId = UserHolder.getUser().getId();
        // 获取redis锁对象
        //SimpleRedisLock lock = new SimpleRedisLock("order:" + userId, stringRedisTemplate);
        RLock lock = redissonClient.getLock("lock:order:" + userId);
        boolean tryLock = lock.tryLock();
        // 如果未获取到锁，直接返回用户不可重复下单
        if (!tryLock) {
            return Result.fail("用户不可重复下单购买，一人只能买一次");
        }
        // 获取代理对象
        IVoucherOrderService iVoucherOrderService = (IVoucherOrderService) AopContext.currentProxy();
        try {
            return iVoucherOrderService.createVoucherOrder(voucherId);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Result seckillVoucherOptimization(Long voucherId) {
        Long userId = UserHolder.getUser().getId(); // 获取用户 ID
        long orderId = redisIdWorker.nextId("order");
        String stockKey = SECKILL_STOCK_KEY + voucherId;
        String orderedUsersKey = "order:user:" + voucherId; // 用户已下单的 SET 键

        // 1. 原子性检查库存并扣减（解决超卖问题）
        Long newStock = stringRedisTemplate.opsForValue().decrement(stockKey);
        if (newStock < 0) {
            // 库存不足，回滚扣减
            stringRedisTemplate.opsForValue().increment(stockKey);
            return Result.fail("优惠券秒杀完毕，库存不足！");
        }

        // 2. 原子性检查用户是否已下单（解决重复下单问题）
        Long isAdded = stringRedisTemplate.opsForSet().add(orderedUsersKey, userId.toString());
        if (isAdded == 0) {
            // 回滚库存（因为用户已下单，但库存可能已被扣减）
            stringRedisTemplate.opsForValue().increment(stockKey);
            return Result.fail("该用户已抢购过该优惠券");
        }

        VoucherOrder voucherOrder = new VoucherOrder();
        voucherOrder.setId(orderId);
        voucherOrder.setUserId(userId);
        voucherOrder.setVoucherId(voucherId);

        // 将上面的订单通过rabbit发送给rabbitmq消息队列
        rabbitTemplate.convertAndSend("voucher_order_queue", voucherOrder);
        return Result.ok();
    }


    @Override
    public Result seckillVoucherByLua(Long voucherId) {
        long orderId = redisIdWorker.nextId("order");
        Long userId = UserHolder.getUser().getId();
        Long seckillFlag = stringRedisTemplate.execute(SECKILL_SCRIPT,
                Collections.emptyList(),
                voucherId.toString(), userId.toString(), String.valueOf(orderId)
        );
        if (seckillFlag != 0) {
            return Result.fail(seckillFlag == 1 ? "库存不足" : "同一用户不可重复抢购");
        }
        VoucherOrder voucherOrder = new VoucherOrder();
        voucherOrder.setId(orderId);
        voucherOrder.setUserId(userId);
        voucherOrder.setVoucherId(voucherId);
        voucherOrderBlockingQueue.add(voucherOrder);
        return Result.ok(orderId);
    }


    // 一人一单
    @Transactional
    @Override
    public Result createVoucherOrder(Long voucherId) {
        Long userId = UserHolder.getUser().getId();
        Integer count = query().eq("user_id", userId).eq("voucher_id", voucherId).count();
        if (count > 0) {
            return Result.fail("该用户已抢购过该优惠券");
        }
        // 更新时判断是否库存是否大于0 乐观锁
        boolean success = seckillVoucherService.update().setSql(" stock = stock - 1").gt("stock", 0)
                .eq("voucher_id", voucherId).update();
        if (!success) {
            return Result.fail("优惠券秒杀完毕库存不足！！！");
        }

        VoucherOrder voucherOrder = new VoucherOrder();
        voucherOrder.setId(redisIdWorker.nextId("order"));
        voucherOrder.setUserId(userId);
        voucherOrder.setVoucherId(voucherId);
        voucherOrder.setUpdateTime(LocalDateTime.now());
        save(voucherOrder);
        return Result.ok("抢购成功");
    }

    // 用来异步调用的创建订单的函数
    @RabbitListener(queues = "voucher_order_queue")
    private void handleVoucherOrder(VoucherOrder voucherOrder) {
        Long userId = voucherOrder.getUserId();
        Long voucherId = voucherOrder.getVoucherId();
        // 创建锁对象
        RLock redisLock = redissonClient.getLock("lock:order:" + userId);
        // 尝试获取锁
        boolean isLock = redisLock.tryLock();
        // 判断
        if (!isLock) {
            // 获取锁失败，直接返回失败或者重试
            log.error("不允许重复下单！");
            return;
        }

        try {
            // 5.1.查询订单
            int count = query().eq("user_id", userId).eq("voucher_id", voucherId).count();
            // 5.2.判断是否存在
            if (count > 0) {
                // 用户已经购买过了
                log.error("不允许重复下单！");
                return;
            }

            // 6.扣减库存
            boolean success = seckillVoucherService.update()
                    .setSql("stock = stock - 1") // set stock = stock - 1
                    .eq("voucher_id", voucherId).gt("stock", 0) // where id = ? and stock > 0
                    .update();
            if (!success) {
                // 扣减失败
                log.error("库存不足！");
                return;
            }

            // 7.创建订单
            save(voucherOrder);
        } finally {
            // 释放锁
            redisLock.unlock();
        }
    }








    // -----------------------------------------阻塞队列--------------------------------------
    /**
     * 阻塞队列消费订单数据
     */
    private static BlockingQueue<VoucherOrder> voucherOrderBlockingQueue = new ArrayBlockingQueue<>(1000 * 24);
    private static final ExecutorService SECKILL_ORDER_EXECUTOR = Executors.newSingleThreadExecutor();

    @PostConstruct
    private void init() {
        SECKILL_ORDER_EXECUTOR.submit(new VoucherOrderHandler());
//        SECKILL_ORDER_EXECUTOR.submit(new VoucherOrderHandler());
    }

    private class VoucherOrderHandler implements Runnable {
        @Override
        public void run() {}


//        @Override
//        public void run() {
//            while (true) {
//                try {
//                    // 1.获取消息队列中的订单信息 XREADGROUP GROUP g1 c1 COUNT 1 BLOCK 2000 STREAMS s1 >
//                    List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
//                            Consumer.from("g1", "c1"),
//                            StreamReadOptions.empty().count(1).block(Duration.ofSeconds(2)),
//                            StreamOffset.create("stream.orders", ReadOffset.lastConsumed())
//                    );
//                    // 2.判断订单信息是否为空
//                    if (list == null || list.isEmpty()) {
//                        // 如果为null，说明没有消息，继续下一次循环
//                        continue;
//                    }
//                    // 解析数据
//                    MapRecord<String, Object, Object> record = list.get(0);
//                    Map<Object, Object> value = record.getValue();
//                    VoucherOrder voucherOrder = BeanUtil.fillBeanWithMap(value, new VoucherOrder(), true);
//                    // 3.创建订单
//                    createVoucherOrder(voucherOrder);
//                    // 4.确认消息 XACK
//                    stringRedisTemplate.opsForStream().acknowledge("s1", "g1", record.getId());
//                } catch (Exception e) {
//                    log.error("处理订单异常", e);
//                    handlePendingList();
//                }
//            }
//        }

//        private void handlePendingList() {
//            while (true) {
//                try {
//                    // 1.获取pending-list中的订单信息 XREADGROUP GROUP g1 c1 COUNT 1 BLOCK 2000 STREAMS s1 0
//                    List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
//                            Consumer.from("g1", "c1"),
//                            StreamReadOptions.empty().count(1),
//                            StreamOffset.create("stream.orders", ReadOffset.from("0"))
//                    );
//                    // 2.判断订单信息是否为空
//                    if (list == null || list.isEmpty()) {
//                        // 如果为null，说明没有异常消息，结束循环
//                        break;
//                    }
//                    // 解析数据
//                    MapRecord<String, Object, Object> record = list.get(0);
//                    Map<Object, Object> value = record.getValue();
//                    VoucherOrder voucherOrder = BeanUtil.fillBeanWithMap(value, new VoucherOrder(), true);
//                    // 3.创建订单
//                    handleVoucherOrder(voucherOrder);
//                    // 4.确认消息 XACK
//                    stringRedisTemplate.opsForStream().acknowledge("s1", "g1", record.getId());
//                } catch (Exception e) {
//                    log.error("处理订单异常", e);
//                }
//            }
//        }
//
//        // 使用redis中stream拿去消息消费
//        @Override
//        public void run() {
//            String queueName = "stream.orders";
//            while (true) {
//
//                try {
//                    List<MapRecord<String, Object, Object>> mapRecordList = stringRedisTemplate.opsForStream().read(
//                            Consumer.from("g1", "c1"),// g1组 c1消费者
//                            StreamReadOptions.empty().count(1).block(Duration.ofSeconds(2)),
//                            StreamOffset.create(queueName, ReadOffset.lastConsumed())
//                    );
//                    // 如果为null，说明没有消息，继续下一次循环
//                    if (CollectionUtil.isEmpty(mapRecordList)) {
//                        continue;
//                    }
//                    // 解析数据
//                    MapRecord<String, Object, Object> record = mapRecordList.get(0);
//                    Map<Object, Object> value = record.getValue();
//                    VoucherOrder voucherOrder = BeanUtil.fillBeanWithMap(value, new VoucherOrder(), true);
//
//                    // 开始消费
//                    handleVoucherOrder(voucherOrder);
//                    // 确认ACK
//                    stringRedisTemplate.opsForStream().acknowledge(queueName, "g1", record.getId());
//                } catch (Exception e) {
//                    log.error("处理订单异常", e);
//                    handlePendingList();
//                }
//            }
//
//        }

        // 通过JVM内存中的BlockingQueue来拿消息消费
        /*@Override
        public void run() {
            while (true){
                try {
                    VoucherOrder voucherOrder = voucherOrderBlockingQueue.take();
                    createVoucherOrder(voucherOrder);
                } catch (Exception e) {
                    log.error("订单创建失败");
                    throw new RuntimeException(e);
                }
            }
        }*/
    }

    /*private BlockingQueue<VoucherOrder> orderTasks = new ArrayBlockingQueue<>(1024 * 1024);
    private class VoucherOrderHandler implements Runnable{

        @Override
        public void run() {
            while (true){
                try {
                    // 1.获取队列中的订单信息
                    VoucherOrder voucherOrder = orderTasks.take();
                    // 2.创建订单
                    createVoucherOrder(voucherOrder);
                } catch (Exception e) {
                    log.error("处理订单异常", e);
                }
            }
        }
    }*/
}
