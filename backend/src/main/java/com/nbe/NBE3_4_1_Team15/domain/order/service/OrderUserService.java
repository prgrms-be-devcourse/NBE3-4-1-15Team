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
        // 1. 회원 조회
        Member consumer = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found: ID " + memberId));

        // 2. 회원의 Cart 가져오기
        Cart cart = consumer.getCart();
        // (주의) 만약 cart가 null이라면, cartRepository.findOrCreateCart... 등의 로직을 똑같이 써도 됨
        if (cart == null) {
            throw new IllegalStateException("장바구니가 존재하지 않습니다. 주문을 생성할 수 없습니다.");
        }

        // 3. 상품이 비었는지 확인
        if (cart.getCartProducts() == null || cart.getCartProducts().isEmpty()) {
            throw new IllegalStateException("장바구니에 상품이 없습니다. 주문을 생성할 수 없습니다.");
        }

        // 4. 총 금액
        int totalPrices = cart.getTotalPrice();
        if (totalPrices <= 0) {
            throw new IllegalStateException("장바구니 총금액이 0원 이하입니다. 주문 불가.");
        }

        // 5. 주문 생성
        Order order = Order.builder()
                .consumer(consumer)
                .cart(cart)
                .orderType(OrderType.ORDERED)
                .totalPrice(totalPrices)
                .orderDate(LocalDateTime.now())
                .build();

        Order savedOrder = orderRepository.save(order);

        // 6. 카트 비우기
        cart.getCartProducts().clear();
        cartRepository.save(cart);

        // 7. 응답
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
