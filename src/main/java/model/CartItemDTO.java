package model;

public class CartItemDTO {
    private Integer productDetailId;
    private Integer quantity;
    private Integer unitPrice;

    public Integer getProductDetailId() { return productDetailId; }
    public void setProductDetailId(Integer productDetailId) { this.productDetailId = productDetailId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Integer getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Integer unitPrice) { this.unitPrice = unitPrice; }
}