package com.nbe.NBE3_4_1_Team15.domain.order;

import com.nbe.NBE3_4_1_Team15.domain.order.entity.Order;
import com.nbe.NBE3_4_1_Team15.domain.order.repository.SpringOrderRepository;
import com.nbe.NBE3_4_1_Team15.domain.order.type.OrderType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderDeliveryScheduler {

    private final SpringOrderRepository orderRepository;

    /**
     * 테스트용: 10초마다 실행하도록 fixedDelay 속성을 사용
     * 실제 운영 시에는 cron 표현식을 사용하여 매일 정해진 시간(예: 14시)에 실행하면 됩니다.
     *
     * 여기서는 ORDERED 상태 주문에 대해 처리합니다.
     */
    @Scheduled(fixedDelay = 10000)  // 10초마다 실행
    @Transactional
    public void processDeliveryScheduled() {
        LocalDateTime todayCutoff = LocalDateTime.of(LocalDate.now(), LocalTime.of(14, 0));

        // 당일 14시 이전(자정 ~ 14시 미만)에 등록된 주문 중 ORDERED 상태 주문을 조회
        List<Order> ordersBeforeCutoff = orderRepository.findAllByOrderTypeAndOrderDateBetween(
                OrderType.ORDERED,
                LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT),
                todayCutoff
        );

        // 당일 14시 이전 주문은 14시에 배송 처리 (orderDate를 오늘 14시로 설정)
        ordersBeforeCutoff.forEach(order -> {
            order.setOrderType(OrderType.DELIVERY);
            order.setOrderDate(todayCutoff);
        });
        orderRepository.saveAll(ordersBeforeCutoff);

        // 오늘 14시 이후에 등록된 주문(즉, cutoff 이후) 중 ORDERED 상태 주문을 조회
        List<Order> ordersAfterCutoff = orderRepository.findAllByOrderTypeAndOrderDateAfter(
                OrderType.ORDERED,
                todayCutoff
        );

        // 오늘 14시 이후 주문은 다음날 14시로 배송 처리
        LocalDateTime tomorrowCutoff = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(14, 0));
        ordersAfterCutoff.forEach(order -> {
            order.setOrderType(OrderType.DELIVERY);
            order.setOrderDate(tomorrowCutoff);
        });
        orderRepository.saveAll(ordersAfterCutoff);
    }
}
