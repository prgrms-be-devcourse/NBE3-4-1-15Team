package com.nbe.NBE3_4_1_Team15.domain.cart;

import com.nbe.NBE3_4_1_Team15.domain.cart.service.CartService;
import com.nbe.NBE3_4_1_Team15.domain.cartProduct.dto.CartProductDto;
import com.nbe.NBE3_4_1_Team15.domain.cartProduct.entity.CartProduct;
import com.nbe.NBE3_4_1_Team15.domain.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    // TODO: Post /cart 장바구니 아이템 추가
    @PostMapping()
    public ResponseEntity<String> addCartProduct(@RequestParam Long memberId, @RequestBody Product product){
        cartService.addProductToCart(memberId, product);
        return ResponseEntity.status(HttpStatus.CREATED).body("장바구니에 추가됐습니다");
    }

    // TODO: Get /cart 장바구니
    @GetMapping("/{carId}")
    public ResponseEntity<List<CartProductDto>> getCartProduct(@PathVariable Long carId){
        List<CartProductDto> cartproducts = cartService.getCartProduct(carId);
        return ResponseEntity.ok(cartproducts);
    }

    // TODO: Put /cart/{id} 특정 장바구니 아이템 업데이트
    @PutMapping("/{id}")
    public ResponseEntity<String> updateCartProduct(@PathVariable Long id, @RequestBody Integer quantity){
        cartService.updateCartProduct(id, quantity);
        return ResponseEntity.ok("업데이트가 정상적으로 이뤄졌습니다.");
    }
    // TODO: DELETE /cart/{id} 특정 장바구니 아이템 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCartProduct(@PathVariable Long id){
        cartService.deleteCartProduct(id);
        return ResponseEntity.noContent().build();

    }
}
