package com.nbe.NBE3_4_1_Team15.domain.product.controller;

import com.nbe.NBE3_4_1_Team15.domain.product.dto.ProductCreateDto;
import com.nbe.NBE3_4_1_Team15.domain.product.dto.ProductResponseDto;
import com.nbe.NBE3_4_1_Team15.domain.product.dto.ProductUpdateDto;
import com.nbe.NBE3_4_1_Team15.domain.product.entity.Product;
import com.nbe.NBE3_4_1_Team15.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
    //dadad
    private final ProductService productService;

    @GetMapping("/new") // 상품 생성 폼을 보여주는 GET 요청
    public String createForm(Model model) {
        model.addAttribute("productCreateDto", new ProductCreateDto());
        return "products/createForm"; // View 이름
    }

    @PostMapping
    public String create(@ModelAttribute ProductCreateDto createDto, RedirectAttributes redirectAttributes) {
        try {
            ProductResponseDto responseDto = productService.create(createDto);
            redirectAttributes.addFlashAttribute("message", "상품이 성공적으로 생성되었습니다.");
            return "redirect:/products/" + responseDto.getId(); // 생성된 상품 상세 페이지로 리다이렉트
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/products/new"; // 생성 폼으로 다시 리다이렉트
        }
    }

    @GetMapping
    public String getProducts(Model model) {
        List<ProductResponseDto> productList = productService.getProductList();
        model.addAttribute("productList", productList);
        return "products/list"; // View 이름
    }

    @GetMapping("/{Id}")
    public String getProduct(@PathVariable Long Id, Model model) {
        try {
            ProductResponseDto product = productService.getProduct(Id);
            model.addAttribute("product", product);
            return "products/detail"; // View 이름
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error"; // 에러 페이지
        }
    }

    @GetMapping("/{Id}/edit") // 상품 수정 폼을 보여주는 GET 요청
    public String updateForm(@PathVariable Long Id, Model model) {
        try {
            ProductResponseDto product = productService.getProduct(Id);
            ProductUpdateDto updateDto = new ProductUpdateDto(product.getPrice(), product.getDescription(), product.getStock());
            model.addAttribute("updateDto", updateDto);
            model.addAttribute("Id", Id);
            return "products/updateForm"; // View 이름
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error"; // 에러 페이지
        }
    }

    @PostMapping("/{Id}")
    public String updateProduct(@PathVariable Long Id, @ModelAttribute ProductUpdateDto updateDto, RedirectAttributes redirectAttributes) {
        try {
            productService.updateProduct(Id, updateDto);
            redirectAttributes.addFlashAttribute("message", "상품이 수정되었습니다.");
            return "redirect:/products/" + Id; // 수정된 상품 상세 페이지로 리다이렉트
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/products/" + Id + "/edit"; // 수정 폼으로 다시 리다이렉트
        }
    }

    @DeleteMapping("/{Id}")
    public String deleteProduct(@PathVariable Long Id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(Id);
            redirectAttributes.addFlashAttribute("message", "상품이 삭제되었습니다.");
            return "redirect:/products"; // 상품 목록 페이지로 리다이렉트
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/products/" + Id; // 상품 상세 페이지로 다시 리다이렉트
        }
    }


}
