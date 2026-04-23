package model;

public class CartRequestDTO {
    private Integer userId;
    private Integer productDetailId;
    private Integer quantity;

    // --- Getter & Setter ---
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getProductDetailId() { return productDetailId; }
    public void setProductDetailId(Integer productDetailId) { this.productDetailId = productDetailId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}