package com.javaweb.repository.entity;

public class OrderDetailEntity {
    private Integer order_detail_id;
    private Integer order_id;
    private Integer product_detail_id;
    private Integer quantity;
    private Integer unit_price;
    private Integer total_price;

    // Getter và Setter
    public Integer getOrder_detail_id() { return order_detail_id; }
    public void setOrder_detail_id(Integer order_detail_id) { this.order_detail_id = order_detail_id; }
    public Integer getOrder_id() { return order_id; }
    public void setOrder_id(Integer order_id) { this.order_id = order_id; }
    public Integer getProduct_detail_id() { return product_detail_id; }
    public void setProduct_detail_id(Integer product_detail_id) { this.product_detail_id = product_detail_id; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Integer getUnit_price() { return unit_price; }
    public void setUnit_price(Integer unit_price) { this.unit_price = unit_price; }
    public Integer getTotal_price() { return total_price; }
    public void setTotal_price(Integer total_price) { this.total_price = total_price; }
}