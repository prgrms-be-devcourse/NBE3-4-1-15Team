package com.nbe.NBE3_4_1_Team15.domain.cart.service;

import com.nbe.NBE3_4_1_Team15.domain.cart.entity.Cart;
import com.nbe.NBE3_4_1_Team15.domain.cart.repository.CartRepository;
import com.nbe.NBE3_4_1_Team15.domain.cartProduct.entity.CartProduct;
import com.nbe.NBE3_4_1_Team15.domain.cartProduct.repository.CartProductRepository;
import com.nbe.NBE3_4_1_Team15.domain.member.entity.Member;
import com.nbe.NBE3_4_1_Team15.domain.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;



    // 1. 장바구니 상품 추가
//    public Cart addProductToCart(Cart cart, Product product, int quantity){
//        CartProduct cartProduct = new CartProduct(cart, product, quantity);
//        cart.getCartProducts().add(cartProduct);
//        cartProductRepository.save(cartProduct);
//        return cartRepository.save(cart);
//    }
    public CartProduct addProduct(Cart cart, Product product, int quantity) {
        CartProduct cartProduct = new CartProduct(cart, product, quantity);
        cart.getCartProducts().add(cartProduct);
        return cartProductRepository.save(cartProduct);
    }

    // 2. 장바구니 조회
//    public Cart getCart(Long cartId) throws IllegalAccessException {
//        return cartRepository.findById(cartId)
//                .orElseThrow(() -> new IllegalAccessException("장바구니를 찾을 수 없습니다."));
//    }
    public List<CartProduct> getCartProduct(Cart cart) throws IllegalAccessException {
        return cart.getCartProducts();
    }

    // 3. 장바구니 수정
//    public CartProduct updateCartProduct(Long cartProductId, int newQuantity) throws IllegalAccessException {
//        CartProduct cartProduct = cartProductRepository.findById(cartProductId)
//                .orElseThrow(() -> new IllegalAccessException("장바구니 상품이 존재하지 않습니다."));
//        cartProduct.setQuantity(newQuantity);
//        return cartProductRepository.save(cartProduct);
//    }
    public Optional<CartProduct> updateCartProduct(Cart cart, Product product, int newQuantity){

        for (CartProduct cartProduct : cart.getCartProducts()) {
            if (cartProduct.getProduct().equals(product)) {
                cartProduct.setQuantity(newQuantity);
                return Optional.of(cartProduct);
            }
        }
        return Optional.empty();
    }

    // 4. 장바구니 상품 삭제
//    public void deleteCartProduct(Long cartProductId){
//        cartProductRepository.deleteById(cartProductId);
//    }
    public void deleteCartProduct(Cart cart, Product product) {
        cart.getCartProducts().removeIf(cartProduct -> cartProduct.getProduct().equals(product));
    }

    // CartService.java 중간에 메서드 추가
    public Cart findOrCreateCart(Member member) {
        if (member.getCart() != null) {
            // 이미 Cart가 연결돼 있다면 그대로 반환
            return member.getCart();
        }
        // 장바구니가 없으면 새로 생성
        Cart newCart = Cart.builder().member(member).build();
        member.setCart(newCart); // Member <-> Cart 서로 연결
        return cartRepository.save(newCart);
    }

}