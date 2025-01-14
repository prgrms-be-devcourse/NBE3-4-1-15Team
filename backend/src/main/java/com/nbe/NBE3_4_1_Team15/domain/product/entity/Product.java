package com.nbe.NBE3_4_1_Team15.domain.product.entity;

import com.nbe.NBE3_4_1_Team15.domain.member.entity.Member;
import com.nbe.NBE3_4_1_Team15.domain.member.type.MemberType;
import com.nbe.NBE3_4_1_Team15.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    Member member;

    String name;

    Integer price;

    String description;

    String category; // 일단 간단하게 String으로 설정. 나중에 객체로 변경 해야할 수도 있음

    Integer stock;
}
