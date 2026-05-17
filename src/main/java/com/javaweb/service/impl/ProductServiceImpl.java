package com.javaweb.service.impl;

import com.javaweb.algorithm.ProductQuickSort;
import com.javaweb.service.ProductTrieService;
import com.javaweb.builder.ProductSearchBuilder;
import com.javaweb.service.ProductService;
import com.javaweb.repository.ProductRepository;
import com.javaweb.repository.ProductImageRepository; // Import thêm
import com.javaweb.repository.entity.ProductEntity;
import com.javaweb.converter.ProductDTOConverter;
import model.ProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javaweb.utils.ConnectionJDBCUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired // Kéo ProductImageRepository vào để dùng
    private ProductImageRepository productImageRepository; 

    @Autowired // Kéo cây Trie vào để sử dụng
    private ProductTrieService productTrieService;
    
    @Override
    public List<ProductDTO> findAll() {
        List<ProductDTO> results = new ArrayList<>();
        
        String sql = "SELECT " +
                     "    p.product_id, p.name, p.description, p.price, p.created_at, " +
                     "    p.category_id, c.name AS category_name, " +
                     "    p.brand_id, b.name AS brand_name, " +
                     "    (SELECT SUM(pd.stock_quantity) FROM product_details pd WHERE pd.product_id = p.product_id) AS stock_quantity, " +
                     "    (SELECT GROUP_CONCAT(pi.image_url SEPARATOR '|||') FROM product_images pi WHERE pi.product_id = p.product_id ORDER BY pi.sort_order ASC) AS thumb " +
                     "FROM products p " +
                     "LEFT JOIN categories c ON p.category_id = c.category_id " +
                     "LEFT JOIN brands b ON p.brand_id = b.brand_id"; 

        try (Connection conn = ConnectionJDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ProductDTO dto = new ProductDTO();
                dto.setId(rs.getInt("product_id"));
                dto.setName(rs.getString("name"));
                dto.setPrice(rs.getDouble("price"));
                dto.setCategoryId(rs.getInt("category_id"));
                dto.setCategoryName(rs.getString("category_name"));
                dto.setBrandId(rs.getInt("brand_id"));
                dto.setBrandName(rs.getString("brand_name"));
                dto.setStock(rs.getInt("stock_quantity"));
                dto.setThumb(rs.getString("thumb"));
                dto.setCreatedAt(rs.getTimestamp("created_at"));
                results.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // =======================================================
        // ÁP DỤNG THUẬT TOÁN QUICKSORT ĐỂ XẾP HẠNG (RANKING)
        // =======================================================
        // Sắp xếp danh sách sản phẩm theo giá từ CAO xuống THẤP (false).
        // Nếu muốn từ thấp đến cao, chỉ cần đổi thành true.
        ProductQuickSort.sort(results, false);

        return results;
    }

    @Override
    public List<ProductDTO> findProduct(ProductSearchBuilder params) {
        List<Integer> matchedIds = null;
        String keyword = params.getName();
        
        // BƯỚC 1: Nếu người dùng có gõ tên sản phẩm -> Gọi Trie chạy trên RAM
        if (keyword != null && !keyword.trim().isEmpty() && !keyword.equals("null")) {
            matchedIds = productTrieService.searchProductIds(keyword);
            
            // BƯỚC 2 (TỐI ƯU O(1)): Nếu Trie không tìm thấy -> Trả về rỗng ngay lập tức, khỏi gọi Database tốn thời gian
            if (matchedIds.isEmpty()) {
                return new ArrayList<>(); 
            }
        }

        // BƯỚC 3: Nếu Trie tìm ra ID (hoặc người dùng không gõ tên), truyền xuống Repository xử lý tiếp
        List<ProductEntity> entities = productRepository.findProduct(params, matchedIds);
        
        List<ProductDTO> results = new ArrayList<>();
        for (ProductEntity item : entities) {
            results.add(ProductDTOConverter.toProductDTO(item));
        }
        return results;
    }

    //Tách chuỗi ||| thành danh sách link
    private List<String> extractImages(ProductDTO dto) {
        List<String> list = new ArrayList<>();
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            list.addAll(dto.getImages());
        } else if (dto.getThumb() != null && !dto.getThumb().isEmpty()) {
            String[] parts = dto.getThumb().split("\\|\\|\\|");
            for (String p : parts) {
                if (!p.trim().isEmpty()) list.add(p.trim());
            }
        }
        return list;
    }

    @Override
    public Integer save(ProductDTO dto) {
        Integer newId = productRepository.save(dto); 
        
        if (newId != null) {
            List<String> imageUrls = extractImages(dto);
            if (!imageUrls.isEmpty()) {
                productImageRepository.saveAll(newId, imageUrls);
            }
            //Nạp sản phẩm vừa tạo vào cây Trie ngay lập tức để tìm kiếm được liền
            productTrieService.addProductToTrie(dto.getName(), newId);
        }
        return newId;
    }

    @Override
    public void update(ProductDTO dto) {
        productRepository.update(dto); // Cập nhật thông tin cơ bản
        
        // Cập nhật ảnh (Xóa ảnh cũ, thêm ảnh mới)
        if (dto.getId() != null) {
            List<String> imageUrls = extractImages(dto);
            if (!imageUrls.isEmpty()) {
                productImageRepository.deleteByProductId(dto.getId());
                productImageRepository.saveAll(dto.getId(), imageUrls);
            }
        }
    }

    @Override
    public void delete(Integer id) {
        productImageRepository.deleteByProductId(id); // Dọn dẹp ảnh trước khi xóa
        productRepository.delete(id);
    }
}