package com.nbe.NBE3_4_1_Team15.global.security;

import com.nbe.NBE3_4_1_Team15.domain.member.entity.Member;
import com.nbe.NBE3_4_1_Team15.domain.member.service.MemberService;
import com.nbe.NBE3_4_1_Team15.global.rq.Rq;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends OncePerRequestFilter {
    private final MemberService memberService;
    private final Rq rq;

    record AuthTokens(String refreshToken, String accessToken) {}

    private AuthTokens getAuthTokenFromRequest() {

        // 1. (Header) 토큰 두개가 Authorization : Bearer refreshToken accessToken 형태로 올 때
        String authorization = rq.getHeader("Authorization");

        if(authorization != null && authorization.startsWith("Bearer ")) {
            String _tokens = authorization.substring(7); //"Bearer ".length();
            String[] tokens = _tokens.split(" ");

            if (tokens.length == 2)
                return new AuthTokens(tokens[0], tokens[1]); // refreshToken, accessToken
        }

        // 2. (Cookie) 토큰이 각각 ~~-Token : ~~Token으로 올 때

        String refreshToken = rq.getCookieValue("Refresh-Token");
        String accessToken = rq.getCookieValue("Access-Token");

        if (refreshToken != null && accessToken != null) { // 헤더에 토큰이 다 있다면
            return new AuthTokens(refreshToken, accessToken);
        }

        // 3. 아무 인증용 값이 오지 않을 때
        return null;
    }

    private void refreshAccessToken(Member member) {
        String newAccessToken = memberService.genAccessToken(member);

        rq.setHeader("Access-Token", newAccessToken);
        rq.setHeader("Refresh-Token", newAccessToken);
    }

    private Member refreshAccessTokenByRefreshToken(String refreshToken) {
        Optional<Member> opMemberByRefreshToken = memberService.findByRefreshToken(refreshToken);

        if(opMemberByRefreshToken.isEmpty()) {
            return null;
        }

        Member member = opMemberByRefreshToken.get();

        refreshAccessToken(member);

        return member;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        AuthTokens authTokens = getAuthTokenFromRequest();

        if(authTokens == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String refreshToken = authTokens.refreshToken;
        String accessToken = authTokens.accessToken;

        Member member = memberService.getMemberFromAccessToken(accessToken);

        if(member == null) {
            member = refreshAccessTokenByRefreshToken(refreshToken);
        }

        if(member != null) {
            rq.setLogin(member);
        }

        filterChain.doFilter(request, response);
    }
}
