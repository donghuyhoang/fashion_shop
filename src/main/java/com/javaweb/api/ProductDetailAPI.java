package com.javaweb.api;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.javaweb.service.ProductDetailService;
import model.ProductDetailDTO;

@CrossOrigin
@RestController
@RequestMapping("/api/product-details")
public class ProductDetailAPI {

    @Autowired
    private ProductDetailService productDetailService;

    // API lấy toàn bộ thông tin chi tiết (các phiên bản) của 1 sản phẩm
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductDetailDTO>> getDetailsByProduct(@PathVariable Integer productId) {
        List<ProductDetailDTO> details = productDetailService.getDetailsByProductId(productId);
        if (details.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(details);
    }
}