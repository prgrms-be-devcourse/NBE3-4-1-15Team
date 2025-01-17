package com.nbe.NBE3_4_1_Team15.domain.member.entity;

import org.springframework.data.jpa.repository.JpaRepository;

//테스트 memberRepository 생성
public interface MemRepository extends JpaRepository<Member, Long> {
}
