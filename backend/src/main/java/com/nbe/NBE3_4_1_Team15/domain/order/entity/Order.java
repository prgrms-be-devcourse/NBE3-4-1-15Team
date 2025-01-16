package com.nbe.NBE3_4_1_Team15.domain.order.entity;

import com.nbe.NBE3_4_1_Team15.domain.cart.entity.Cart;
import com.nbe.NBE3_4_1_Team15.domain.member.entity.Member;
import com.nbe.NBE3_4_1_Team15.domain.order.type.OrderType;
import com.nbe.NBE3_4_1_Team15.global.jpa.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
<<<<<<< HEAD
import java.util.Date;
=======
>>>>>>> 242d40f3cfa8ba6bfd8a6ada82bc44b42d95e36a

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
<<<<<<< HEAD
    private OrderType orderType;

    private Integer totalPrice;

    private LocalDateTime orderDate;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;

=======
    private OrderType orderType; // 주문 상태 (Enum으로 관리)
>>>>>>> 242d40f3cfa8ba6bfd8a6ada82bc44b42d95e36a

    private Integer totalPrice; // 주문 총 금액
    private LocalDateTime orderDate; // 주문이 이루어진 시간private LocalDateTime orderDate;
}
