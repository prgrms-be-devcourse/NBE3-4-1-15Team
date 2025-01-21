package com.nbe.NBE3_4_1_Team15.global.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * SpringSecurity 전용 커스텀 클래스
 * UserDetails를 구현하는 User를 커스텀한 클래스
 * */
@Getter
public class SecurityUser extends User {
    private final long id;

    public SecurityUser(long id, String email, String password, Collection<? extends GrantedAuthority> authorities) {
        super(email, password, authorities);
        this.id = id;
    }
}
