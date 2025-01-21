package com.nbe.NBE3_4_1_Team15.domain.product.dto;

import com.nbe.NBE3_4_1_Team15.domain.product.entity.Product;
import com.nbe.NBE3_4_1_Team15.domain.product.type.ProductType;
import lombok.Getter;

@Getter
public class ProductDto {
    private Long id;
    private Long sellerId;             // ★ 추가
    private String name;
    private int price;
    private int stock;
    private String description;
    private ProductType productType;

    public ProductDto(Product product) {
        this.id = product.getId();
        this.sellerId = product.getSeller().getId(); // ★ product.getSeller().getId()
        this.name = product.getName();
        this.price = product.getPrice();
        this.stock = product.getStock();
        this.description = product.getDescription();
        this.productType = product.getProductType();
    }
}
