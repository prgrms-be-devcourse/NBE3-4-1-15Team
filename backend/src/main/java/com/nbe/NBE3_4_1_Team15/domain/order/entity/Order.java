package com.nbe.NBE3_4_1_Team15.domain.order.entity;

import com.nbe.NBE3_4_1_Team15.domain.cart.entity.Cart;
import com.nbe.NBE3_4_1_Team15.domain.member.entity.Member;
import com.nbe.NBE3_4_1_Team15.domain.order.type.OrderType;
import com.nbe.NBE3_4_1_Team15.global.jpa.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="order_table") // 데이터베이스 테이블 이름
public class Order extends BaseTime {
    @ManyToOne(fetch = FetchType.LAZY)
    private Member consumer; // 주문자 정보 (Member와 다대일 관계)

    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    private Integer totalPrice;

    private LocalDateTime orderDate; // 주문이 이루어진 시간

    @ManyToOne(fetch = FetchType.LAZY)
    private Cart cart;
}