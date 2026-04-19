package com.javaweb.repository.entity;
import java.util.Date;

public class ProductReviewEntity {
    private Integer review_id;
    private Integer user_id;
    private String user_full_name; // Field mở rộng từ bảng users
    private Integer product_id;
    private Integer order_id;
    private Integer rating;
    private String comment;
    private Date created_at;

    // Getter và Setter
    public Integer getReview_id() { return review_id; }
    public void setReview_id(Integer review_id) { this.review_id = review_id; }
    public Integer getUser_id() { return user_id; }
    public void setUser_id(Integer user_id) { this.user_id = user_id; }
    public String getUser_full_name() { return user_full_name; }
    public void setUser_full_name(String user_full_name) { this.user_full_name = user_full_name; }
    public Integer getProduct_id() { return product_id; }
    public void setProduct_id(Integer product_id) { this.product_id = product_id; }
    public Integer getOrder_id() { return order_id; }
    public void setOrder_id(Integer order_id) { this.order_id = order_id; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Date getCreated_at() { return created_at; }
    public void setCreated_at(Date created_at) { this.created_at = created_at; }
}