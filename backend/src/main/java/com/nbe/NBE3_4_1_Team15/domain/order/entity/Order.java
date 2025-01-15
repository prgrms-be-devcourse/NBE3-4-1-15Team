package com.nbe.NBE3_4_1_Team15.domain.order.entity;

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
@Table(name="order_table")
public class Order extends BaseTime {

    @ManyToOne(fetch = FetchType.LAZY)
    private Member consumer;

    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    private Integer totalPrice;

    private LocalDateTime orderDate;
}
