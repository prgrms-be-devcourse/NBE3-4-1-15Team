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

    /**
     * 주문 ID로 단건 조회
     */
    public OrderDto findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));
        return OrderDto.of(order);  // OrderDto.of(...)에서 cart->cartProducts를 처리
    }

    /**
     * 특정 회원의 주문 목록
     */
    public List<OrderDto> findByConsumerId(Long memberId) {
        return orderRepository.findAllByConsumer_Id(memberId)
                .stream()
                .map(OrderDto::of) // 여기서 Order -> OrderDto 변환
                .collect(Collectors.toList());
    }

    /**
     * 특정 회원 + 주문 상태로 필터링 조회 (옵션)
     */
    public List<OrderDto> findOrdersByMemberIdAndType(Long memberId, OrderType orderType) {
        return orderRepository.findAllByConsumer_Id(memberId).stream()
                .filter(order -> orderType == null || order.getOrderType() == orderType)
                .map(OrderDto::of)
                .collect(Collectors.toList());
    }
}
