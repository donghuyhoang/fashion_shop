package model;

public class ProductDetailDTO {
    private Integer productDetailId;
    private Integer productId;
    private Integer sizeId;     
    private Integer colorId;    
    private Integer stockQuantity;
    private Integer price;
    private String thumbnailUrl;

    // Getter ane Setter
    public Integer getProductDetailId() { return productDetailId; }
    public void setProductDetailId(Integer productDetailId) { this.productDetailId = productDetailId; }
    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public Integer getSizeId() { return sizeId; }
    public void setSizeId(Integer sizeId) { this.sizeId = sizeId; }
    public Integer getColorId() { return colorId; }
    public void setColorId(Integer colorId) { this.colorId = colorId; }
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
}