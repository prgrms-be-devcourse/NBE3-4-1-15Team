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

    private String postCode; // 우편번호

    @Enumerated(EnumType.STRING)
    private MemberType memberType;

    @OneToMany(mappedBy = "member", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Product> products; // 판매상품, 회원 도메인에 생명주기가 의존적이므로 영속성 전이 PERSIST, REMOVE 적용

    @OneToOne
    private Cart cart; // 장바구니

    @OneToMany(mappedBy = "consumer", cascade = {CascadeType.PERSIST}, orphanRemoval = true)
    private List<Order> orders;

    public Member(long id, String email) { //SiteUser를 위한 생성자
        this.setId(id);
        this.email = email;
    }

    /**
     * Spring Security의 User의 AuthorityList 스펙으로 변환시켜주는 메서드
     * */
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getAuthoritiesAsStringList()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    // 권한들을 문자열 리스트로 변경해주는 메서드
    private List<String> getAuthoritiesAsStringList() {
        List<String> authorities = new ArrayList<>();
        if (memberType == MemberType.ROLE_MEMBER) {
            authorities.add("ROLE_MEMBER");
        }
        return authorities;
    }
}
