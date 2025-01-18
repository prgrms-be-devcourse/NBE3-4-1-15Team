package com.nbe.NBE3_4_1_Team15.standard.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.SneakyThrows;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

public class Ut { // 다른 프로젝트에도 사용 가능한 이식성이 높은 유틸 클래스 모음
    public static class str { // 문자열 처리 유틸 클래스
        public static boolean isBlank(String str) {
            return str == null || str.trim().isEmpty();
        }
    }

    public static class json { // json 처리 유틸 클래스
        private static final ObjectMapper om = new ObjectMapper();

        @SneakyThrows
        public static String toJson(Object obj) {
            return om.writeValueAsString(obj);
        }
    }

    public static class jwt { // jwt 토큰 생성 클래스
        /**
         * jwt string 생성 메서드
         * */
        public static String toString(String secret, long expireSeconds, Map<String, Object> body) {
            Date issuedAt = new Date(); // 토큰 발행 시간
            Date expiration = new Date(issuedAt.getTime() + expireSeconds * 1000L); // 토큰 만료 시간

            SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());

            String jwt = Jwts.builder()
                    .claims(body)
                    .issuedAt(issuedAt)
                    .expiration(expiration)
                    .signWith(secretKey)
                    .compact();

            return jwt;
        }

        /**
         * jwt string 검증 메서드
         * */
        public static  boolean isValid(String secret, String jwtStr) { // jwt 토큰 검증 메서드
            SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes()); // secret 문자열을 암호키 형식으로 전환(hmac)

            try {
                Jwts
                        .parser()
                        .verifyWith(secretKey)
                        .build()
                        .parse(jwtStr);
            } catch (Exception e) {
                return false;
            }

            return true;
        }

        /**
         * jwt 페이로드 반환 메서드
         * json -> Map
         * getPayload()가 capture를 반환해서 나오는 경고는 무시해도 된다.
         * */
        public static Map<String, Object> payload(String secret, String jwtStr) {
            SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());

            try {
                return (Map<String, Object>) Jwts
                        .parser()
                        .verifyWith(secretKey)
                        .build()
                        .parse(jwtStr)
                        .getPayload();
            } catch (Exception e) {
                return null;
            }
        }
    }
}
