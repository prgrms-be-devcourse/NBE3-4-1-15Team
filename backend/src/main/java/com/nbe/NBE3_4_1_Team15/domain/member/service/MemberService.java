package com.nbe.NBE3_4_1_Team15.domain.member.service;

import com.nbe.NBE3_4_1_Team15.domain.member.entity.Member;
import com.nbe.NBE3_4_1_Team15.domain.member.repository.MemberRepository;
import com.nbe.NBE3_4_1_Team15.global.exceptions.ServiceException;
import com.nbe.NBE3_4_1_Team15.global.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final AuthService authService;

    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }


    public Member join(String email, String password, String nickname) {
        memberRepository
                .findByEmail(email)
                .ifPresent(_ -> {
                    throw new ServiceException("409-1", "해당 email은 이미 사용중입니다.");
                });
        String encryptedPassword = authService.encode(password);

        Member member = Member.builder()
                .email(email)
                .password(encryptedPassword)
                .nickname(nickname)
                .refreshToken(UUID.randomUUID().toString())
                .build();

        return memberRepository.save(member);
    }

    public String genAccessToken(Member member) {
        return authService.genAccessToken(member);
    }

    public Optional<Member> findByRefreshToken(String refreshToken) {
        return memberRepository.findByRefreshToken(refreshToken);
    }

    public Member getMemberFromAccessToken(String accessToken) {
        System.out.println("Access Token 검증 중: " + accessToken);
        Map<String, Object> payload = authService.payload(accessToken);

        if (payload == null) {
            return null;
        }

        long id = (long) payload.get("id");
        String email = (String) payload.get("email");

        return new Member(id, email);
    }

    public Member getMemberFromSecurityUser(SecurityUser securityUser) {
        return new Member(securityUser.getId(), securityUser.getUsername());
    }

    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public String genAuthToken(Member member) {
        return member.getRefreshToken() + " " + genAccessToken(member);
    }
}
