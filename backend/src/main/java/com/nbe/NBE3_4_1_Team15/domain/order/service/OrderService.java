package com.nbe.NBE3_4_1_Team15.domain.order.service;

import com.nbe.NBE3_4_1_Team15.domain.member.entity.MemRepository;
import com.nbe.NBE3_4_1_Team15.domain.member.entity.Member;
import com.nbe.NBE3_4_1_Team15.domain.order.dto.OrderDto;
import com.nbe.NBE3_4_1_Team15.domain.order.entity.Order;
import com.nbe.NBE3_4_1_Team15.domain.order.repository.OrderRepository;
import com.nbe.NBE3_4_1_Team15.domain.order.type.OrderType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final MemRepository memberRepository;
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
        return orderRepository.findAllByConsumer_Id(memberId).stream()
                .map(OrderDto::of)
                .collect(Collectors.toList());
    }

    // 특정 회원의 배송상태에 따른 주문 내역 조회
    public List<OrderDto> findOrdersByMemberIdAndType(Long memberId, OrderType orderType){
        return orderRepository.findAllByConsumer_Id(memberId).stream()
                .filter(order -> orderType == null || order.getOrderType() == orderType)
                .map(OrderDto::of)
                .collect(Collectors.toList());
    }

    //주문 생성
    public OrderDto create(Long memberId, OrderType orderType, Integer totalPrice){
        Member consumer = memberRepository.findById(memberId).orElseThrow(()-> new IllegalArgumentException("Member not found"));

        Order order = Order.builder()
                .consumer(consumer)
                .orderType(orderType)
                .totalPrice(totalPrice)
                .orderDate(LocalDateTime.now())
                .build();

        Order savedOrder = orderRepository.save(order);

        return OrderDto.of(savedOrder);
    }

    // 주문 삭제
    public void delete(Long id) {
        orderRepository.findById(id).ifPresent(orderRepository::delete);
    }

}
