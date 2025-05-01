package com.techblog.utils.idWorker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class SnowflakeIdWorker {
    // 基准时间戳（2025-04-01 00:00:00 GMT+8）
    private static final long EPOCH = 1743508800000L;

    // 各部分的位数（采用经典分配方案）
    private static final long WORKER_ID_BITS = 5L;    // 机器ID占5位
    private static final long DATA_CENTER_ID_BITS = 5L; // 数据中心占5位
    private static final long SEQUENCE_BITS = 12L;     // 序列号占12位

    // 最大值计算（位运算优化）
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private static final long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS);

    // 位移计算（基于网页3的位移公式）
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATA_CENTER_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;

    // 序列号掩码（防止溢出）
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    // 运行参数（通过配置注入）
    private final long workerId;
    private final long dataCenterId;

    // 原子操作保证线程安全（参考网页6）
    private final AtomicLong sequence = new AtomicLong(0);
    private volatile long lastTimestamp = -1L;

    /**
     * 构造器（参数通过配置文件注入）
     * @param workerId 机器ID (0-31)
     * @param dataCenterId 数据中心ID (0-31)
     */
    public SnowflakeIdWorker(
            @Value("${snowflake.worker-id:0}") long workerId,
            @Value("${snowflake.data-center-id:0}") long dataCenterId) {

        // 参数校验（遵循网页5的校验逻辑）
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(
                    String.format("Worker ID必须在0到%d之间", MAX_WORKER_ID));
        }
        if (dataCenterId > MAX_DATA_CENTER_ID || dataCenterId < 0) {
            throw new IllegalArgumentException(
                    String.format("数据中心ID必须在0到%d之间", MAX_DATA_CENTER_ID));
        }

        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    /**
     * 生成分布式ID（线程安全实现）
     */
    public synchronized long nextId() {
        long currentTimestamp = timeGen();

        // 时钟回拨检测（参考网页5的异常处理）
        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("时钟回拨拒绝生成ID。回拨时长：%dms",
                            lastTimestamp - currentTimestamp));
        }

        // 同一毫秒内处理（原子操作优化）
        if (currentTimestamp == lastTimestamp) {
            long sequenceVal = sequence.incrementAndGet() & SEQUENCE_MASK;
            if (sequenceVal == 0) {
                currentTimestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence.set(0);
        }

        lastTimestamp = currentTimestamp;

        // 组合各部分（基于网页1的位移方式）
        return ((currentTimestamp - EPOCH) << TIMESTAMP_SHIFT)
                | (dataCenterId << DATA_CENTER_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence.getAndIncrement();
    }

    // 获取下一毫秒（参考网页3的实现）
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    // 时间戳生成（隔离系统调用）
    protected long timeGen() {
        return System.currentTimeMillis();
    }
}
