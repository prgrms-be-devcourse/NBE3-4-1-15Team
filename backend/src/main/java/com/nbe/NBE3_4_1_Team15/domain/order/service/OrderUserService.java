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

    // 주문 생성
    @Transactional
    public OrderDto create(Long memberId) {
        // 1. 회원 정보 조회
        Member consumer = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found: ID " + memberId));

        // 2. 장바구니 확인
        Cart cart = consumer.getCart();
        if (cart == null) {
            // 장바구니가 없을 경우 자동 생성 (필요 시)
            cart = Cart.builder()
                    .member(consumer)
                    .cartProducts(List.of()) // 빈 장바구니 초기화
                    .build();
            cartRepository.save(cart);
        }

        // 3. 장바구니 내 상품 확인
        if (cart.getCartProducts() == null || cart.getCartProducts().isEmpty()) {
            throw new IllegalStateException("장바구니에 상품이 없습니다. 주문을 생성할 수 없습니다.");
        }

        // 4. 장바구니 내 상품의 유효성 검사
        for (CartProduct cp : cart.getCartProducts()) {
            if (cp == null || cp.getProduct() == null) {
                throw new IllegalStateException("장바구니에 유효하지 않은 상품이 포함되어 있습니다.");
            }
        }

        // 5. 총 금액 계산
        int totalPrices = cart.getTotalPrice();
        if (totalPrices <= 0) {
            throw new IllegalStateException("장바구니의 총 금액이 0원 이하입니다. 주문을 생성할 수 없습니다.");
        }

        // 6. 주문 생성
        Order order = Order.builder()
                .consumer(consumer)
                .cart(cart)
                .orderType(OrderType.ORDERED)
                .totalPrice(totalPrices)
                .orderDate(LocalDateTime.now())
                .build();

        // 7. 주문 저장
        Order savedOrder = orderRepository.save(order);

        // 8. 카트 초기화
        cart.getCartProducts().clear();
        cartRepository.save(cart);

        // 9. 생성된 주문 반환
        return OrderDto.of(savedOrder);
    }




    public void delete(Long id) {
        orderRepository.findById(id).ifPresent(orderRepository::delete);
    }

    // 주문 상태 변경 및 배송 처리
    public OrderDto processDelivery(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));

        if (!OrderType.PAID.equals(order.getOrderType())) {
            throw new IllegalStateException("Order must be in PAID state to process delivery.");
        }

        LocalTime cutoffTime = LocalTime.of(14, 0);
        LocalDateTime now = LocalDateTime.now();

        if (now.toLocalTime().isBefore(cutoffTime)) {
            order.setOrderType(OrderType.DELIVERY);
            order.setOrderDate(now.toLocalDate().atStartOfDay());
        } else {
            order.setOrderType(OrderType.DELIVERY);
            order.setOrderDate(now.toLocalDate().plusDays(1).atStartOfDay());
        }

        orderRepository.save(order);
        return OrderDto.of(order);
    }
}
