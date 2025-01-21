package com.nbe.NBE3_4_1_Team15.domain.product.entity;

import com.nbe.NBE3_4_1_Team15.domain.member.entity.Member;
import com.nbe.NBE3_4_1_Team15.domain.member.type.MemberType;
import com.nbe.NBE3_4_1_Team15.domain.product.dto.ProductUpdateDto;
import com.nbe.NBE3_4_1_Team15.domain.product.type.ProductType;
import com.nbe.NBE3_4_1_Team15.global.jpa.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseTime {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member seller;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductType productType;

    @Column(nullable = false)
    private Integer stock;

    public void updateFromDto(ProductUpdateDto updateDto) {
        this.price = updateDto.getPrice();
        this.description = updateDto.getDescription();
        this.stock = updateDto.getStock();
    }
}
