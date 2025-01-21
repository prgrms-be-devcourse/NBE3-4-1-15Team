package com.nbe.NBE3_4_1_Team15.domain.member.repository;

import com.nbe.NBE3_4_1_Team15.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//테스트 memberRepository 생성
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String username);

    Optional<Member> findByRefreshToken(String refreshToken);
}
