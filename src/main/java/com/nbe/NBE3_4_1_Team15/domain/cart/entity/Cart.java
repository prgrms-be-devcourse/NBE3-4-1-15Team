package com.nbe.NBE3_4_1_Team15.domain.cart.entity;

import com.nbe.NBE3_4_1_Team15.domain.cartProduct.entity.CartProduct;
import com.nbe.NBE3_4_1_Team15.domain.member.entity.Member;
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
// cart.java git 업로드용 주석 추가
    @OneToMany(mappedBy = "cart", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<CartProduct> cartProducts; // cart에 있는 상품

    public int getTotalPrice() {
        int totalPrice = 0;
        for (CartProduct cartProduct : cartProducts) {
            totalPrice += cartProduct.getProduct().getPrice() * cartProduct.getQuantity();
        }
        return totalPrice;
    }
}


//    private int member_id; // 회원 ID와 연관
//    private int product_id; // 상품 ID와 연관
//    private int quantity; // 상품 수량
