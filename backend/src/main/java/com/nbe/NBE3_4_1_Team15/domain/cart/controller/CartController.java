package com.nbe.NBE3_4_1_Team15.domain.cart.controller;

import com.nbe.NBE3_4_1_Team15.domain.cart.entity.Cart;
import com.nbe.NBE3_4_1_Team15.domain.cart.service.CartService;
import com.nbe.NBE3_4_1_Team15.domain.cartProduct.dto.CartProductDto;
import com.nbe.NBE3_4_1_Team15.domain.cartProduct.entity.CartProduct;
import com.nbe.NBE3_4_1_Team15.domain.member.entity.Member;
import com.nbe.NBE3_4_1_Team15.domain.member.service.MemberService;
import com.nbe.NBE3_4_1_Team15.domain.product.entity.Product;
import com.nbe.NBE3_4_1_Team15.domain.product.service.ProductService;
import com.nbe.NBE3_4_1_Team15.global.rq.Rq;
import com.nbe.NBE3_4_1_Team15.global.rsData.RsData;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final ProductService productService;
    private final MemberService memberService;
    private final Rq rq;

    @Data
    static class AddCartRequest {
        @NotNull
        private Long productId; // 상품 ID
        private int quantity;   // 수량
    }

    @Data
    static class RemoveOneRequest {
        @NotNull
        private Long productId; // 상품 ID
    }

    // 장바구니에 상품 추가 (stock 감소 처리)
    @PostMapping("/add")
    public RsData<CartProductDto> addProduct(@RequestBody AddCartRequest request) {
        Long memberId = rq.getMemberId();
        Member member = memberService.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));

        Product product = productService.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다."));

        Cart cart = cartService.findOrCreateCart(member);

        // 장바구니에 상품 추가 + stock 감소
        CartProduct cartProduct = cartService.addProduct(cart, product, request.getQuantity());

        // 응답 DTO
        CartProductDto dto = new CartProductDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                cartProduct.getQuantity(),
                cartProduct.getTotalPrice()
        );
        return new RsData<>("200-1", "장바구니에 상품이 추가되었습니다.", dto);
    }

    // 장바구니에서 상품 한 개 제거 (stock 복구)
    @PatchMapping("/removeOne")
    public RsData<CartProductDto> removeOne(@RequestBody RemoveOneRequest request) {
        Long memberId = rq.getMemberId();
        Member member = memberService.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));
        Cart cart = cartService.findOrCreateCart(member);

        Product product = productService.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다."));

        CartProduct cartProduct = cartService.removeOneProduct(cart, product);

        if (cartProduct == null) {
            // 이미 삭제되어 null이면, 해당 cartProductDto는 더 이상 없음
            return new RsData<>("200-2", "장바구니에서 상품이 제거되었습니다.", null);
        }

        // 응답 DTO
        CartProductDto dto = new CartProductDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                cartProduct.getQuantity(),
                cartProduct.getTotalPrice()
        );
        return new RsData<>("200-3", "장바구니 상품 한 개 감소 처리 완료", dto);
    }

    // 장바구니 목록 조회
    @GetMapping("")
    public RsData<List<CartProductDto>> getCartItems() {
        Long memberId = rq.getMemberId();
        Member member = memberService.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));
        Cart cart = cartService.findOrCreateCart(member);

        List<CartProduct> cartProducts = cart.getCartProducts();
        List<CartProductDto> dtos = new ArrayList<>();
        for (CartProduct cp : cartProducts) {
            dtos.add(new CartProductDto(
                    cp.getProduct().getId(),
                    cp.getProduct().getName(),
                    cp.getProduct().getPrice(),
                    cp.getQuantity(),
                    cp.getTotalPrice()
            ));
        }
        return new RsData<>("200-2", "장바구니 조회 성공", dtos);
    }
}
