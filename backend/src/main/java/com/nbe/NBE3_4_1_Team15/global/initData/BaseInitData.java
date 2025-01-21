package com.nbe.NBE3_4_1_Team15.global.initData;

import com.nbe.NBE3_4_1_Team15.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class BaseInitData {
    private final MemberService memberService;
    @Autowired
    @Lazy
    private BaseInitData self;

    @Bean
    public ApplicationRunner baseInitDataApplicationRunner() {
        return args -> {
            self.work1();
        };
    }

    @Transactional
    public void work1() {
        System.out.println("-----------------------Init Data--------------------------");

        memberService.join("test1@naver.com","pw","test1");
        memberService.join("test2@naver.com","pw","test1");
        memberService.join("test3@naver.com","pw","test1");

    }
}
