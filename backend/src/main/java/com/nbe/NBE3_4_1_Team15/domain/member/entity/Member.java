package com.nbe.NBE3_4_1_Team15.domain.member.entity;

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
public class Member extends BaseEntity {
    private String username;

    private String password;

    private String email;

    private String address;

    private String postCode; // 우편번호

    @Enumerated(EnumType.STRING)
    private MemberType memberType;
}
