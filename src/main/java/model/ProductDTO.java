package model;

import java.util.Date;
import java.util.List;

public class ProductDTO {
    private Integer id;
    private String name;
    private String description;
    private Double price;
    private Integer categoryId;
    private String categoryName;
    private Integer brandId;
    private String brandName;
    private Date createdAt;
    
    private String thumb; 
    private List<String> images; 
    
    private Integer stock;
    private Integer sizeId;
    private Integer colorId;

    // --- Getters and Setters ---
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public Integer getBrandId() { return brandId; }
    public void setBrandId(Integer brandId) { this.brandId = brandId; }
    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    
    public String getThumb() { return thumb; }
    public void setThumb(String thumb) { this.thumb = thumb; }
    
    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
    
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public Integer getSizeId() { return sizeId; }
    public void setSizeId(Integer sizeId) { this.sizeId = sizeId; }
    public Integer getColorId() { return colorId; }
    public void setColorId(Integer colorId) { this.colorId = colorId; }
}