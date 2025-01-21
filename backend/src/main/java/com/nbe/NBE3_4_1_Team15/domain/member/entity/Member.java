package com.nbe.NBE3_4_1_Team15.domain.member.entity;

import com.nbe.NBE3_4_1_Team15.domain.cart.entity.Cart;
import com.nbe.NBE3_4_1_Team15.domain.member.type.MemberType;
import com.nbe.NBE3_4_1_Team15.domain.order.entity.Order;
import com.nbe.NBE3_4_1_Team15.domain.product.entity.Product;
import com.nbe.NBE3_4_1_Team15.global.jpa.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseTime {

    @Column(nullable = false)
    private String email;

    private String password;

    @Column(unique = true)
    private String refreshToken;

    private String nickname;
    private String address;
    private String postCode;

    @Enumerated(EnumType.STRING)
    private MemberType memberType;

    // 회원이 판매하는 상품들(예시)
    @OneToMany(mappedBy = "seller", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Product> products;

    // 회원이 가진 장바구니 (1:1)
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Cart cart;

    // 주문 목록
    @OneToMany(mappedBy = "consumer", cascade = {CascadeType.PERSIST}, orphanRemoval = true)
    private List<Order> orders;

    public Member(long id, String email) {
        this.setId(id);
        this.email = email;
    }

    // 권한 관련 (예시)
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getAuthoritiesAsStringList()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    private List<String> getAuthoritiesAsStringList() {
        List<String> authorities = new ArrayList<>();
        if (memberType == MemberType.ROLE_MEMBER) {
            authorities.add("ROLE_MEMBER");
        }
        return authorities;
    }

    public boolean matchPassword(String password) {
        return this.password.equals(password);
    }
}
