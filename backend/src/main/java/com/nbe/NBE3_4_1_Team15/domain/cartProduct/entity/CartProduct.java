package com.nbe.NBE3_4_1_Team15.domain.cartProduct.entity;

import com.nbe.NBE3_4_1_Team15.domain.cart.entity.Cart;
import com.nbe.NBE3_4_1_Team15.domain.product.entity.Product;
import com.nbe.NBE3_4_1_Team15.global.jpa.entity.BaseTime;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

@Entity
@Getter
@Setter
@NoArgsConstructor // JPA에서 기본 생성자 필요
@AllArgsConstructor // 모든 필드를 사용하는 생성자 자동 생성
// CartProduct.java git 업로드용 주석 추가
public class CartProduct extends BaseTime {
    @ManyToOne(fetch = FetchType.LAZY)
    private Cart cart;

    // CartProduct를 사용하지 않으면 장바구니에 담을 때마다 Product에 cartId와 같은 cart에 대한 식별자가 필요한데 이는 product의 입장에서 필요도 없고 좋지도 않다.
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    private Integer quantity;
}
