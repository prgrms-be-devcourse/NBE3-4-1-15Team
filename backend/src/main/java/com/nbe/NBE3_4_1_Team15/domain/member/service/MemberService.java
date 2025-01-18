package com.nbe.NBE3_4_1_Team15.domain.member.service;

import com.nbe.NBE3_4_1_Team15.domain.member.entity.Member;
import com.nbe.NBE3_4_1_Team15.domain.member.repository.MemberRepository;
import com.nbe.NBE3_4_1_Team15.global.exceptions.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final AuthTokenService authTokenService;

    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }


    public Member join(String email, String password, String nickname) {
        memberRepository
                .findByEmail(email)
                .ifPresent(_ -> {
                    throw new ServiceException("409-1", "해당 email은 이미 사용중입니다.");
                });

        Member member = Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .refreshToken(UUID.randomUUID().toString())
                .build();

        return memberRepository.save(member);
    }

    public String genAccessToken(Member member) {
        return authTokenService.genAccessToken(member);
    }
}
