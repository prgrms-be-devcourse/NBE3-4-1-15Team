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
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final ProductService productService;
    private final MemberService memberService;
    private final Rq rq;

    // "장바구니에 상품 담기" 요청 바디용 DTO
    @Data
    static class AddCartRequest {
        @NotNull
        private Long productId; // 상품 ID
        private int quantity;   // 수량
    }

    // 장바구니에 상품 추가
    @PostMapping("/add")
    public RsData<CartProductDto> addProduct(@RequestBody AddCartRequest request) {
        // 1) 현재 로그인한 사용자
        Long memberId = rq.getMemberId();
        Member member = memberService.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));

        // 2) 상품 조회
        Product product = productService.findById(request.productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다."));

        // 3) cart 찾거나 없으면 생성
        Cart cart = cartService.findOrCreateCart(member);

        // 4) 실제 장바구니에 상품 추가
        CartProduct cartProduct = cartService.addProduct(cart, product, request.quantity);

        // 5) DTO 변환
        CartProductDto dto = new CartProductDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                cartProduct.getQuantity(),
                cartProduct.getTotalPrice()
        );

        return new RsData<>(
                "200-1",
                "장바구니에 상품이 추가되었습니다.",
                dto
        );
    }

    // 장바구니 목록 조회 API (선택)
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
