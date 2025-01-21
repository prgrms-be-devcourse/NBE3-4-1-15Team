package com.nbe.NBE3_4_1_Team15.domain.order.repository;

import com.nbe.NBE3_4_1_Team15.domain.order.entity.Order;
import com.nbe.NBE3_4_1_Team15.domain.order.type.OrderType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SpringOrderRepository extends JpaRepository<Order, Long> {
    // 특정 memberId를 가진 Order 전부 조회
    List<Order> findAllByConsumer_Id(Long memberId);

    // 추가적으로 Scheduled 에서 사용할 주문 조회 메서드가 있다면 추가할 수 있습니다.
    List<Order> findAllByOrderTypeAndOrderDateBetween(OrderType orderType, LocalDateTime start, LocalDateTime end);
    List<Order> findAllByOrderTypeAndOrderDateAfter(OrderType orderType, LocalDateTime dateTime);
}
