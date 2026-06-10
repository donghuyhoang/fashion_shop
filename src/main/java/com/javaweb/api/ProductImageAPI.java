package com.javaweb.api;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.javaweb.service.ProductImageService;
import model.ProductImageDTO;

@CrossOrigin
@RestController
@RequestMapping("/api/product-images")
public class ProductImageAPI {

    @Autowired
    private ProductImageService productImageService;

    // API lấy danh sách toàn bộ ảnh của 1 sản phẩm
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductImageDTO>> getImagesByProduct(@PathVariable("productId") Integer productId) {
        List<ProductImageDTO> images = productImageService.getImagesByProductId(productId);
        if (images.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(images);
    }
}