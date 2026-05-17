package com.javaweb.algorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Trie {
    private final TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    // Hàm chèn một từ vào Trie
    public void insert(String word, Integer productId) {
        if (word == null || word.isEmpty()) return;
        
        // Chuyển về chữ thường để tìm kiếm không phân biệt hoa thường
        word = word.toLowerCase();
        TrieNode current = root;

        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            current.children.putIfAbsent(ch, new TrieNode());
            current = current.children.get(ch);
            // Lưu ID sản phẩm vào node này để khi người dùng gõ dở dang (ví dụ: gõ "áo t") vẫn tìm ra "áo thun"
            current.productIds.add(productId); 
        }
        current.isEndOfWord = true;
    }

    // Hàm tìm kiếm trả về danh sách Product ID dựa trên prefix (tiền tố)
    public List<Integer> searchPrefix(String prefix) {
        if (prefix == null || prefix.isEmpty()) return new ArrayList<>();
        
        prefix = prefix.toLowerCase();
        TrieNode current = root;

        for (int i = 0; i < prefix.length(); i++) {
            char ch = prefix.charAt(i);
            TrieNode node = current.children.get(ch);
            // Nếu gõ một ký tự không tồn tại trong cây -> Không tìm thấy sản phẩm nào
            if (node == null) {
                return new ArrayList<>();
            }
            current = node;
        }
        
        // Trả về danh sách ID sản phẩm nằm ở node cuối cùng của tiền tố
        return new ArrayList<>(current.productIds);
    }
}