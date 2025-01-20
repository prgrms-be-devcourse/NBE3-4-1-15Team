package com.nbe.NBE3_4_1_Team15.domain.cart.entity;
import com.nbe.NBE3_4_1_Team15.domain.cartProduct.entity.CartProduct;
import com.nbe.NBE3_4_1_Team15.domain.member.entity.Member;
import com.nbe.NBE3_4_1_Team15.domain.order.entity.Order;
import com.nbe.NBE3_4_1_Team15.global.jpa.entity.BaseEntity;
import com.nbe.NBE3_4_1_Team15.global.jpa.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart")
public class Cart extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "cart", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<CartProduct> cartProducts; // cart에 있는 상품

    public int getTotalPrice() {
        return cartProducts.stream()
                .mapToInt(CartProduct::getTotalPrice) // CartProduct의 getTotalPrice 호출
                .sum();
    }

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders;
    @OneToOne(fetch = FetchType.LAZY) // Cart와 Member는 1:1 관계
    @JoinColumn(name = "consumer_id") // 외래 키 이름 지정
    private Member consumer;
}