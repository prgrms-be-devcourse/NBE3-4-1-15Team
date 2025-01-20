package com.nbe.NBE3_4_1_Team15.domain.order.controller;

import com.nbe.NBE3_4_1_Team15.domain.order.dto.OrderDto;
import com.nbe.NBE3_4_1_Team15.domain.order.service.OrderAdminService;
import com.nbe.NBE3_4_1_Team15.domain.order.service.OrderCommonService;
import com.nbe.NBE3_4_1_Team15.domain.order.type.OrderType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class OrderAdminController {
    private final OrderAdminService orderAdminService;
    private final OrderCommonService orderCommonService;

    // 모든 주문 조회
    @GetMapping("")
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        List<OrderDto> orders = orderAdminService.findAll();
        return ResponseEntity.ok(orders);
    }

    // 특정 주문 조회
    // 모든 주문을 조회 했을 경우 한개를 선택
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable("id") Long id) {
        OrderDto order = orderCommonService.findById(id);
        return ResponseEntity.ok(order);
    }

    // 특정 회원의 주문 조회
    @GetMapping("/mem")
    public ResponseEntity<List<OrderDto>> getOrdersByMemberId(@RequestParam(name = "memberId") Long memberId) {
        List<OrderDto> orderDtos = orderCommonService.findByConsumerId(memberId);
        return ResponseEntity.ok(orderDtos);
    }

    // 주문 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable("id") Long id) {
        orderAdminService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // 주문 상태 변경
    // 이건 시간 상관 안쓰고 관리자가 변경 시
    // 콤보박스같은걸로 처리하면 될듯
    @PutMapping("/update/{orderId}")
    public ResponseEntity<OrderDto> updateOrderType(
            @PathVariable("orderId") Long orderId,
            @RequestParam(name = "orderType") OrderType orderType) {
        OrderDto updatedOrder = orderAdminService.orderTypeUpdate(orderId, orderType);
        return ResponseEntity.ok(updatedOrder);
    }
}
