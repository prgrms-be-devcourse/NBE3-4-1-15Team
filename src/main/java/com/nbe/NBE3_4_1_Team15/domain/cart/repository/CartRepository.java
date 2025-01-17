package com.nbe.NBE3_4_1_Team15.domain.cart.repository;

import com.nbe.NBE3_4_1_Team15.domain.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
