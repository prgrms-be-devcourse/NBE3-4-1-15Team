package com.nbe.NBE3_4_1_Team15.domain.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nbe.NBE3_4_1_Team15.domain.order.entity.Order;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDto {
    private Long id; // 주문 고유 ID

    @JsonProperty("created_at")
    private LocalDateTime createAt; // 주문 생성 시간

    @JsonProperty("updated_at")
    private LocalDateTime updateAt; // 주문 마지막 수정 시간

    private Long consumerId; // 주문자의 ID
    private String orderType; // 주문 상태 (예: BEFORE_ORDER, COMPLETE_ORDER 등)
    private Integer totalPrice; // 주문 총 금액

    @JsonProperty("order_date")
    private LocalDateTime orderDate; // 주문이 이루어진 시간

    private Long cartId;

    // 엔티티를 DTO로 변환하는 정적 팩토리 메서드
    public static OrderDto of(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .createAt(order.getCreateAt())
                .updateAt(order.getUpdateAt())
                .consumerId(order.getConsumer() != null ? order.getConsumer().getId() : null)
                .orderType(order.getOrderType() != null ? order.getOrderType().name() : null)
                .totalPrice(order.getTotalPrice())
                .orderDate(order.getOrderDate())
                .cartId(order.getCart() != null ? order.getCart().getId() : null)
                .build();
    }
}
