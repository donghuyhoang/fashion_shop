package com.javaweb.repository.entity;

public class ProductDetailEntity {
    // Property
    private Integer product_detail_id;
    private Integer product_id;
    private Integer size_id;   
    private Integer color_id;   
    private Integer stock_quantity;
    private Integer price;
    private String thumbnail_img_url;

    // Getter ane Setter method
    public Integer getProduct_detail_id() { return product_detail_id; }
    public void setProduct_detail_id(Integer product_detail_id) { this.product_detail_id = product_detail_id; }
    public Integer getProduct_id() { return product_id; }
    public void setProduct_id(Integer product_id) { this.product_id = product_id; }
    public Integer getSize_id() { return size_id; }
    public void setSize_id(Integer size_id) { this.size_id = size_id; }
    public Integer getColor_id() { return color_id; }
    public void setColor_id(Integer color_id) { this.color_id = color_id; }
    public Integer getStock_quantity() { return stock_quantity; }
    public void setStock_quantity(Integer stock_quantity) { this.stock_quantity = stock_quantity; }
    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
    public String getThumbnail_img_url() { return thumbnail_img_url; }
    public void setThumbnail_img_url(String thumbnail_img_url) { this.thumbnail_img_url = thumbnail_img_url; }
}