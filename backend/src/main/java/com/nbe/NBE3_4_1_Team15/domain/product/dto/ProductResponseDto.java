//package com.nbe.NBE3_4_1_Team15.domain.product.dto;
//
//import com.nbe.NBE3_4_1_Team15.domain.member.entity.Member;
//import com.nbe.NBE3_4_1_Team15.domain.product.entity.Product;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.criteria.CriteriaBuilder;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Positive;
//import lombok.AccessLevel;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDateTime;
//
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//public class ProductResponseDto {
//
//    private Long id;
//    private Member member;
//    private String name;
//    private Integer price;
//    private String description;
//    private String category; // 일단 간단하게 String으로 설정. 나중에 객체로 변경 해야할 수도 있음
//    private Integer stock;
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;
//
//    public ProductResponseDto(Long id, Member member, String name, Integer price,
//                              String description, String category, Integer stock, LocalDateTime createdAt, LocalDateTime updatedAt){
//        this.id = id;
//        this.member = member;
//        this.price = price;
//        this.description = description;
//        this.category = category;
//        this.stock = stock;
//        this.createdAt = createdAt;
//        this.updatedAt = updatedAt;
//    }
//
//    public static ProductResponseDto from(Product product){
//        return new ProductResponseDto(
//                product.getId(),
//                product.getMember(),
//                product.getName(),
//                product.getPrice(),
//                product.getDescription(),
//                product.getCategory(),
//                product.getStock(),
//                product.getCreateAt(),
//                product.getUpdateAt()
//        );
//    }
//}
//
//
