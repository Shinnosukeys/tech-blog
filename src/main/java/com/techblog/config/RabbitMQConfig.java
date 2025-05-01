package com.techblog.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue voucherOrderQueue() {
        return new Queue("voucher_order_queue");
    }
}
