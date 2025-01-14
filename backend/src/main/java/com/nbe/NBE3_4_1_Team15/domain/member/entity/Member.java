package com.nbe.NBE3_4_1_Team15.domain.member.entity;

import com.nbe.NBE3_4_1_Team15.domain.cart.entity.Cart;
import com.nbe.NBE3_4_1_Team15.domain.member.type.MemberType;
import com.nbe.NBE3_4_1_Team15.domain.order.entity.Order;
import com.nbe.NBE3_4_1_Team15.domain.product.entity.Product;
import com.nbe.NBE3_4_1_Team15.global.jpa.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseTime {
    private String username;

    private String password;

    private String email;

    private String address;

    private String postCode; // 우편번호

    @Enumerated(EnumType.STRING)
    private MemberType memberType;

    @OneToMany(mappedBy = "member", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Product> products; // 판매상품, 회원 도메인에 생명주기가 의존적이므로 영속성 전이 PERSIST, REMOVE 적용

    @OneToOne
    private Cart cart; // 장바구니

    @OneToMany(mappedBy = "consumer", cascade = {CascadeType.PERSIST}, orphanRemoval = true)
    private List<Order> orders;
}
