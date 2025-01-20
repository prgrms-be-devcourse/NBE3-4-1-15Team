package com.nbe.NBE3_4_1_Team15.domain.product.dto;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductUpdateDto {
    @Positive
    private Integer price;
    @NotBlank
    private String description;
    @NotNull
    private Integer stock;

    public ProductUpdateDto(Integer price, String description, Integer stock){
        this.price = price;
        this.description = description;
        this.stock = stock;
    }
}
