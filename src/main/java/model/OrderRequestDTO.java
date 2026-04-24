package model;

// DTO này dùng để nhận request từ Frontend
public class OrderRequestDTO {
    private Integer userId;
    private String receiverName;
    private String receiverPhone;
    private String shippingAddress;
    private String paymentMethod;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) { this.userId = userId; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public String getReceiverPhone() { return receiverPhone; }
    public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}