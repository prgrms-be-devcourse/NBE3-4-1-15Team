package com.nbe.NBE3_4_1_Team15.domain.order.service;

import com.nbe.NBE3_4_1_Team15.domain.order.dto.OrderDto;
import com.nbe.NBE3_4_1_Team15.domain.order.entity.Order;
import com.nbe.NBE3_4_1_Team15.domain.order.repository.OrderRepository;
import com.nbe.NBE3_4_1_Team15.domain.order.type.OrderType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderAdminService {
    private final OrderRepository orderRepository;

    /**
     * 모든 주문 조회
     */
    public List<OrderDto> findAll() {
        return orderRepository.findAll().stream()
                .map(OrderDto::of)
                .collect(Collectors.toList());
    }

    /**
     * 주문 삭제
     */
    public void delete(Long id) {
        orderRepository.findById(id).ifPresent(orderRepository::delete);
    }

    /**
     * 주문 상태 변경 (관리자용)
     */
    public OrderDto orderTypeUpdate(Long orderId, OrderType orderType) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        order.setOrderType(orderType);
        Order saved = orderRepository.save(order);
        return OrderDto.of(saved);
    }
}
