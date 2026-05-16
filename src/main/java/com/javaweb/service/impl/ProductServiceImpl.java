package com.javaweb.service.impl;

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

    @Override
    public List<ProductDTO> findAll() {
        List<ProductDTO> results = new ArrayList<>();
        // Lấy ảnh trực tiếp từ bảng product_images và gộp lại bằng |||
        String sql = "SELECT " +
                     "    p.product_id, p.name, p.description, p.price, p.created_at, " +
                     "    p.category_id, c.name AS category_name, " +
                     "    p.brand_id, b.name AS brand_name, " +
                     "    (SELECT SUM(pd.stock_quantity) FROM product_details pd WHERE pd.product_id = p.product_id) AS stock_quantity, " +
                     "    (SELECT GROUP_CONCAT(pi.image_url SEPARATOR '|||') FROM product_images pi WHERE pi.product_id = p.product_id ORDER BY pi.sort_order ASC) AS thumb " +
                     "FROM products p " +
                     "LEFT JOIN categories c ON p.category_id = c.category_id " +
                     "LEFT JOIN brands b ON p.brand_id = b.brand_id " +
                     "ORDER BY p.product_id DESC";

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
        return results;
    }

    @Override
    public List<ProductDTO> findProduct(ProductSearchBuilder params) {
        List<ProductEntity> entities = productRepository.findProduct(params);
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
        Integer newId = productRepository.save(dto); // Lấy ID vừa tạo
        
        // Giải nén ảnh và đưa vào Database bảng Product Images
        if (newId != null) {
            List<String> imageUrls = extractImages(dto);
            if (!imageUrls.isEmpty()) {
                productImageRepository.saveAll(newId, imageUrls);
            }
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