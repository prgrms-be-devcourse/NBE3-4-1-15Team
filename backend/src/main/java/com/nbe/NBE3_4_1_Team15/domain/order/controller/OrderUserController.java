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
    // 회원의 모든 주문을 조회 했을 경우 한개를 선택
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable("id") Long id) {
        OrderDto order = orderCommonService.findById(id);
        return ResponseEntity.ok(order);
    }

    // 특정 회원의 주문 조회
    // 조회 버튼을 사용하거나 useState 또는 useEffect로 프론트에서 구현하면 될듯
    @GetMapping("/mem")
    public ResponseEntity<List<OrderDto>> getOrdersByMember() {
        Long memberId = rq.getMemberId(); // 현재 로그인한 사용자의 ID 가져오기
        List<OrderDto> orderDtos = orderCommonService.findByConsumerId(memberId); // 주문 조회
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
        orderUserService.delete(id); // 삭제는 사용자와 관리자 모두 가능
        return ResponseEntity.noContent().build();
    }

    // 주문 상태 변경 및 배송 처리
    // 14시 기준 orderId를 기준으로 OrderType 변경
    // PAID 상태일 경우 DEILVERY로 변경
    @PostMapping("/process/{orderId}")
    public ResponseEntity<OrderDto> processOrder(@PathVariable("orderId") Long orderId) {
        OrderDto updatedOrder = orderUserService.processDelivery(orderId);
        return ResponseEntity.ok(updatedOrder);
    }
}
