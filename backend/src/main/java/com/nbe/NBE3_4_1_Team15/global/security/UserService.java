package com.nbe.NBE3_4_1_Team15.global.security;

import com.nbe.NBE3_4_1_Team15.domain.member.entity.Member;
import com.nbe.NBE3_4_1_Team15.global.security.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

//    @Autowired
//    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
//        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;


    // 이메일 중복 확인
    public Member registerUser(String email, String rawPassword) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("이미 사용 중인 이메일입니다."));

        // 비밀번호 암호화 저장
        String encryptedPassword = passwordEncoder.encode(rawPassword);

        // DB에 회원 저장
        Member member = Member.builder()
                .email(email)
                .password(encryptedPassword)
                .build();

        return userRepository.save(member);
    }

    // 로그인 (이 객체를 UserConto~로 받음)
    public Member login(String email, String rawPassword) {

        Member member = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일로 가입된 사용자가 없습니다."));

        if (!passwordEncoder.matches(rawPassword, member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return member;
    }
}
