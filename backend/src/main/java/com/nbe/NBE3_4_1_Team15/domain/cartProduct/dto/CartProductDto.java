package com.nbe.NBE3_4_1_Team15.domain.cartProduct.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartProductDto {
    private Long productId; // 상품 ID
    private String productName; // 상품 이름
    private int price; // 상품 가격
    private int quantity; // 상품 수량
    private int totalPrice; // 총 가격(가격 * 수량)
}
