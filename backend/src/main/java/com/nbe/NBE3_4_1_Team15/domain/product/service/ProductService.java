package com.nbe.NBE3_4_1_Team15.domain.product.service;

import com.nbe.NBE3_4_1_Team15.domain.product.dto.ProductCreateDto;
import com.nbe.NBE3_4_1_Team15.domain.product.dto.ProductResponseDto;
import com.nbe.NBE3_4_1_Team15.domain.product.dto.ProductUpdateDto;
import com.nbe.NBE3_4_1_Team15.domain.product.entity.Product;
import com.nbe.NBE3_4_1_Team15.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;

    private Product findProductOrThrow(Long id) {
        return productRepository.findById(id).orElseThrow(()->new RuntimeException("NOT_FOUND_PRODUCT"));
    }

    @Transactional
    public ProductResponseDto create(ProductCreateDto productCreateDto) throws RuntimeException {
        Optional<Product> opProduct = productRepository.findByName(productCreateDto.getName());

        if (opProduct.isPresent()) {
            throw new RuntimeException();
        }

        Product product = productCreateDto.toEntity();
        productRepository.save(product);
        return ProductResponseDto.from(product);
    }

    public List<ProductResponseDto> getProductList() {
        List<Product> productList = productRepository.findAll();

        return productList.stream().map(ProductResponseDto::from).toList();
    }

    public ProductResponseDto getProduct(Long id) throws RuntimeException {
        Optional<Product> opProduct = productRepository.findById(id);

        if (!opProduct.isPresent()) {
            throw new RuntimeException();
        }

        Product product = opProduct.get();
        return ProductResponseDto.from(product);

    }

    @Transactional
    public ProductResponseDto updateProduct(Long id, ProductUpdateDto updateDto) {
        Product findProduct = findProductOrThrow(id);
        findProduct.updateFromDto(updateDto);
        return ProductResponseDto.from(findProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product findProduct = findProductOrThrow(id);
        productRepository.delete(findProduct);
    }
}
