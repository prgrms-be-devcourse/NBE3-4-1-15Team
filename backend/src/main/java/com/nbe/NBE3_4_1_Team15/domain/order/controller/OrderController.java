package com.nbe.NBE3_4_1_Team15.domain.order.controller;

import com.nbe.NBE3_4_1_Team15.domain.order.dto.OrderDto;
import com.nbe.NBE3_4_1_Team15.domain.order.entity.Order;
import com.nbe.NBE3_4_1_Team15.domain.order.service.OrderService;
import com.nbe.NBE3_4_1_Team15.domain.order.type.OrderType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders") // 기본 경로
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    // 모든 주문 조회
    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        List<OrderDto> orders = orderService.findAll();
        return ResponseEntity.ok(orders);
    }

    // 특정 주문 조회
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable("id") Long id) {
        OrderDto order = orderService.findById(id);
        return ResponseEntity.ok(order);
    }

    // 특정 memberId를 가진 모든 Order 조회
    @GetMapping("/mem/{id}")
    public ResponseEntity<List<OrderDto>> getOrdersByMemberId(@PathVariable("id") Long id) {
        List<OrderDto> orderDtos = orderService.findByConsumerId(id);
        return ResponseEntity.ok(orderDtos);
    }

    //주문 생성
    @PostMapping("")
    public ResponseEntity<OrderDto> create(@RequestParam(name = "memberId") Long memberId,
                                           @RequestParam(name = "orderType") OrderType orderType,
                                           @RequestParam(name = "totalPrice") Integer totalPrice){
        OrderDto createOrder = orderService.create(memberId, orderType, totalPrice);
        return ResponseEntity.ok(createOrder);
    }

    // 주문 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable("id") Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
