package com.nbe.NBE3_4_1_Team15.domain.product.entity;

import com.nbe.NBE3_4_1_Team15.domain.member.entity.Member;
import com.nbe.NBE3_4_1_Team15.domain.product.dto.ProductUpdateDto;
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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private String category; // 일단 간단하게 String으로 설정. 나중에 객체로 변경 해야할 수도 있음

    @Column(nullable = false)
    private Integer stock;

    public void updateFromDto(ProductUpdateDto updateDto) {
        this.price = updateDto.getPrice();
        this.description = updateDto.getDescription();
        this.stock = updateDto.getStock();
    }
}
