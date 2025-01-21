package com.nbe.NBE3_4_1_Team15.global.initData;

import com.nbe.NBE3_4_1_Team15.domain.member.entity.Member;
import com.nbe.NBE3_4_1_Team15.domain.member.service.MemberService;
import com.nbe.NBE3_4_1_Team15.domain.member.type.MemberType;
import com.nbe.NBE3_4_1_Team15.domain.product.service.ProductService;
import com.nbe.NBE3_4_1_Team15.domain.product.type.ProductType;
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
    private final ProductService productService; // ★ 주입받기

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

        // === 1) Member 테스트 데이터 생성 ===
        memberService.join("test1@naver.com","11","서울",  "test1", MemberType.ADMIN);
        memberService.join("test2@naver.com","11","경기도","test2", MemberType.MEMBER);
        memberService.join("test3@naver.com","11","부산",   "test3", MemberType.ADMIN);

        // 예시) seller로 활용할 Member 찾기 (test1@naver.com)
        Member seller = memberService.findByEmail("test1@naver.com").orElseThrow();

        // === 2) Product 테스트 데이터 생성 ===
        // 상품 1
        productService.create(
                seller,
                "Test Product 1",
                1000,
                "This is a test product 1 description",
                ProductType.CAFFEINE,
                50
        );
        // 상품 2
        productService.create(
                seller,
                "Test Product 2",
                2000,
                "This is a test product 2 description",
                ProductType.DE_CAFFEINE,
                30
        );
        // 상품 3
        productService.create(
                seller,
                "Test Product 3",
                1500,
                "Description for product 3",
                ProductType.CAFFEINE,
                100
        );
    }
}
