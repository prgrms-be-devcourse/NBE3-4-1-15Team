package com.nbe.NBE3_4_1_Team15.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * "주문에 포함된 상품"을 표현하기 위한 DTO
 */
@Data
@AllArgsConstructor
public class OrderProductDto {
    private String productName;   // 상품 이름
    private int productPrice;     // 상품 가격
    private int quantity;         // 주문 수량
    private int totalPrice;       // 상품별 (가격 x 수량)
}
