package com.nbe.NBE3_4_1_Team15.domain.order.controller;

import com.nbe.NBE3_4_1_Team15.domain.order.dto.OrderDto;
import com.nbe.NBE3_4_1_Team15.domain.order.service.OrderService;
import com.nbe.NBE3_4_1_Team15.domain.order.type.OrderType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/orders") // 기본 경로
@RequiredArgsConstructor
public class OrderUserController {
    private final OrderService orderService;

    // 특정 주문 조회
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable("id") Long id) {
        OrderDto order = orderService.findById(id);
        return ResponseEntity.ok(order);
    }

    // 특정 memberId를 가진 모든 Order 조회
    // user 계정일 경우 기본으로 memberId 보고 자동 조회
    // admin 계정일 경우 모든 주문 내역이 나오고 memberId 입력시 조회
    @GetMapping("/mem")
    public ResponseEntity<List<OrderDto>> getOrdersByMemberId(@RequestParam(name = "memberId") Long memberId) {
        List<OrderDto> orderDtos = orderService.findByConsumerId(memberId);
        return ResponseEntity.ok(orderDtos);
    }

    //주문 생성
    //관리자는 굳이 주문 생성 필요없을 듯
    @PostMapping("")
    public ResponseEntity<OrderDto> create(@RequestParam(name = "memberId") Long memberId,
                                           @RequestParam(name = "totalPrice") Integer totalPrice){
        OrderDto createOrder = orderService.create(memberId, totalPrice);
        return ResponseEntity.ok(createOrder);
    }

    // 주문 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable("id") Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    //주문 상태 변경 및 배송 처리 (14시 기준 데이터 처리)
    //조회 버튼으로 처리 할 예정
    @PostMapping("/process/{orderId}")
    public ResponseEntity<OrderDto> processOrder(@PathVariable("orderId") Long orderId) {
        OrderDto updatedOrder = orderService.processDelivery(orderId);
        return ResponseEntity.ok(updatedOrder);
    }
}
