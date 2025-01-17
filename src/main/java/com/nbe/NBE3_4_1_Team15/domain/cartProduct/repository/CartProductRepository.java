package com.nbe.NBE3_4_1_Team15.domain.cartProduct.repository;

import com.nbe.NBE3_4_1_Team15.domain.cart.entity.Cart;
import com.nbe.NBE3_4_1_Team15.domain.cartProduct.entity.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;
// CartProductRepository.java git 업로드용 주석 추가
public interface CartProductRepository extends JpaRepository<CartProduct, Long> {
}
