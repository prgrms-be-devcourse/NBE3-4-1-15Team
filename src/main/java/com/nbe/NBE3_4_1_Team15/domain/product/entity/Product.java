package com.nbe.NBE3_4_1_Team15.domain.product.entity;

import com.nbe.NBE3_4_1_Team15.domain.member.entity.Member;
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
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private String name;

    private Integer price;

    private String description;

    private String category; // 일단 간단하게 String으로 설정. 나중에 객체로 변경 해야할 수도 있음

    private Integer stock;
}
