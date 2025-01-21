package com.nbe.NBE3_4_1_Team15.domain.product.dto;

import com.nbe.NBE3_4_1_Team15.domain.product.entity.Product;
import com.nbe.NBE3_4_1_Team15.domain.product.type.ProductType;

import lombok.Getter;

@Getter
public class ProductDto {
    private long id;
    private long sellerId;
    private String name;
    private int price;
    private String description;
    private ProductType productType;
    private int stock;

    public ProductDto(Product product) {
        this.id = product.getId();
        this.sellerId = product.getSeller().getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.description = product.getDescription();
        this.productType = product.getProductType();
        this.stock = product.getStock();
    }
}
