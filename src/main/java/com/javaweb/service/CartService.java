package com.javaweb.service;

import model.CartRequestDTO;
import model.CartItemResponseDTO;
import java.util.List;

public interface CartService {
    void addToCart(CartRequestDTO cartRequestDTO);
    Integer getCartCount(Integer userId);
    List<CartItemResponseDTO> getCartByUser(Integer userId);
    void removeCartItem(Integer userId, Integer productDetailId);
}