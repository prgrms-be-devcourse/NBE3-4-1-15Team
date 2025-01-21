package com.nbe.NBE3_4_1_Team15.domain.cart.service;

import com.nbe.NBE3_4_1_Team15.domain.cart.entity.Cart;
import com.nbe.NBE3_4_1_Team15.domain.cart.repository.CartRepository;
import com.nbe.NBE3_4_1_Team15.domain.cartProduct.entity.CartProduct;
import com.nbe.NBE3_4_1_Team15.domain.cartProduct.repository.CartProductRepository;
import com.nbe.NBE3_4_1_Team15.domain.member.entity.Member;
import com.nbe.NBE3_4_1_Team15.domain.product.entity.Product;
import com.nbe.NBE3_4_1_Team15.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;
    private final ProductRepository productRepository; // 재고 갱신을 위해 필요

    /**
     * 특정 Member에게 연결된 Cart를 찾거나 없으면 생성
     */
    public Cart findOrCreateCart(Member member) {
        Optional<Cart> opCart = cartRepository.findByMemberId(member.getId());
        if (opCart.isPresent()) {
            return opCart.get();
        }

        // Cart가 없으면 새로 생성
        Cart newCart = new Cart();
        newCart.setMember(member);
        newCart.setCartProducts(new ArrayList<>());
        return cartRepository.save(newCart);
    }

    /**
     * 장바구니에 상품 추가 (재고 감소)
     */
    public CartProduct addProduct(Cart cart, Product product, int quantity) {
        // 재고 확인
        if (product.getStock() < quantity) {
            throw new IllegalStateException("재고가 부족합니다.");
        }

        // 기존에 cartProduct가 있는지 확인
        CartProduct existingCartProduct = null;
        for (CartProduct cp : cart.getCartProducts()) {
            if (cp.getProduct().getId().equals(product.getId())) {
                existingCartProduct = cp;
                break;
            }
        }

        if (existingCartProduct == null) {
            // 새로 장바구니에 추가
            CartProduct cartProduct = new CartProduct(cart, product, quantity);
            cart.getCartProducts().add(cartProduct);
            cartProductRepository.save(cartProduct);
            // 재고 감소
            product.setStock(product.getStock() - quantity);
            productRepository.save(product);
            return cartProduct;
        } else {
            // 이미 존재하면 수량 추가
            existingCartProduct.setQuantity(existingCartProduct.getQuantity() + quantity);
            cartProductRepository.save(existingCartProduct);
            // 재고 감소
            product.setStock(product.getStock() - quantity);
            productRepository.save(product);
            return existingCartProduct;
        }
    }

    /**
     * 장바구니에서 상품을 1개 제거 (재고 복구)
     */
    public CartProduct removeOneProduct(Cart cart, Product product) {
        CartProduct target = null;
        for (CartProduct cp : cart.getCartProducts()) {
            if (cp.getProduct().equals(product)) {
                target = cp;
                break;
            }
        }
        if (target == null) {
            return null; // 해당 상품이 장바구니에 없음
        }

        int currentQty = target.getQuantity();
        if (currentQty <= 1) {
            // 수량이 1이라면 CartProduct 삭제
            cart.getCartProducts().remove(target);
            cartProductRepository.delete(target);

            // 재고 복구
            product.setStock(product.getStock() + 1);
            productRepository.save(product);

            return null; // 이미 제거된 상태
        } else {
            // 수량 1 감소
            target.setQuantity(currentQty - 1);
            cartProductRepository.save(target);

            // 재고 복구
            product.setStock(product.getStock() + 1);
            productRepository.save(product);

            return target;
        }
    }

    /**
     * 장바구니 상품 수정(수량 변경)
     */
    public Optional<CartProduct> updateCartProduct(Cart cart, Product product, int newQuantity) {
        for (CartProduct cp : cart.getCartProducts()) {
            if (cp.getProduct().equals(product)) {
                cp.setQuantity(newQuantity);
                return Optional.of(cartProductRepository.save(cp));
            }
        }
        return Optional.empty();
    }

    /**
     * 장바구니 상품 삭제 (사용 안 할 수도 있음)
     */
    public void deleteCartProduct(Cart cart, Product product) {
        cart.getCartProducts().removeIf(cp -> cp.getProduct().equals(product));
        // if needed, cartProductRepository.delete(...) 호출
    }
}
