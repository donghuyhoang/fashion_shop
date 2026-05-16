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
        // [FIX 1] Tối ưu hiệu năng: Dùng GROUP_CONCAT để lấy toàn bộ ảnh trong 1 câu truy vấn duy nhất, triệt tiêu lỗi N+1 Query.
	    StringBuilder sql = new StringBuilder("SELECT p.product_id, p.name, p.price, p.description, b.name as brand_name, SUM(pd.stock_quantity) as stock_quantity, ");
	    sql.append("MAX(pd.thumbnail_img_url) as thumbnail_img_url, ");
	    sql.append("(SELECT GROUP_CONCAT(image_url SEPARATOR '|||') FROM product_images WHERE product_id = p.product_id ORDER BY sort_order ASC) as image_urls ");
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
	                
                    String imageUrls = rs.getString("image_urls");
                    if (imageUrls != null && !imageUrls.isEmpty()) {
                        product.setThumbnailUrl(imageUrls);
                    } else {
                        product.setThumbnailUrl(rs.getString("thumbnail_img_url"));
                    }
                    
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
        // Tương tự, áp dụng kỹ thuật gộp chuỗi ảnh bằng SQL cho hàm lấy tất cả
		String sql = "SELECT p.product_id, p.name, p.price, p.description, b.name as brand_name, SUM(pd.stock_quantity) as stock_quantity, " +
                     "MAX(pd.thumbnail_img_url) as thumbnail_img_url, " +
                     "(SELECT GROUP_CONCAT(image_url SEPARATOR '|||') FROM product_images WHERE product_id = p.product_id ORDER BY sort_order ASC) as image_urls " +
                     "FROM products p " +
                     "LEFT JOIN product_details pd ON p.product_id = pd.product_id " +
                     "LEFT JOIN brands b ON p.brand_id = b.brand_id " +
                     "GROUP BY p.product_id, p.name, p.price, p.description, b.name";
		List<ProductEntity> result = new ArrayList<>();
		try (Connection conn = ConnectionJDBCUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
			while(rs.next()) {
				ProductEntity product = new ProductEntity();
				product.setProduct_id(rs.getInt("product_id"));
				product.setName(rs.getString("name"));
    			product.setPrice(rs.getInt("price"));
    			product.setDescription(rs.getString("description"));
				product.setBrandName(rs.getString("brand_name"));
				product.setStockQuantity(rs.getInt("stock_quantity"));
				
                String imageUrls = rs.getString("image_urls");
                if (imageUrls != null && !imageUrls.isEmpty()) {
                    product.setThumbnailUrl(imageUrls);
                } else {
                    product.setThumbnailUrl(rs.getString("thumbnail_img_url"));
                }
    			result.add(product);
			}
			
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	@Override
	public Integer save(model.ProductDTO dto) {
		String sqlProduct = "INSERT INTO products (name, description, price, category_id, brand_id, created_at) VALUES (?, ?, ?, ?, ?, NOW())";
		String sqlDetail = "INSERT INTO product_details (product_id, size_id, color_id, stock_quantity, price, thumbnail_img_url) VALUES (?, ?, ?, ?, ?, ?)";
		String sqlImage = "INSERT INTO product_images (product_id, image_url, is_thumbnail, sort_order) VALUES (?, ?, ?, ?)";
		
		try (Connection conn = ConnectionJDBCUtil.getConnection()) {
			conn.setAutoCommit(false); 
			
			try (PreparedStatement pstmt = conn.prepareStatement(sqlProduct, Statement.RETURN_GENERATED_KEYS)) {
				pstmt.setString(1, dto.getName());
				pstmt.setString(2, dto.getDescription());
				if (dto.getPrice() != null) pstmt.setDouble(3, dto.getPrice()); else pstmt.setNull(3, java.sql.Types.DOUBLE);
				if (dto.getCategoryId() != null) pstmt.setInt(4, dto.getCategoryId()); else pstmt.setNull(4, java.sql.Types.INTEGER);
				if (dto.getBrandId() != null) pstmt.setInt(5, dto.getBrandId()); else pstmt.setNull(5, java.sql.Types.INTEGER);
				pstmt.executeUpdate();
				
				// [SỬA LỖI Ở ĐÂY]: Khai báo biến productId ở ngoài khối if
				Integer productId = null; 
				
				ResultSet rs = pstmt.getGeneratedKeys();
				if (rs.next()) {
					// [SỬA LỖI Ở ĐÂY]: Chỉ gán giá trị, không khai báo lại kiểu int
					productId = rs.getInt(1); 
					
					String firstImage = null;
					String[] images = null;
					if (dto.getThumb() != null && !dto.getThumb().trim().isEmpty()) {
						String separator = dto.getThumb().contains("|||") ? "\\|\\|\\|" : ";";
						images = dto.getThumb().split(separator);
						if (images.length > 0) {
							firstImage = images[0].trim();
						}
					}
					
					// Insert tiếp vào product_details
					try (PreparedStatement pstmtDetail = conn.prepareStatement(sqlDetail)) {
						pstmtDetail.setInt(1, productId);
						if (dto.getSizeId() != null) pstmtDetail.setInt(2, dto.getSizeId()); else pstmtDetail.setNull(2, java.sql.Types.INTEGER);
						if (dto.getColorId() != null) pstmtDetail.setInt(3, dto.getColorId()); else pstmtDetail.setNull(3, java.sql.Types.INTEGER);
						pstmtDetail.setInt(4, dto.getStock() != null ? dto.getStock() : 0);
						if (dto.getPrice() != null) pstmtDetail.setDouble(5, dto.getPrice()); else pstmtDetail.setNull(5, java.sql.Types.DOUBLE);
						pstmtDetail.setString(6, firstImage);
						pstmtDetail.executeUpdate();
					}
					
					// Insert vào product_images
					if (images != null) {
						try (PreparedStatement pstmtImg = conn.prepareStatement(sqlImage)) {
							int sortOrder = 0;
							for (String img : images) {
								String imgUrl = img.trim();
								if (!imgUrl.isEmpty()) {
									pstmtImg.setInt(1, productId);
									pstmtImg.setString(2, imgUrl);
									pstmtImg.setInt(3, sortOrder == 0 ? 1 : 0);
									pstmtImg.setInt(4, sortOrder);
									pstmtImg.addBatch();
									sortOrder++;
								}
							}
							pstmtImg.executeBatch();
						}
					}
				}
				conn.commit(); 
				
				// Lệnh return lúc này đã có thể đọc được biến productId
				return productId; 
				
			} catch (SQLException e) {
				conn.rollback(); 
				e.printStackTrace();
				throw new RuntimeException("Lỗi CSDL khi thêm sản phẩm: " + e.getMessage());
			} finally {
				conn.setAutoCommit(true);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Lỗi kết nối CSDL: " + e.getMessage());
		}
	}

	@Override
	public void update(model.ProductDTO dto) {
		String sqlProduct = "UPDATE products SET name=?, description=?, price=?, category_id=?, brand_id=? WHERE product_id=?";
		// Vá lỗi nghiêm trọng: Chỉ cập nhật ảnh, TUYỆT ĐỐI KHÔNG ghi đè làm hỏng size và số lượng kho cũ!
		String sqlDetail = "UPDATE product_details SET thumbnail_img_url=? WHERE product_id=?";
		String sqlDeleteImages = "DELETE FROM product_images WHERE product_id=?";
		String sqlInsertImage = "INSERT INTO product_images (product_id, image_url, is_thumbnail, sort_order) VALUES (?, ?, ?, ?)";
		
		try (Connection conn = ConnectionJDBCUtil.getConnection()) {
			conn.setAutoCommit(false);
			try (PreparedStatement pstmt = conn.prepareStatement(sqlProduct)) {
				
				// Update bảng products
				pstmt.setString(1, dto.getName());
				pstmt.setString(2, dto.getDescription());
				if (dto.getPrice() != null) pstmt.setDouble(3, dto.getPrice()); else pstmt.setNull(3, java.sql.Types.DOUBLE);
				if (dto.getCategoryId() != null) pstmt.setInt(4, dto.getCategoryId()); else pstmt.setNull(4, java.sql.Types.INTEGER);
				if (dto.getBrandId() != null) pstmt.setInt(5, dto.getBrandId()); else pstmt.setNull(5, java.sql.Types.INTEGER);
				pstmt.setInt(6, dto.getId());
				pstmt.executeUpdate();
				
				String[] images = null;
				if (dto.getThumb() != null && !dto.getThumb().trim().isEmpty()) {
				    String separator = dto.getThumb().contains("|||") ? "\\|\\|\\|" : ";";
				    images = dto.getThumb().split(separator);
				}
				
                // [FIX 4] Ràng buộc logic: CHỈ cập nhật và xóa ảnh nếu mảng images có dữ liệu gửi lên. Bảo vệ ảnh cũ không bị bốc hơi.
				if (images != null && images.length > 0) {
				    String firstImage = images[0].trim();
				    
                    try(PreparedStatement pstmtDetail = conn.prepareStatement(sqlDetail)) {
                        pstmtDetail.setString(1, firstImage);
                        pstmtDetail.setInt(2, dto.getId());
                        pstmtDetail.executeUpdate();
                    }

                    try (PreparedStatement pstmtDelImg = conn.prepareStatement(sqlDeleteImages)) {
                        pstmtDelImg.setInt(1, dto.getId());
                        pstmtDelImg.executeUpdate();
                    }

                    try (PreparedStatement pstmtImg = conn.prepareStatement(sqlInsertImage)) {
                        int sortOrder = 0;
                        for (String img : images) {
                            String imgUrl = img.trim();
                            if (!imgUrl.isEmpty()) {
                                pstmtImg.setInt(1, dto.getId());
                                pstmtImg.setString(2, imgUrl);
                                pstmtImg.setInt(3, sortOrder == 0 ? 1 : 0);
                                pstmtImg.setInt(4, sortOrder);
                                pstmtImg.addBatch();
                                sortOrder++;
                            }
                        }
                        pstmtImg.executeBatch();
                    }
				}
				
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
			throw new RuntimeException("Lỗi kết nối CSDL: " + e.getMessage());
		}
	}

	@Override
    public void delete(Integer id) {
        // Hàm delete cũ của bạn đã xử lý Transaction cực tốt, mình giữ nguyên 100%
        String sqlImage = "DELETE FROM product_images WHERE product_id = ?";
        String sqlCartItem = "DELETE FROM cart_items WHERE product_detail_id IN (SELECT product_detail_id FROM product_details WHERE product_id = ?)";
        String sqlDetail = "DELETE FROM product_details WHERE product_id = ?";
        String sqlProduct = "DELETE FROM products WHERE product_id = ?";
        
        try (Connection conn = ConnectionJDBCUtil.getConnection()) {
            conn.setAutoCommit(false); // Bật khiên bảo vệ Transaction
            
            try (PreparedStatement pstmtImg = conn.prepareStatement(sqlImage);
                 PreparedStatement pstmtCart = conn.prepareStatement(sqlCartItem);
                 PreparedStatement pstmtDetail = conn.prepareStatement(sqlDetail);
                 PreparedStatement pstmtProduct = conn.prepareStatement(sqlProduct)) {
                 
                // Xóa ảnh
                pstmtImg.setInt(1, id);
                pstmtImg.executeUpdate();

                // Dọn dẹp giỏ hàng
                pstmtCart.setInt(1, id);
                pstmtCart.executeUpdate();
                 
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
                if (e.getMessage().toLowerCase().contains("foreign key constraint")) {
                    throw new RuntimeException("Không thể xóa! Sản phẩm này đã có khách hàng đặt mua (đang nằm trong Lịch sử đơn hàng).");
                }
                throw new RuntimeException("Lỗi CSDL khi xóa sản phẩm: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true); // Trả lại trạng thái mặc định cho Connection Pool
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi kết nối CSDL: " + e.getMessage());
        }
    }
}
