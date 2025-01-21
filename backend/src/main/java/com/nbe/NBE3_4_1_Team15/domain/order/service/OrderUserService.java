package com.nbe.NBE3_4_1_Team15.domain.order.service;

import com.nbe.NBE3_4_1_Team15.domain.cart.entity.Cart;
import com.nbe.NBE3_4_1_Team15.domain.cart.repository.CartRepository;
import com.nbe.NBE3_4_1_Team15.domain.cartProduct.entity.CartProduct;
import com.nbe.NBE3_4_1_Team15.domain.member.entity.Member;
import com.nbe.NBE3_4_1_Team15.domain.member.repository.MemberRepository;
import com.nbe.NBE3_4_1_Team15.domain.order.dto.OrderDto;
import com.nbe.NBE3_4_1_Team15.domain.order.entity.Order;
import com.nbe.NBE3_4_1_Team15.domain.order.repository.OrderRepository;
import com.nbe.NBE3_4_1_Team15.domain.order.type.OrderType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderUserService {
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;

    /**
     * 주문 생성
     */
    @Transactional
    public OrderDto create(Long memberId) {
        // 1) 회원 조회
        Member consumer = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found: " + memberId));

        // 2) Cart 가져오기
        Cart cart = consumer.getCart(); // 또는 cartRepository.findByMemberId(memberId);
        if (cart == null) {
            throw new IllegalStateException("장바구니가 없습니다. 주문을 생성할 수 없습니다.");
        }

        // 3) 카트 내 상품 검사
        if (cart.getCartProducts() == null || cart.getCartProducts().isEmpty()) {
            throw new IllegalStateException("장바구니가 비어 있습니다. 주문을 생성할 수 없습니다.");
        }
        for (CartProduct cp : cart.getCartProducts()) {
            if (cp.getProduct() == null) {
                throw new IllegalStateException("유효하지 않은 상품이 장바구니에 포함됨");
            }
        }

        // 4) 총금액 계산
        int totalPrices = cart.getTotalPrice();
        if (totalPrices <= 0) {
            throw new IllegalStateException("장바구니 총금액이 0 이하입니다. 주문 불가");
        }

        // 5) Order 생성
        Order order = Order.builder()
                .consumer(consumer)
                .cart(cart)
                .orderType(OrderType.ORDERED)
                .totalPrice(totalPrices)
                .orderDate(LocalDateTime.now())
                .build();

        Order savedOrder = orderRepository.save(order);

        // 6) 주문 생성 후 장바구니 비우기
        cart.getCartProducts().clear();
        cartRepository.save(cart);

        // 7) 반환
        return OrderDto.of(savedOrder);
    }

    /**
     * 주문 삭제
     */
    public void delete(Long id) {
        orderRepository.findById(id).ifPresent(orderRepository::delete);
    }

    /**
     * 주문 상태 변경(배송 처리)
     * PAID 상태의 주문에 대해 배송 처리 시,
     * - 당일 14시 이전 주문은 당일 14시에 배송 처리,
     * - 당일 14시 이후 주문은 다음날 14시에 배송 처리하도록 처리합니다.
     */
    public OrderDto processDelivery(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        if (!OrderType.PAID.equals(order.getOrderType())) {
            throw new IllegalStateException("주문 상태가 PAID가 아니면 배송 처리 불가");
        }

        LocalTime cutoffTime = LocalTime.of(14, 0);
        LocalDateTime now = LocalDateTime.now();

        if (now.toLocalTime().isBefore(cutoffTime)) {
            order.setOrderType(OrderType.DELIVERY);
            order.setOrderDate(now);  // 당일 14시에 배송 처리
        } else {
            order.setOrderType(OrderType.DELIVERY);
            order.setOrderDate(now.plusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(0));  // 다음날 14시에 배송 처리
        }

        Order saved = orderRepository.save(order);
        return OrderDto.of(saved);
    }
}
