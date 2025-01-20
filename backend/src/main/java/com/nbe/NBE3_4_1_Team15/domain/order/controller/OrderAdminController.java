package com.nbe.NBE3_4_1_Team15.domain.order.controller;

import com.nbe.NBE3_4_1_Team15.domain.order.dto.OrderDto;
import com.nbe.NBE3_4_1_Team15.domain.order.service.OrderService;
import com.nbe.NBE3_4_1_Team15.domain.order.type.OrderType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/orders") // 기본 경로
@RequiredArgsConstructor
public class OrderAdminController {
    private final OrderService orderService;

    // 모든 주문 조회
    @GetMapping("")
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        List<OrderDto> orders = orderService.findAll();
        return ResponseEntity.ok(orders);
    }

    // 특정 memberId를 가진 모든 Order 조회
    // user 계정일 경우 기본으로 memberId 보고 자동 조회
    // admin 계정일 경우 모든 주문 내역이 나오고 memberId 입력시 조회
    @GetMapping("/mem")
    public ResponseEntity<List<OrderDto>> getOrdersByMemberId(@RequestParam(name = "memberId") Long memberId) {
        List<OrderDto> orderDtos = orderService.findByConsumerId(memberId);
        return ResponseEntity.ok(orderDtos);
    }

    //주문 삭제
    //관리자는 필요할지 모르겠는데 일단 넣어둠
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable("id") Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    //관리자가 상태변경하는 코드
    //프론트 측에서 콤보박스로 해결하면 될 듯
    @PutMapping("/update/{orderId}")
    public ResponseEntity<OrderDto> updateOrderType(
            @PathVariable("orderId") Long orderId,
            @RequestParam(name = "orderType") OrderType orderType){
        OrderDto updateOrder = orderService.orderTypeUpdate(orderId, orderType);
        return ResponseEntity.ok(updateOrder);
    }
}
