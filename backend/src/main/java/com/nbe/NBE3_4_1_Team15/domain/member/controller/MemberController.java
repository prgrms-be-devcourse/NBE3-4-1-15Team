package com.nbe.NBE3_4_1_Team15.domain.member.controller;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.nbe.NBE3_4_1_Team15.domain.member.dto.MemberDto;
import com.nbe.NBE3_4_1_Team15.domain.member.entity.Member;
import com.nbe.NBE3_4_1_Team15.domain.member.service.AuthService;
import com.nbe.NBE3_4_1_Team15.domain.member.service.MemberService;
import com.nbe.NBE3_4_1_Team15.global.exceptions.ServiceException;
import com.nbe.NBE3_4_1_Team15.global.rq.Rq;
import com.nbe.NBE3_4_1_Team15.global.rsData.RsData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final AuthService authService;
    private final Rq rq;

    record MemberJoinReqBody(
            @NotBlank String email,
            @NotBlank String password,
            @NotBlank String nickname
    ){}

    @PostMapping("/join")
    @Transactional
    public RsData<MemberDto> join(@RequestBody @Valid MemberJoinReqBody reqBody) {
        Member member = memberService.join(reqBody.email, reqBody.password, reqBody.nickname);

        return new RsData<>(
                "201-1",
                "%s님 환영합니다. 회원가입이 완료되었습니다.".formatted(member.getNickname()),
                new MemberDto(member)
        );
    }

    record MemberLoginReqBody(
            @NotBlank
            String email,
            @NotBlank
            String password
    ) {
    }

    @JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
    record MemberLoginResBody(
            MemberDto item,
            String accessToken,
            String refreshToken
    ) {
    }

    @PostMapping("/login")
    @Transactional(readOnly = true)
    public RsData<MemberLoginResBody> login(@RequestBody @Valid MemberLoginReqBody reqBody) {
        Member member = memberService
                .findByEmail(reqBody.email)
                .orElseThrow(() -> new ServiceException("401-1", "존재하지 않는 사용자입니다."));

        if (!authService.matches(reqBody.password, member.getPassword())) {
            throw new ServiceException("401-2", "비밀번호가 일치하지 않습니다.");
        }

        String accessToken = memberService.genAccessToken(member);

        rq.setCookie("access-token", accessToken);
        rq.setCookie("refresh-token", member.getRefreshToken());

        return new RsData<>(
                "200-1",
                "%s님 환영합니다.".formatted(member.getNickname()),
                new MemberLoginResBody(
                        new MemberDto(member),
                        accessToken,
                        member.getRefreshToken()
                )
        );
    }

    @DeleteMapping("/logout")
    @Transactional(readOnly = true)
    public RsData<Void> logout() {
        rq.deleteCookie("access-token");
        rq.deleteCookie("refresh-token");

        return new RsData<>(
                "200-1",
                "로그아웃 되었습니다."
        );
    }

    @GetMapping("/me")
    @Transactional(readOnly = true)
    public MemberDto me() {
        Member actor = rq.findByActor().get();

        return new MemberDto(actor);
    }

}
