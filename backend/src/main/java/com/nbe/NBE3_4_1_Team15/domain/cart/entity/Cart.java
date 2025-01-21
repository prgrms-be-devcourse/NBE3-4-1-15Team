package com.nbe.NBE3_4_1_Team15.domain.cart.entity;

import com.nbe.NBE3_4_1_Team15.domain.cartProduct.entity.CartProduct;
import com.nbe.NBE3_4_1_Team15.domain.member.entity.Member;
import com.nbe.NBE3_4_1_Team15.domain.order.entity.Order;
import com.nbe.NBE3_4_1_Team15.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart")
public class Cart extends BaseEntity {

    // "member"를 이 Cart의 주인(외래키)으로 지정, 1:1 관계
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 장바구니에 들어있는 상품 목록
    @OneToMany(mappedBy = "cart", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<CartProduct> cartProducts = new ArrayList<>();

    // Order(주문) 목록
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    public int getTotalPrice() {
        return cartProducts.stream()
                .mapToInt(CartProduct::getTotalPrice)
                .sum();
    }
}
