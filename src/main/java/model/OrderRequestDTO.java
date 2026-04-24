package model;
import java.util.List;

public class OrderRequestDTO {
    private Integer userId;
    private Integer totalMoney;
    private String shippingAddress;
    private String receiverName;
    private String receiverPhone;
    private String paymentMethod;
    private List<CartItemDTO> items;

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public Integer getTotalMoney() { return totalMoney; }
    public void setTotalMoney(Integer totalMoney) { this.totalMoney = totalMoney; }
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
    public String getReceiverPhone() { return receiverPhone; }
    public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public List<CartItemDTO> getItems() { return items; }
    public void setItems(List<CartItemDTO> items) { this.items = items; }
}