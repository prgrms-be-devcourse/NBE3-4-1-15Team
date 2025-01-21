package com.nbe.NBE3_4_1_Team15.global.rq;

import com.nbe.NBE3_4_1_Team15.domain.member.entity.Member;
import com.nbe.NBE3_4_1_Team15.domain.member.service.MemberService;
import com.nbe.NBE3_4_1_Team15.global.exceptions.ServiceException;
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
public class Rq {
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final MemberService memberService;

    public void setLogin(Member member) {
        UserDetails user = new SecurityUser(
                member.getId(),
                member.getEmail(),
                "",
                member.getAuthorities()
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user,
                user.getPassword(),
                user.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public Member getActor() {
        return Optional.ofNullable(
                        SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                )
                .map(Authentication::getPrincipal)
                .filter(principal -> principal instanceof SecurityUser)
                .map(principal -> (SecurityUser) principal)
                .map(securityUser -> new Member(securityUser.getId(), securityUser.getUsername()))
                .orElse(null);
    }

    public void setCookie(String name, String value) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .path("/")
                .domain("localhost")
                .sameSite("Strict")
                .secure(true)
                .httpOnly(true)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
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
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    public void setHeader(String name, String value) {
        response.setHeader(name, value);
    }

    public String getHeader(String name) {
        return request.getHeader(name);
    }

    public Optional<Member> findByActor() {
        Member actor = getActor();

        if (actor == null) return Optional.empty();

        return memberService.findById(actor.getId());
    }

    public Long getMemberId() {
        return Optional.ofNullable(getActor())
                .map(Member::getId)
                .orElseThrow(() -> new ServiceException("401-1", "로그인이 필요합니다."));
    }
}
