package com.javaweb.repository.entity;
import java.util.Date;

public class OrderEntity {
    private Integer order_id;
    private Integer user_id;
    private Date order_date;
    private Integer total_money;
    private Integer discount_money;
    private Integer final_money;
    private String shipping_address;
    private String receiver_name;
    private String receiver_phone;
    private String payment_method;
    private String payment_status;
    private String shipping_status;

    // Getter và Setter
    public Integer getOrder_id() { return order_id; }
    public void setOrder_id(Integer order_id) { this.order_id = order_id; }
    public Integer getUser_id() { return user_id; }
    public void setUser_id(Integer user_id) { this.user_id = user_id; }
    public Date getOrder_date() { return order_date; }
    public void setOrder_date(Date order_date) { this.order_date = order_date; }
    public Integer getTotal_money() { return total_money; }
    public void setTotal_money(Integer total_money) { this.total_money = total_money; }
    public Integer getDiscount_money() { return discount_money; }
    public void setDiscount_money(Integer discount_money) { this.discount_money = discount_money; }
    public Integer getFinal_money() { return final_money; }
    public void setFinal_money(Integer final_money) { this.final_money = final_money; }
    public String getShipping_address() { return shipping_address; }
    public void setShipping_address(String shipping_address) { this.shipping_address = shipping_address; }
    public String getReceiver_name() { return receiver_name; }
    public void setReceiver_name(String receiver_name) { this.receiver_name = receiver_name; }
    public String getReceiver_phone() { return receiver_phone; }
    public void setReceiver_phone(String receiver_phone) { this.receiver_phone = receiver_phone; }
    public String getPayment_method() { return payment_method; }
    public void setPayment_method(String payment_method) { this.payment_method = payment_method; }
    public String getPayment_status() { return payment_status; }
    public void setPayment_status(String payment_status) { this.payment_status = payment_status; }
    public String getShipping_status() { return shipping_status; }
    public void setShipping_status(String shipping_status) { this.shipping_status = shipping_status; }
}