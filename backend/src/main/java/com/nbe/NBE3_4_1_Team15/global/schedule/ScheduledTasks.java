package com.nbe.NBE3_4_1_Team15.global.schedule;

import com.nbe.NBE3_4_1_Team15.domain.order.repository.OrderRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class ScheduledTasks {
    private OrderRepository orderRepository;

    @Scheduled(cron = "0 0 14 * * ?") // 매일 정오에 실행
    public void executeAt2pm() {
        orderRepository.scheduledTask();
    }

}
