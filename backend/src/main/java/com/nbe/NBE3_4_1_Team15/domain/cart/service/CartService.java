package com.nbe.NBE3_4_1_Team15.domain.cart.service;

import com.nbe.NBE3_4_1_Team15.domain.cartProduct.dto.CartProductDto;
import com.nbe.NBE3_4_1_Team15.domain.cartProduct.entity.CartProduct;
import com.nbe.NBE3_4_1_Team15.domain.cartProduct.repository.CartProductRepository;
import com.nbe.NBE3_4_1_Team15.domain.member.entity.Member;
import com.nbe.NBE3_4_1_Team15.domain.member.repository.MemberRepository;
import com.nbe.NBE3_4_1_Team15.domain.product.entity.Product;
import com.nbe.NBE3_4_1_Team15.domain.cart.entity.Cart;
import com.nbe.NBE3_4_1_Team15.domain.cart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CartService {

    private CartRepository cartRepository;
    private CartProductRepository cartProductRepository;
    private CartProduct cartProduct;
    private MemberRepository memberRepository;

    // 1. 장바구니 상품 추가
    @Transactional
    public void addProductToCart(Long memberId, Product product) {
        // member와 연결된 cart 찾기(없으면 새로 생성)
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseGet(()-> createNewCart(memberId)); // 장바구니가 없으면 생성
        // 해당 cart의 cartproducts에 동일한 상품 있는지 확인
        CartProduct existingCartProduct =
                cartProductRepository.findByCartIdAndProductId(cart.getId(), product.getId());
        // 동일한 상품 있으면 수량 증가
        if(existingCartProduct != null){
            existingCartProduct.setQuantity(existingCartProduct.getQuantity() + 1);
            cartProductRepository.save(existingCartProduct);
        } // 동일한 상품 없으면 CartProduct 생성해서 추가
        else {
            CartProduct newCartProduct = CartProduct.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(1) // 처음 넣는 상품이라 1
                    .build();
            cartProductRepository.save(cartProduct); // 새 상품 저장
        }
    }

    // 1-2. 장바구니 id가 없을 경우 -> 새로운 장바구니 생성
    private Cart createNewCart(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));

        Cart cart = Cart.builder()
                .member(member)
                .build();

        return cartRepository.save(cart);
    }

    // 2. 장바구니 조회
    @Transactional(readOnly = true)
    public List<CartProductDto> getCartProduct(Long cartId) {
        // CartRepository를 사용해 cartId에 해당 장바구니 가져오기
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new RuntimeException("장바구니가 없습니다."));
        // 2. 장바구니 상품 목록 가져오기
        List<CartProduct> cartProducts = cart.getCartProducts();
        // 3. DTO로 변환
        return cartProducts.stream()
                .map(cartProduct -> new CartProductDto(
                        cartProduct.getProduct().getId(),
                        cartProduct.getProduct().getName(),
                        cartProduct.getProduct().getPrice(),
                        cartProduct.getQuantity(),
                        cartProduct.getProduct().getPrice()*cartProduct.getQuantity())).collect(Collectors.toList());
    }

    // 3. 장바구니 수정
    @Transactional
    public void updateCartProduct(Long id, Integer quantity) {
        // CartProductRepository를 사용해 id에 해당하는 CartProduct 찾기
        // 존재하지 않으면 예외 던지기
        CartProduct cartProduct = cartProductRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(id + "은(는) 존재하지 않는 상품입니다."));
        // * 유효성 검사(상품 수량이 0 이하가 되면 안됨)
        if(quantity<=0){
            throw new IllegalArgumentException("수량은 0개 이하일 수 없습니다");
        }
        // 상품의 수량이나 속성 업데이트
        cartProduct.setQuantity(quantity);
        // 업데이트 내용 저장
        cartProductRepository.save(cartProduct);
    }

    // 4. 장바구니 상품 삭제
    @Transactional
    public void deleteCartProduct(Long id) {
        // CartProductRepository 사용하여 id에 해당하는 CartProduct 찾기
        // 존재하지 않으면 예외 던지기
        CartProduct cartProduct = cartProductRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(id + "은(는) 존재하지 않는 상품입니다."));
        // 해당 CartProduct 삭제
        cartProductRepository.delete(cartProduct);
        // 장바구니가 비었을 때 처리 로직(아마도 장바구니에 상품이 없습니다 띄워야 할 듯?)
        Cart cart = cartProduct.getCart();
        if (cart.getCartProducts().isEmpty()) {
            System.out.println("장바구니가 비어 있습니다.");
        }
    }
}
