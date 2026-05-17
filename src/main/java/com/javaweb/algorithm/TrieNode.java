package com.javaweb.algorithm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TrieNode {
    // Lưu các ký tự con
    public Map<Character, TrieNode> children;
    // Đánh dấu kết thúc một từ
    public boolean isEndOfWord;
    // Lưu danh sách ID của các sản phẩm có chứa từ khóa này (Dùng Set để tránh trùng lặp)
    public Set<Integer> productIds;

    public TrieNode() {
        children = new HashMap<>();
        isEndOfWord = false;
        productIds = new HashSet<>();
    }
}