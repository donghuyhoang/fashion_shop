package com.javaweb.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.javaweb.builder.ProductSearchBuilder;
import com.javaweb.repository.ProductRepository;
import com.javaweb.repository.entity.ProductEntity;
import com.javaweb.utils.ConnectionJDBCUtil;
@Repository

public class ProductRepositoryImpl implements ProductRepository{
	@Override
	public List<ProductEntity> findProduct(ProductSearchBuilder params) {
	    StringBuilder sql = new StringBuilder("SELECT p.product_id, p.name, p.price, p.description, b.name as brand_name, SUM(pd.stock_quantity) as stock_quantity, MAX(pd.thumbnail_img_url) as thumbnail_img_url ");
	    sql.append("FROM products p ");
	    sql.append("LEFT JOIN product_details pd ON p.product_id = pd.product_id ");
	    sql.append("LEFT JOIN brands b ON p.brand_id = b.brand_id WHERE 1=1 ");

	    List<Object> queryParams = new ArrayList<>();

	    if(params.getName() != null && !params.getName().trim().isEmpty() && !params.getName().equals("null")) {
	        sql.append(" AND p.name LIKE ? ");
	        queryParams.add("%" + params.getName() + "%");
	    }
	    if(params.getBrandId() != null) {
	        sql.append(" AND p.brand_id = ? ");
	        queryParams.add(params.getBrandId());
	    }
	    if(params.getCategoryId() != null) {
	        sql.append(" AND p.category_id = ? ");
	        queryParams.add(params.getCategoryId());
	    }
	    if(params.getMinPrice() != null) {
	        sql.append(" AND p.price >= ? ");
	        queryParams.add(params.getMinPrice());
	    }
	    if(params.getMaxPrice() != null) {
	        sql.append(" AND p.price <= ? ");
	        queryParams.add(params.getMaxPrice());
	    }

	    sql.append(" GROUP BY p.product_id, p.name, p.price, p.description, b.name");

	    List<ProductEntity> result = new ArrayList<>();
	    
	    try (Connection conn = ConnectionJDBCUtil.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
	         
	        // Set các tham số vào dấu ?
	        for (int i = 0; i < queryParams.size(); i++) {
	            pstmt.setObject(i + 1, queryParams.get(i));
	        }

	        try (ResultSet rs = pstmt.executeQuery()) {
	            while(rs.next()) {
	                ProductEntity product = new ProductEntity();
	                product.setProduct_id(rs.getInt("product_id"));
	                product.setName(rs.getString("name"));
	                product.setPrice(rs.getInt("price"));
	                product.setDescription(rs.getString("description"));
	                product.setBrandName(rs.getString("brand_name"));
	                product.setStockQuantity(rs.getInt("stock_quantity"));
	                product.setThumbnailUrl(rs.getString("thumbnail_img_url"));
	                result.add(product);
	            }
	        }
	    }
		 catch(SQLException e) {
	        e.printStackTrace();
	    }
	    return result;
	}

	@Override
	public List<ProductEntity> findAll() {
		String sql = "select * from products";
		List<ProductEntity> result = new ArrayList<>();
		try(Connection conn = ConnectionJDBCUtil.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);){
			while(rs.next()) {
				ProductEntity product = new ProductEntity();
				product.setName(rs.getString("name"));
    			product.setPrice(rs.getInt("price"));
    			product.setProduct_id(rs.getInt("product_id"));
    			product.setDescription(rs.getString("description"));
    			result.add(product);
			}
			
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	@Override
	public void save(model.ProductDTO dto) {
		String sqlProduct = "INSERT INTO products (name, description, price, category_id, brand_id, created_at) VALUES (?, ?, ?, ?, ?, NOW())";
		String sqlDetail = "INSERT INTO product_details (product_id, size_id, color_id, stock_quantity, price, thumbnail_img_url) VALUES (?, ?, ?, ?, ?, ?)";
		
		try (Connection conn = ConnectionJDBCUtil.getConnection()) {
			conn.setAutoCommit(false); 
			
			try (PreparedStatement pstmt = conn.prepareStatement(sqlProduct, Statement.RETURN_GENERATED_KEYS)) {
				pstmt.setString(1, dto.getName());
				pstmt.setString(2, dto.getDescription());
				pstmt.setObject(3, dto.getPrice());
				pstmt.setObject(4, dto.getCategoryId());
				pstmt.setObject(5, dto.getBrandId());
				pstmt.executeUpdate();
				
				// Lấy product_id vừa được sinh ra
				ResultSet rs = pstmt.getGeneratedKeys();
				if (rs.next()) {
					int productId = rs.getInt(1);
					
					// Insert tiếp vào product_details
					try (PreparedStatement pstmtDetail = conn.prepareStatement(sqlDetail)) {
						pstmtDetail.setInt(1, productId);
						pstmtDetail.setObject(2, dto.getSizeId());
						pstmtDetail.setObject(3, dto.getColorId());
						pstmtDetail.setObject(4, dto.getStock() != null ? dto.getStock() : 0);
						pstmtDetail.setObject(5, dto.getPrice());
						pstmtDetail.setString(6, dto.getThumb());
						pstmtDetail.executeUpdate();
					}
				}
				conn.commit(); 
			} catch (SQLException e) {
				conn.rollback(); 
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(model.ProductDTO dto) {
		String sqlProduct = "UPDATE products SET name=?, description=?, price=?, category_id=?, brand_id=? WHERE product_id=?";
		String sqlDetail = "UPDATE product_details SET size_id=?, color_id=?, stock_quantity=?, price=?, thumbnail_img_url=? WHERE product_id=?";
		
		try (Connection conn = ConnectionJDBCUtil.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sqlProduct);
			 PreparedStatement pstmtDetail = conn.prepareStatement(sqlDetail)) {
			
			// Update bảng products
			pstmt.setString(1, dto.getName());
			pstmt.setString(2, dto.getDescription());
			pstmt.setObject(3, dto.getPrice());
			pstmt.setObject(4, dto.getCategoryId());
			pstmt.setObject(5, dto.getBrandId());
			pstmt.setInt(6, dto.getId());
			pstmt.executeUpdate();
			
			// Update bảng product_details
			pstmtDetail.setObject(1, dto.getSizeId());
			pstmtDetail.setObject(2, dto.getColorId());
			pstmtDetail.setObject(3, dto.getStock() != null ? dto.getStock() : 0);
			pstmtDetail.setObject(4, dto.getPrice());
			pstmtDetail.setString(5, dto.getThumb());
			pstmtDetail.setInt(6, dto.getId());
			pstmtDetail.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Xóa sản phẩm sẽ phải xóa cả chi tiết, nên cần dùng Transaction để đảm bảo tính toàn vẹn dữ liệu
	@Override
    public void delete(Integer id) {
        // Xóa thằng con trước
        String sqlDetail = "DELETE FROM product_details WHERE product_id = ?";
        // Xóa thằng bố sau
        String sqlProduct = "DELETE FROM products WHERE product_id = ?";
        
        try (Connection conn = ConnectionJDBCUtil.getConnection()) {
            conn.setAutoCommit(false); // Bật khiên bảo vệ Transaction
            
            try (PreparedStatement pstmtDetail = conn.prepareStatement(sqlDetail);
                 PreparedStatement pstmtProduct = conn.prepareStatement(sqlProduct)) {
                 
                // Xóa chi tiết
                pstmtDetail.setInt(1, id);
                pstmtDetail.executeUpdate();
                
                // Xóa sản phẩm
                pstmtProduct.setInt(1, id);
                pstmtProduct.executeUpdate();
                
                conn.commit(); // Thành công thì lưu cả 2
            } catch (SQLException e) {
                conn.rollback(); // Lỗi thì hoàn tác
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
