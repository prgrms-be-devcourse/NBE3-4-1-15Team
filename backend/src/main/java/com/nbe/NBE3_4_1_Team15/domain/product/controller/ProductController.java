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
//    private final ProductRepository productRepository;

//    @GetMapping("/new") // 상품 생성 폼을 보여주는 GET 요청
//    public String createForm(Model model) {
//        model.addAttribute("productCreateDto", new ProductCreateDto());
//        return "products/createForm"; // View 이름
//    }

    record ProductCreateReqBody(
            @NotBlank String name,
            @NotBlank int price,
            @NotBlank String description,
            @NotBlank ProductType productType,
            @NotBlank int stock
    ) {
        void updateProduct(Product product) {
            product.setName(name);
            product.setPrice(price);
            product.setDescription(description);
            product.setProductType(productType);
            product.setStock(stock);
        }
    }

    // 권한은 회원 에게만
    @PostMapping
    @Transactional
    public RsData<Void> create(@Valid @RequestBody ProductCreateReqBody reqBody, Authentication authentication, Model model, Principal principal) {
        SecurityUser loggedInUser = (SecurityUser) authentication;
        Member seller = memberService.getMemberFromSecurityUser(loggedInUser);

        Product product = productService.create(
                seller,
                reqBody.name,
                reqBody.price,
                reqBody.description,
                reqBody.productType,
                reqBody.stock
        );

        return new RsData<>(
                "201-2",
                "제품이 등록되었습니다."
        );
    }

//    public String create(@ModelAttribute ProductCreateDto createDto, RedirectAttributes redirectAttributes) {
//        try {
//            ProductResponseDto responseDto = productService.create(createDto);
//            redirectAttributes.addFlashAttribute("message", "상품이 성공적으로 생성되었습니다.");
//            return "redirect:/products/" + responseDto.getId(); // 생성된 상품 상세 페이지로 리다이렉트
//        } catch (RuntimeException e) {
//            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
//            return "redirect:/products/new"; // 생성 폼으로 다시 리다이렉트
//        }
//    }

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
//    @GetMapping
//    public String getProducts(Model model) {
//        List<ProductResponseDto> productList = productService.getProductList();
//        model.addAttribute("productList", productList);
//        return "products/list"; // View 이름
//    }



    @GetMapping("/{id}")
    public RsData<ProductDto> getProduct(@PathVariable Long id) {
        Optional<Product> opProduct = productService.findById(id);
        Product product = opProduct.orElseThrow(
                () -> new ServiceException(
                        "404-2",
                        "Product(%d) not found".formatted(id)
                )
        );
        return new RsData<> (
                "200-2",
                "상품 조회가 완료되었습니다.",
                new ProductDto(product)
        );
    }
//    @GetMapping("/{Id}")
//    public String getProduct(@PathVariable Long Id, Model model) {
//        try {
//            ProductResponseDto product = productService.getProduct(Id);
//            model.addAttribute("product", product);
//            return "products/detail"; // View 이름
//        } catch (RuntimeException e) {
//            model.addAttribute("errorMessage", e.getMessage());
//            return "error"; // 에러 페이지
//        }
//    }

    @PutMapping("/{id}")
    @Transactional
    public RsData<Void> update(@RequestBody @Valid ProductCreateReqBody reqBody,@PathVariable Long id, Authentication authentication) {
        Product product = productService.findById(id).get();
        SecurityUser loggedInUser = (SecurityUser) authentication;
        if(!product.getSeller().getId().equals(loggedInUser.getId())) {
            throw new ServiceException(
                    "403-1",
                    "수정 권한이 없습니다!"
            );
        }

        reqBody.updateProduct(product);

        return new RsData<> (
                "200-3",
                "상품 수정이 완되었습니다."
                );
    }

//    @GetMapping("/{Id}/edit") // 상품 수정 폼을 보여주는 GET 요청
//    public String updateForm(@PathVariable Long Id, Model model) {
//        try {
//            ProductResponseDto product = productService.getProduct(Id);
//            ProductUpdateDto updateDto = new ProductUpdateDto(product.getPrice(), product.getDescription(), product.getStock());
//            model.addAttribute("updateDto", updateDto);
//            model.addAttribute("Id", Id);
//            return "products/updateForm"; // View 이름
//        } catch (RuntimeException e) {
//            model.addAttribute("errorMessage", e.getMessage());
//            return "error"; // 에러 페이지
//        }
//    }

//    @PostMapping("/{Id}")
//    public String updateProduct(@PathVariable Long Id, @ModelAttribute ProductUpdateDto updateDto, RedirectAttributes redirectAttributes) {
//        try {
//            productService.updateProduct(Id, updateDto);
//            redirectAttributes.addFlashAttribute("message", "상품이 수정되었습니다.");
//            return "redirect:/products/" + Id; // 수정된 상품 상세 페이지로 리다이렉트
//        } catch (RuntimeException e) {
//            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
//            return "redirect:/products/" + Id + "/edit"; // 수정 폼으로 다시 리다이렉트
//        }
//    }

    @DeleteMapping("/{id}")
    public RsData<Void> delete(@PathVariable Long id, Authentication authentication) {
        Product product = productService.findById(id).get();
        SecurityUser loggedInUser = (SecurityUser) authentication;
        if(!product.getSeller().getId().equals(loggedInUser.getId())) {
            throw new ServiceException(
                    "403-1",
                    "삭제 권한이 없습니다!"
            );
        }

        productService.deleteProduct(id);
        return new RsData<>(
                "200-4",
                "상품 삭제가 완료되었습니다."
        );
    }
//    @DeleteMapping("/{Id}")
//    public String deleteProduct(@PathVariable Long Id, RedirectAttributes redirectAttributes) {
//        try {
//            productService.deleteProduct(Id);
//            redirectAttributes.addFlashAttribute("message", "상품이 삭제되었습니다.");
//            return "redirect:/products"; // 상품 목록 페이지로 리다이렉트
//        } catch (RuntimeException e) {
//            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
//            return "redirect:/products/" + Id; // 상품 상세 페이지로 다시 리다이렉트
//        }
//    }


}
