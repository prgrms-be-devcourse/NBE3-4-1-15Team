package com.nbe.NBE3_4_1_Team15.domain.product.controller;

import com.nbe.NBE3_4_1_Team15.domain.member.entity.Member;
import com.nbe.NBE3_4_1_Team15.domain.member.service.MemberService;
import com.nbe.NBE3_4_1_Team15.domain.product.dto.ProductDto;
import com.nbe.NBE3_4_1_Team15.domain.product.entity.Product;
import com.nbe.NBE3_4_1_Team15.domain.product.service.ProductService;
import com.nbe.NBE3_4_1_Team15.domain.product.type.ProductType;
import com.nbe.NBE3_4_1_Team15.global.exceptions.ServiceException;
import com.nbe.NBE3_4_1_Team15.global.rsData.RsData;
import com.nbe.NBE3_4_1_Team15.global.security.SecurityUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;
    private final MemberService memberService;

    record ProductCreateReqBody(
            @NotBlank String name,         // 상품명
            @NotNull Integer price,        // 가격
            @NotBlank String description,  // 설명
            @NotNull ProductType productType,  // enum
            @NotNull Integer stock         // 재고
    ) {
        void updateProduct(Product product) {
            product.setName(name);
            product.setPrice(price);
            product.setDescription(description);
            product.setProductType(productType);
            product.setStock(stock);
        }
    }

    // 상품 생성
    @PostMapping
    @Transactional
    public RsData<Void> create(
            @Valid @RequestBody ProductCreateReqBody reqBody,
            Authentication authentication,
            Model model,
            Principal principal
    ) {
        // 로그인 사용자 추출
        SecurityUser loggedInUser = (SecurityUser) authentication.getPrincipal();
        Member seller = memberService.getMemberFromSecurityUser(loggedInUser);

        // Product 생성
        productService.create(
                seller,
                reqBody.name(),
                reqBody.price(),
                reqBody.description(),
                reqBody.productType(),
                reqBody.stock()
        );

        return new RsData<>("201-2", "제품이 등록되었습니다.");
    }

    // 전체 상품 조회
    @GetMapping
    public RsData<List<ProductDto>> getProducts() {
        List<Product> products = productService.getProducts();
        return new RsData<>(
                "200-1",
                "전체 상품 조회가 완료되었습니다.",
                products.stream()
                        .map(ProductDto::new)
                        .toList()
        );
    }


    // 단일 상품 조회
    @GetMapping("/{id}")
    public RsData<ProductDto> getProduct(@PathVariable Long id) {
        Optional<Product> opProduct = productService.findById(id);
        Product product = opProduct.orElseThrow(
                () -> new ServiceException(
                        "404-2",
                        "Product(%d) not found".formatted(id)
                )
        );
        return new RsData<>(
                "200-2",
                "상품 조회가 완료되었습니다.",
                new ProductDto(product)
        );
    }

    // 상품 수정
    @PutMapping("/{id}")
    @Transactional
    public RsData<Void> update(
            @RequestBody @Valid ProductCreateReqBody reqBody,
            @PathVariable Long id,
            Authentication authentication
    ) {
        // 상품 존재 여부
        Product product = productService.findById(id)
                .orElseThrow(() -> new ServiceException("404-1", "상품을 찾을 수 없습니다."));

        // 권한 체크 (본인 상품인지)
        SecurityUser loggedInUser = (SecurityUser) authentication;
        if (!product.getSeller().getId().equals(loggedInUser.getId())) {
            throw new ServiceException("403-1", "수정 권한이 없습니다!");
        }

        // 수정 로직
        reqBody.updateProduct(product);

        return new RsData<>("200-3", "상품 수정이 완료되었습니다.");
    }

    // 상품 삭제
    @DeleteMapping("/{id}")
    @Transactional
    public RsData<Void> delete(@PathVariable Long id, Authentication authentication) {
        // 상품 조회
        Product product = productService.findById(id)
                .orElseThrow(() -> new ServiceException("404-1", "상품을 찾을 수 없습니다."));

        // 권한 체크 (본인 상품인지)
        SecurityUser loggedInUser = (SecurityUser) authentication.getPrincipal();
        if (!product.getSeller().getId().equals(loggedInUser.getId())) {
            throw new ServiceException("403-1", "삭제 권한이 없습니다!");
        }

        // 삭제
        productService.deleteProduct(id);

        return new RsData<>("200-4", "상품 삭제가 완료되었습니다.");
    }
}
