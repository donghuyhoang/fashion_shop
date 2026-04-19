package model;
import java.util.Date;

public class OrderDTO {
    private Integer orderId;
    private Integer userId;
    private Date orderDate;
    private Integer totalMoney;
    private Integer discountMoney;
    private Integer finalMoney;
    private String shippingAddress;
    private String receiverName;
    private String receiverPhone;
    private String paymentMethod;
    private String paymentStatus;
    private String shippingStatus;

    // Getter và Setter
    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }
    public Integer getTotalMoney() { return totalMoney; }
    public void setTotalMoney(Integer totalMoney) { this.totalMoney = totalMoney; }
    public Integer getDiscountMoney() { return discountMoney; }
    public void setDiscountMoney(Integer discountMoney) { this.discountMoney = discountMoney; }
    public Integer getFinalMoney() { return finalMoney; }
    public void setFinalMoney(Integer finalMoney) { this.finalMoney = finalMoney; }
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
    public String getReceiverPhone() { return receiverPhone; }
    public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getShippingStatus() { return shippingStatus; }
    public void setShippingStatus(String shippingStatus) { this.shippingStatus = shippingStatus; }
}