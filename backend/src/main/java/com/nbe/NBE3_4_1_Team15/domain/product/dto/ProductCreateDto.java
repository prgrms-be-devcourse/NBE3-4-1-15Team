//package com.nbe.NBE3_4_1_Team15.domain.product.dto;
//
//import com.nbe.NBE3_4_1_Team15.domain.member.entity.Member;
//import com.nbe.NBE3_4_1_Team15.domain.product.entity.Product;
//import jakarta.persistence.Column;
//import jakarta.persistence.FetchType;
//import jakarta.persistence.ManyToOne;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Positive;
//import lombok.AccessLevel;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//@Getter
//@NoArgsConstructor(access = AccessLevel.PUBLIC)
//public class ProductCreateDto {
//
//    @NotBlank
//    private Member member;
//    @NotBlank
//    private String name;
//    @Positive
//    private Integer price;
//    @NotBlank
//    private String description;
//    private String category; // 일단 간단하게 String으로 설정. 나중에 객체로 변경 해야할 수도 있음
//    @NotNull
//    private Integer stock;
//
//    public ProductCreateDto(Member member, String name, Integer price, String description, String category, Integer stock){
//        this.member= member;
//        this.name = name;
//        this.price = price;
//        this.description = description;
//        this.category = category;
//        this.stock = stock;
//    }
//
//    public Product toEntity(){
//        return Product.builder()
//                .seller(this.getMember())
//                .name(this.getName())
//                .price(this.getPrice())
//                .description(this.getDescription())
//                .category(this.getCategory())
//                .stock(this.getStock())
//                .build();
//    }
//}
