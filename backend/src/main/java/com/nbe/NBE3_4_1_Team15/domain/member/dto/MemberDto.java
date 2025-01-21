package com.nbe.NBE3_4_1_Team15.domain.member.dto;

import com.nbe.NBE3_4_1_Team15.domain.member.entity.Member;

import java.time.LocalDateTime;

public record MemberDto(Long id, LocalDateTime createDate, LocalDateTime modifyDate, String nickname) {

    public MemberDto(Member member) {
        this(member.getId(), member.getCreateDate(), member.getModifyDate(), member.getNickname());
    }
}
