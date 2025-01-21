package com.nbe.NBE3_4_1_Team15.domain.cartProduct.entity;

import com.nbe.NBE3_4_1_Team15.domain.cart.entity.Cart;
import com.nbe.NBE3_4_1_Team15.domain.product.entity.Product;
import com.nbe.NBE3_4_1_Team15.global.jpa.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartProduct extends BaseTime {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private Integer quantity;

    public int getTotalPrice() {
        return product.getPrice() * quantity;
    }
}
