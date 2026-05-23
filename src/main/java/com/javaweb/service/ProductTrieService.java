package com.javaweb.service;

import com.javaweb.algorithm.Trie;
import com.javaweb.repository.ProductRepository;
import com.javaweb.repository.entity.ProductEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Service
public class ProductTrieService {

    private Trie trie;

    @Autowired
    private ProductRepository productRepository;

    // Annotation @PostConstruct giúp hàm này tự động chạy NGAY KHI khởi động ứng dụng
    @PostConstruct
    public void initTrie() {
        trie = new Trie();
        System.out.println("====== BẮT ĐẦU NẠP DỮ LIỆU SẢN PHẨM VÀO CÂY TRIE ======");
        
        // Lấy tất cả sản phẩm từ DB
        List<ProductEntity> allProducts = productRepository.findAll();
        
        for (ProductEntity product : allProducts) {
            String name = product.getName();
            Integer id = product.getProduct_id();
            
            if (name != null) {
                // Mẹo thuật toán: Để tìm kiếm chính xác (giống LIKE %name%), 
                // ta tách tên sản phẩm thành các từ và lưu từng từ vào Trie.
                // VD: "Áo thun nam" -> Lưu "Áo thun nam", "thun nam", "nam"
                String[] words = name.split("\\s+");
                for (int i = 0; i < words.length; i++) {
                    StringBuilder suffix = new StringBuilder();
                    for (int j = i; j < words.length; j++) {
                        suffix.append(words[j]).append(" ");
                    }
                    trie.insert(suffix.toString().trim(), id);
                }
            }
        }
        System.out.println("====== NẠP TRIE HOÀN TẤT ======");
    }

    // Cung cấp hàm tìm kiếm ra bên ngoài
    public List<Integer> searchProductIds(String keyword) {
        return trie.searchPrefix(keyword);
    }

    // Cập nhật Trie khi thêm/sửa sản phẩm mới (không cần khởi động lại server)
    public void addProductToTrie(String name, Integer productId) {
        if (name != null) {
            String[] words = name.split("\\s+");
            for (int i = 0; i < words.length; i++) {
                StringBuilder suffix = new StringBuilder();
                for (int j = i; j < words.length; j++) {
                    suffix.append(words[j]).append(" ");
                }
                trie.insert(suffix.toString().trim(), productId);
            }
        }
    }
}