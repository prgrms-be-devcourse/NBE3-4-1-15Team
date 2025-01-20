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
public class OrderCommonService {
    private final OrderRepository orderRepository;

    // 특정 주문 조회
    public OrderDto findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));
        return OrderDto.of(order);
    }

    // 특정 회원의 주문 내역 조회
    public List<OrderDto> findByConsumerId(Long memberId) {
        return orderRepository.findAllByConsumer_Id(memberId).stream()
                .map(OrderDto::of)
                .collect(Collectors.toList());
    }

    // 특정 회원의 배송 상태에 따른 주문 조회
    public List<OrderDto> findOrdersByMemberIdAndType(Long memberId, OrderType orderType) {
        return orderRepository.findAllByConsumer_Id(memberId).stream()
                .filter(order -> orderType == null || order.getOrderType() == orderType)
                .map(OrderDto::of)
                .collect(Collectors.toList());
    }
}
