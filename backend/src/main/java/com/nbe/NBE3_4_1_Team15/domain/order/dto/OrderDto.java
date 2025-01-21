package com.nbe.NBE3_4_1_Team15.domain.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nbe.NBE3_4_1_Team15.domain.cart.entity.Cart;
import com.nbe.NBE3_4_1_Team15.domain.cartProduct.entity.CartProduct;
import com.nbe.NBE3_4_1_Team15.domain.order.entity.Order;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDto {

    private Long id;              // 주문 ID
    private Long consumerId;      // 주문자 ID
    private String orderType;     // 주문 상태 (ORDERED, PAID, DELIVERY 등)
    private Integer totalPrice;   // 주문 총 금액

    @JsonProperty("order_date")
    private LocalDateTime orderDate; // 주문 시간

    // 필요에 따라 created_at, updated_at 등
    // private LocalDateTime createAt;
    // private LocalDateTime updateAt;

    private List<OrderProductDto> orderProducts; // ★ 이 주문에 포함된 상품 목록

    // 주문 -> Cart -> CartProduct -> Product 를 순회하여 담는다.
    public static OrderDto of(Order order) {
        // 1) 기본 필드 세팅
        OrderDto dto = OrderDto.builder()
                .id(order.getId())
                .consumerId(order.getConsumer() != null ? order.getConsumer().getId() : null)
                .orderType(order.getOrderType() != null ? order.getOrderType().name() : null)
                .totalPrice(order.getTotalPrice())
                .orderDate(order.getOrderDate())
                .orderProducts(new ArrayList<>())  // 일단 빈 배열
                .build();

        // 2) Cart -> CartProducts -> Product 순회
        Cart cart = order.getCart();
        if (cart != null && cart.getCartProducts() != null) {
            for (CartProduct cp : cart.getCartProducts()) {
                if (cp.getProduct() == null) continue;

                String productName = cp.getProduct().getName();
                int productPrice = cp.getProduct().getPrice();
                int quantity = cp.getQuantity();
                int total = productPrice * quantity;

                OrderProductDto opDto = new OrderProductDto(
                        productName,
                        productPrice,
                        quantity,
                        total
                );
                dto.getOrderProducts().add(opDto);
            }
        }

        return dto;
    }
}
