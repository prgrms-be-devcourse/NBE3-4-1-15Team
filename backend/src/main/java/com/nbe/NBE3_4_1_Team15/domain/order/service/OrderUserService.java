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

@Service
@RequiredArgsConstructor
public class OrderUserService {
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;

    // 주문 생성
    @Transactional
    public OrderDto create(Long memberId) {
        Member consumer = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        Cart cart = cartRepository.findByConsumer_Id(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found for memberId: " + memberId));

        // 디버깅: 장바구니 내용 확인
        System.out.println("Cart ID: " + cart.getId());
        System.out.println("Number of Products in Cart: " + cart.getCartProducts().size());
        for (CartProduct cp : cart.getCartProducts()) {
            System.out.println("Product Name: " + cp.getProduct().getName());
            System.out.println("Price: " + cp.getProduct().getPrice());
            System.out.println("Quantity: " + cp.getQuantity());
        }

        int totalPrices = cart.getTotalPrice();
        System.out.println("Total Price: " + totalPrices);

        Order order = Order.builder()
                .consumer(consumer)
                .cart(cart)
                .orderType(OrderType.ORDERED)
                .totalPrice(totalPrices)
                .orderDate(LocalDateTime.now())
                .build();

        // 주문 저장
        Order savedOrder = orderRepository.save(order);

        // 카트 초기화
        cart.getCartProducts().clear();
        cartRepository.save(cart);

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
