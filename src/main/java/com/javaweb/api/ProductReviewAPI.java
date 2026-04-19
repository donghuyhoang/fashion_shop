package com.javaweb.api;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.javaweb.service.ProductReviewService;
import model.ProductReviewDTO;

@CrossOrigin
@RestController
@RequestMapping("/api/product-reviews")
public class ProductReviewAPI {

    @Autowired
    private ProductReviewService productReviewService;

    // API lấy tất cả bình luận của 1 sản phẩm
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductReviewDTO>> getReviewsByProduct(@PathVariable Integer productId) {
        List<ProductReviewDTO> reviews = productReviewService.getReviewsByProductId(productId);
        if (reviews.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(reviews);
    }
}