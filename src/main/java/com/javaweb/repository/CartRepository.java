package com.javaweb.repository;

import model.CartItemResponseDTO;
import java.util.List;

public interface CartRepository {
    void addToCart(Integer userId, Integer productDetailId, Integer quantity);
    Integer getCartCount(Integer userId);
    List<CartItemResponseDTO> getCartDetails(Integer userId);
    void removeCartItem(Integer userId, Integer productDetailId);
}