package com.nbe.NBE3_4_1_Team15.domain.order.service;

import com.nbe.NBE3_4_1_Team15.domain.order.dto.OrderDto;
import com.nbe.NBE3_4_1_Team15.domain.order.entity.Order;
import com.nbe.NBE3_4_1_Team15.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    // 모든 주문 내역 조회
    public List<OrderDto> findAll() {
        return orderRepository.findAll().stream()
                .map(OrderDto::of) // 엔티티를 DTO로 변환
                .collect(Collectors.toList());
    }

    // 특정 주문 조회
    public OrderDto findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));
        return OrderDto.of(order); // 엔티티를 DTO로 변환
    }

    // 특정 회원의 주문 내역 조회
    public List<OrderDto> findByConsumerId(Long memberId) {
        return orderRepository.findByConsumerId(memberId).stream()
                .map(OrderDto::of) // 엔티티를 DTO로 변환
                .collect(Collectors.toList());
    }

    // 주문 삭제
    public void delete(Long id) {
        orderRepository.findById(id).ifPresent(orderRepository::delete);
    }

    public Long findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("값 없음"));
    }
}
