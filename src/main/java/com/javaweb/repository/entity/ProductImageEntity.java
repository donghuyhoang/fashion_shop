package com.javaweb.repository.entity;

public class ProductImageEntity {
    private Integer image_id;
    private Integer product_id;
    private String image_url;
    private Boolean is_thumbnail;
    private Integer sort_order;

    // Getter và Setter
    public Integer getImage_id() { return image_id; }
    public void setImage_id(Integer image_id) { this.image_id = image_id; }
    public Integer getProduct_id() { return product_id; }
    public void setProduct_id(Integer product_id) { this.product_id = product_id; }
    public String getImage_url() { return image_url; }
    public void setImage_url(String image_url) { this.image_url = image_url; }
    public Boolean getIs_thumbnail() { return is_thumbnail; }
    public void setIs_thumbnail(Boolean is_thumbnail) { this.is_thumbnail = is_thumbnail; }
    public Integer getSort_order() { return sort_order; }
    public void setSort_order(Integer sort_order) { this.sort_order = sort_order; }
}