package com.nbe.NBE3_4_1_Team15.global.rq;


import com.nbe.NBE3_4_1_Team15.domain.member.entity.Member;
import com.nbe.NBE3_4_1_Team15.domain.member.service.MemberService;
import com.nbe.NBE3_4_1_Team15.global.security.SecurityUser;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Arrays;
import java.util.Optional;

@RequestScope
@Component
@RequiredArgsConstructor
public class Rq { // RequestScope에서 사용하는 클래스
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final MemberService memberService;

    /**
     * JWT를 통한 로그인
     * filter단에서 자동 로그인이므로 Rq에서 처리함
     * */
    public void setLogin(Member member) {
        UserDetails user = new SecurityUser(
                member.getId(),
                member.getEmail(),
                "",// JWT 방식에서는 비밀번호를 통한 로그인을 하지 않기 때문에 필요없다.
                member.getAuthorities()
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, // principal
                user.getPassword(), // credential
                user.getAuthorities() // authorities
        );

        // SecurityContext에 user 등록
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * Actor는 Member(엔티티)와 같은 Class를 사용하지만 JWT의 정보(email, id)만으로 생성된 가짜 엔티티이다.
     * Actor는 권한을 가진 사용자
     * */
    public Member getActor() {
        return Optional.ofNullable(
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication()
                )
                .map(Authentication::getPrincipal)
                .filter(principal -> principal instanceof SecurityUser) // SecurityContext에 있는 값이 우리의 양식이 맞는지, findById를 위해 SecurityUser 사용
                .map(principal -> (SecurityUser) principal) // 맞다면 getter사용을 위해 형변환 사용
                .map(securityUser -> new Member(securityUser.getId(), securityUser.getUsername())) //이렇게 사용하기 위해 엔티티에 생성자 추가함
                .orElse(null); // 만약 잘 안되면 null 반환
    }

    public void setCookie(String name, String value) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .path("/")
                .domain("localhost")
                .sameSite("Strict")
                .secure(true)
                .httpOnly(true)
                .build();
        response.addHeader(cookie.getName(), value);
    }

    public String getCookieValue(String name) {
        return Optional.ofNullable(request.getCookies())
                .stream()
                .flatMap(Arrays::stream)
                .filter(cookie -> cookie.getName().equals(name))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    public void deleteCookie(String name) {
        ResponseCookie cookie = ResponseCookie.from(name, null)
                .path("/")
                .domain("localhost")
                .sameSite("Strict")
                .secure(true)
                .httpOnly(true)
                .maxAge(0) // maxAge = 0 으로 설정하여 쿠키를 삭제한다.
                .build();

        response.addHeader("Set-Cookie", cookie.getName());
    }

    public void setHeader(String name, String value) {
        response.setHeader(name, value);
    }

    public String getHeader(String name) {
        return request.getHeader(name);
    }

    /**
     * getActor()와는 다르게 DB를 조회하여 완전한 엔티티가 반환된다.
     * */
    public Optional<Member> findByActor() {
        Member actor = getActor();

        if (actor == null) return Optional.empty();

        return memberService.findById(actor.getId());
    }
}
