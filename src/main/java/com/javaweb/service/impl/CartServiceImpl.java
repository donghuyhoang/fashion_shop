package com.javaweb.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.javaweb.repository.CartRepository;
import com.javaweb.service.CartService;
import model.CartRequestDTO;
import model.CartItemResponseDTO;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    
    @Autowired
    private CartRepository cartRepository;

    @Override
    public void addToCart(CartRequestDTO cartRequestDTO) {
        cartRepository.addToCart(
            cartRequestDTO.getUserId(), 
            cartRequestDTO.getProductDetailId(), 
            cartRequestDTO.getQuantity());
    }

    @Override
    public Integer getCartCount(Integer userId) {
        return cartRepository.getCartCount(userId);
    }

    @Override
    public List<CartItemResponseDTO> getCartByUser(Integer userId) {
        return cartRepository.getCartDetails(userId);
    }

    @Override
    public void removeCartItem(Integer userId, Integer productDetailId) {
        cartRepository.removeCartItem(userId, productDetailId);
    }
}