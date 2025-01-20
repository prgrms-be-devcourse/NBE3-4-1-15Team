package com.nbe.NBE3_4_1_Team15.domain.cart.repository;

import com.nbe.NBE3_4_1_Team15.domain.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// CartRepository.java git 업로드용 주석 추가
public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByMemberId(Long memberId);
}