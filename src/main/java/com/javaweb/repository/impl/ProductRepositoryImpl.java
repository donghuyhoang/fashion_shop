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
	public List<ProductEntity> findAll() {
		String sql = "SELECT p.product_id, p.name, p.price, p.description, b.name as brand_name, SUM(pd.stock_quantity) as stock_quantity, MAX(pd.thumbnail_img_url) as thumbnail_img_url " +
                     "FROM products p " +
                     "LEFT JOIN product_details pd ON p.product_id = pd.product_id " +
                     "LEFT JOIN brands b ON p.brand_id = b.brand_id " +
                     "GROUP BY p.product_id, p.name, p.price, p.description, b.name";
                     
		List<ProductEntity> result = new ArrayList<>();
		try(Connection conn = ConnectionJDBCUtil.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);){
			while(rs.next()) {
				ProductEntity product = new ProductEntity();
				product.setProduct_id(rs.getInt("product_id"));
				product.setName(rs.getString("name"));
    			product.setPrice(rs.getInt("price"));
    			product.setDescription(rs.getString("description"));
				product.setBrandName(rs.getString("brand_name"));
				product.setStockQuantity(rs.getInt("stock_quantity"));
                // Lấy ảnh từ product_details (MAX)
				product.setThumbnailUrl(rs.getString("thumbnail_img_url")); 
				product.setThumb(rs.getString("thumbnail_img_url"));
				
    			result.add(product);
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public List<ProductEntity> findProduct(ProductSearchBuilder params, List<Integer> matchedIds) {
        // Đã xóa p.thumb khỏi SELECT
	    StringBuilder sql = new StringBuilder("SELECT p.product_id, p.name, p.price, p.description, b.name as brand_name, SUM(pd.stock_quantity) as stock_quantity, MAX(pd.thumbnail_img_url) as thumbnail_img_url ");
	    sql.append("FROM products p ");
	    sql.append("LEFT JOIN product_details pd ON p.product_id = pd.product_id ");
	    sql.append("LEFT JOIN brands b ON p.brand_id = b.brand_id WHERE 1=1 ");

	    List<Object> queryParams = new ArrayList<>();

	    if (matchedIds != null) {
            if (matchedIds.isEmpty()) {
                sql.append(" AND 1=0 "); 
            } else {
                sql.append(" AND p.product_id IN (");
                for (int i = 0; i < matchedIds.size(); i++) {
                    sql.append("?");
                    if (i < matchedIds.size() - 1) sql.append(",");
                    queryParams.add(matchedIds.get(i));
                }
                sql.append(") ");
            }
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
	                product.setThumb(rs.getString("thumbnail_img_url"));
	                result.add(product);
	            }
	        }
	    } catch(SQLException e) {
	        e.printStackTrace();
	    }
	    return result;
	}

	@Override
	public Integer save(model.ProductDTO dto) {
		String sqlProduct = "INSERT INTO products (name, description, price, category_id, brand_id, created_at) VALUES (?, ?, ?, ?, ?, NOW())";
		String sqlDetail = "INSERT INTO product_details (product_id, size_id, color_id, stock_quantity, price, thumbnail_img_url) VALUES (?, ?, ?, ?, ?, ?)";
		
        Integer generatedId = null;

		try (Connection conn = ConnectionJDBCUtil.getConnection()) {
			conn.setAutoCommit(false); 
			
			try (PreparedStatement pstmt = conn.prepareStatement(sqlProduct, Statement.RETURN_GENERATED_KEYS)) {
				pstmt.setString(1, dto.getName());
				pstmt.setString(2, dto.getDescription());
				pstmt.setObject(3, dto.getPrice());
				pstmt.setObject(4, dto.getCategoryId());
				pstmt.setObject(5, dto.getBrandId());
				pstmt.executeUpdate();
				
				ResultSet rs = pstmt.getGeneratedKeys();
				if (rs.next()) {
					generatedId = rs.getInt(1); 
					
					try (PreparedStatement pstmtDetail = conn.prepareStatement(sqlDetail)) {
						pstmtDetail.setInt(1, generatedId); 
						pstmtDetail.setObject(2, dto.getSizeId());
						pstmtDetail.setObject(3, dto.getColorId());
						pstmtDetail.setObject(4, dto.getStock() != null ? dto.getStock() : 0);
						pstmtDetail.setObject(5, dto.getPrice());

                        // Cắt lấy đúng link ảnh đầu tiên để không bị quá ký tự
                        String thumbUrl = dto.getThumb();
                        if (thumbUrl != null && thumbUrl.contains("|||")) {
                            thumbUrl = thumbUrl.split("\\|\\|\\|")[0].trim(); 
                        }
						pstmtDetail.setString(6, thumbUrl);

						pstmtDetail.executeUpdate();
					}
				}
				conn.commit(); 
			} catch (SQLException e) {
				conn.rollback(); 
				e.printStackTrace();
                // Ném lỗi báo về API
                throw new RuntimeException("Lỗi CSDL khi insert dữ liệu: " + e.getMessage());
			} finally {
                conn.setAutoCommit(true); 
            }
		} catch (SQLException e) {
			e.printStackTrace();
            // Ném lỗi báo về API
            throw new RuntimeException("Lỗi kết nối hoặc thực thi SQL: " + e.getMessage());
		}
        
        return generatedId; 
	}

	@Override
	public void update(model.ProductDTO dto) {
		// CHỈ update bảng products - KHÔNG update product_details ở đây
		// Vì mỗi variant (size/màu) được cập nhật riêng qua ProductDetailRepositoryImpl.addProductDetail()
		// Nếu update product_details theo WHERE product_id thì sẽ ghi đè tất cả variant bằng 1 giá trị (BUG)
		String sqlProduct = "UPDATE products SET name=?, description=?, price=?, category_id=?, brand_id=? WHERE product_id=?";
		
		try (Connection conn = ConnectionJDBCUtil.getConnection()) {
			conn.setAutoCommit(false);
			try (PreparedStatement pstmt = conn.prepareStatement(sqlProduct)) {
				
				pstmt.setString(1, dto.getName());
				pstmt.setString(2, dto.getDescription());
				pstmt.setObject(3, dto.getPrice());
				pstmt.setObject(4, dto.getCategoryId());
				pstmt.setObject(5, dto.getBrandId());
				pstmt.setInt(6, dto.getId());
				pstmt.executeUpdate();
				
				conn.commit();
			} catch (SQLException e) {
				conn.rollback();
				e.printStackTrace();
                throw new RuntimeException("Lỗi CSDL khi cập nhật sản phẩm: " + e.getMessage());
			} finally {
				conn.setAutoCommit(true);
			}
		} catch (SQLException e) {
			e.printStackTrace();
            throw new RuntimeException("Lỗi kết nối hoặc thực thi SQL: " + e.getMessage());
		}
	}

	@Override
    public void delete(Integer id) {
        // Xóa đúng thứ tự: bảng con trước, bảng cha sau
        // cart_items và order_details tham chiếu product_details -> phải xóa trước
        String sqlCartItems    = "DELETE FROM cart_items WHERE product_detail_id IN (SELECT product_detail_id FROM product_details WHERE product_id = ?)";
        String sqlOrderDetails = "DELETE FROM order_details WHERE product_detail_id IN (SELECT product_detail_id FROM product_details WHERE product_id = ?)";
        String sqlDetail       = "DELETE FROM product_details WHERE product_id = ?";
        String sqlProduct      = "DELETE FROM products WHERE product_id = ?";
        
        try (Connection conn = ConnectionJDBCUtil.getConnection()) {
            conn.setAutoCommit(false); 
            
            try (PreparedStatement pstmtCart    = conn.prepareStatement(sqlCartItems);
                 PreparedStatement pstmtOrder   = conn.prepareStatement(sqlOrderDetails);
                 PreparedStatement pstmtDetail  = conn.prepareStatement(sqlDetail);
                 PreparedStatement pstmtProduct = conn.prepareStatement(sqlProduct)) {
                 
                pstmtCart.setInt(1, id);
                pstmtCart.executeUpdate();
                
                pstmtOrder.setInt(1, id);
                pstmtOrder.executeUpdate();
                
                pstmtDetail.setInt(1, id);
                pstmtDetail.executeUpdate();
                
                pstmtProduct.setInt(1, id);
                pstmtProduct.executeUpdate();
                
                conn.commit(); 
            } catch (SQLException e) {
                conn.rollback(); 
                throw e; // ném lên để tầng trên biết lỗi
            } finally {
                conn.setAutoCommit(true); 
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Loi khi xoa san pham ID: " + id, e);
        }
    }
}