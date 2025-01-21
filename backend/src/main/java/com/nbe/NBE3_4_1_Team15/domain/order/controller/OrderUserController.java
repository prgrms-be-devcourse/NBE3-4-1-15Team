package com.nbe.NBE3_4_1_Team15.domain.order.controller;

import com.nbe.NBE3_4_1_Team15.domain.order.dto.OrderDto;
import com.nbe.NBE3_4_1_Team15.domain.order.service.OrderUserService;
import com.nbe.NBE3_4_1_Team15.domain.order.service.OrderCommonService;
import com.nbe.NBE3_4_1_Team15.global.rq.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/orders")
@RequiredArgsConstructor
public class OrderUserController {
    private final OrderUserService orderUserService;
    private final OrderCommonService orderCommonService;
    private final Rq rq;

    // 특정 주문 조회
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable("id") Long id) {
        OrderDto order = orderCommonService.findById(id);
        return ResponseEntity.ok(order);
    }

    // 특정 회원의 주문 조회
    @GetMapping("/mem")
    public ResponseEntity<List<OrderDto>> getOrdersByMember() {
        Long memberId = rq.getMemberId(); // 현재 로그인한 사용자의 ID
        List<OrderDto> orderDtos = orderCommonService.findByConsumerId(memberId);
        return ResponseEntity.ok(orderDtos);
    }

    // 주문 생성
    @PostMapping("/create")
    public ResponseEntity<OrderDto> create() {
        Long memberId = rq.getMemberId();
        OrderDto createdOrder = orderUserService.create(memberId);
        return ResponseEntity.ok(createdOrder);
    }

    // 주문 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable("id") Long id) {
        orderUserService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // 주문 상태 변경 및 배송 처리 (PAID 상태 주문에 대해 처리)
    @PostMapping("/process/{orderId}")
    public ResponseEntity<OrderDto> processOrder(@PathVariable("orderId") Long orderId) {
        OrderDto updatedOrder = orderUserService.processDelivery(orderId);
        return ResponseEntity.ok(updatedOrder);
    }
}
