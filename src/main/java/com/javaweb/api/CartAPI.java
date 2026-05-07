package com.javaweb.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.javaweb.service.CartService;
import model.CartRequestDTO;
import model.CartItemResponseDTO;
import java.util.List;

@CrossOrigin 
@RestController
@RequestMapping("/api/cart")
public class CartAPI {

    @Autowired
    private CartService cartService;

    // API 1: Thêm vào giỏ hàng
    @PostMapping("/add")
    public ResponseEntity<String> addToCart(@RequestBody CartRequestDTO requestDTO) {
        try {
            cartService.addToCart(requestDTO);
            return ResponseEntity.ok("Thêm vào giỏ hàng thành công!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi: " + e.getMessage());
        }
    }

    // API 2: Đếm số lượng để hiện cục Badge màu đỏ trên Frontend
    @GetMapping("/count/{userId}")
    public ResponseEntity<Integer> getCartCount(@PathVariable Integer userId) {
        try {
            Integer count = cartService.getCartCount(userId);
            return ResponseEntity.ok(count != null ? count : 0);
        } catch (Exception e) {
            return ResponseEntity.ok(0);
        }
    }

    // API 3: Lấy danh sách sản phẩm hiển thị trên trang cart.html
    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItemResponseDTO>> getCart(@PathVariable Integer userId) {
        try {
            List<CartItemResponseDTO> cartItems = cartService.getCartByUser(userId);
            return ResponseEntity.ok(cartItems);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // API 4: Xóa sản phẩm khỏi giỏ hàng
    @DeleteMapping("/remove")
    public ResponseEntity<String> removeCartItem(@RequestParam Integer userId, @RequestParam Integer productDetailId) {
        try {
            cartService.removeCartItem(userId, productDetailId);
            return ResponseEntity.ok("Đã xóa sản phẩm khỏi giỏ hàng!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi: " + e.getMessage());
        }
    }
}