package com.nbe.NBE3_4_1_Team15.domain.member.service;

import com.nbe.NBE3_4_1_Team15.domain.member.entity.Member;
import com.nbe.NBE3_4_1_Team15.standard.util.Ut;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthTokenService {
//    @Value("${custom.jwt.secretKey}")
    private String jwtSecretKey;

//    @Value("${custom.accessToken.expirationSeconds}")
    private long accessTokenExpirationSeconds;

    public String genAccessToken(Member member) {
        long id = member.getId();
        String email = member.getEmail();

        return Ut.jwt.toString(
                jwtSecretKey,
                accessTokenExpirationSeconds,
                Map.of("id", id, "email", email) // JWT는 id와 email로 생성
        );
    }

    // payload 중에서도 id, email만 추출 (Actor 생성을 위해)
    Map<String, Object> payload(String accessToken) {
        Map<String, Object> parsedPayload = Ut.jwt.payload(jwtSecretKey, accessToken);

        if (parsedPayload == null) return null;

        long id = ((Number) parsedPayload.get("id")).longValue();
        String email = (String) parsedPayload.get("email");

        return Map.of("id", id, "email", email);
    }
}

