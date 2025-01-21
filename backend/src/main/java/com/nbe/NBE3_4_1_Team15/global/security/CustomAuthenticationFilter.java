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

    /**
     * AccessToken, RefreshToken을 담는 레코드
     * 필요하다면 refreshToken도 헤더나 쿠키에서 직접 꺼내어 셋팅할 수 있음
     */
    record AuthTokens(String refreshToken, String accessToken) {
    }

    /**
     * 현재 코드는 'Authorization' 헤더만 확인하여 AccessToken만 추출함.
     * RefreshToken을 헤더로 받을 거라면 별도로 파싱하는 로직을 추가해야 함.
     */
    private AuthTokens getAuthTokenFromRequest() {
        String authorization = rq.getHeader("Authorization");

        // Bearer 토큰
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String accessToken = authorization.substring(7);
            // 현재는 refreshToken은 null로 처리
            return new AuthTokens(null, accessToken);
        }
        return null;
    }

    /**
     * 새 AccessToken 발급 후, 원하는 헤더에 세팅
     */
    private void refreshAccessToken(Member member) {
        String newAccessToken = memberService.genAccessToken(member);
        // 필요하다면 RefreshToken도 재발급
        // String newRefreshToken = memberService.genRefreshToken(member);

        // 이 예시에서는 그냥 헤더에 넣어둠
        rq.setHeader("Access-Token", newAccessToken);
        rq.setHeader("Refresh-Token", newAccessToken);
    }

    /**
     * 전달받은 refreshToken으로 DB 등을 조회하여 유효하면 AccessToken 갱신
     */
    private Member refreshAccessTokenByRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            return null;
        }

        Optional<Member> opMemberByRefreshToken = memberService.findByRefreshToken(refreshToken);

        if (opMemberByRefreshToken.isEmpty()) {
            return null;
        }

        Member member = opMemberByRefreshToken.get();
        refreshAccessToken(member); // 새 AccessToken 생성 및 헤더 세팅
        return member;
    }

    /**
     * 필터 로직
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 1) /api/v1/members/refresh 요청이면 이 필터를 그냥 건너뛴다.
        //    refresh 요청은 이미 SecurityConfig에서 permitAll() 처리되고,
        //    Controller나 Service 계층에서 적절히 처리하면 됨.
        String uri = request.getRequestURI();
        if ("/api/v1/members/refresh".equals(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2) 나머지 요청들은 AccessToken을 검사
        AuthTokens authTokens = getAuthTokenFromRequest();
        if (authTokens == null) {
            System.out.println("토큰이 없습니다.");
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("AccessToken: " + authTokens.accessToken);

        // 3) AccessToken으로 사용자 조회
        Member member = memberService.getMemberFromAccessToken(authTokens.accessToken);

        // 4) 조회 실패 시, refreshToken으로 재발급 시도
        if (member == null) {
            System.out.println("토큰 검증 실패: " + authTokens.accessToken);
            member = refreshAccessTokenByRefreshToken(authTokens.refreshToken());
        }

        // 5) 최종적으로 member가 null이 아니면 인증 성공
        if (member != null) {
            System.out.println("사용자 인증 성공: " + member.getEmail());
            rq.setLogin(member);
        } else {
            System.out.println("사용자 인증 실패");
        }

        // 6) 다음 필터로 진행
        filterChain.doFilter(request, response);
    }
}
