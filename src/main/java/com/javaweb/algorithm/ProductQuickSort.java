package com.javaweb.algorithm;

import model.ProductDTO;
import java.util.List;

public class ProductQuickSort {

    /**
     * Hàm gọi công khai để sắp xếp danh sách sản phẩm
     * @param list Danh sách sản phẩm cần sắp xếp
     * @param ascending true: Sắp xếp tăng dần, false: Sắp xếp giảm dần
     */
    public static void sort(List<ProductDTO> list, boolean ascending) {
        if (list == null || list.isEmpty()) return;
        quickSort(list, 0, list.size() - 1, ascending);
    }

    private static void quickSort(List<ProductDTO> list, int low, int high, boolean ascending) {
        if (low < high) {
            // pi là chỉ số của phần tử chốt đã nằm đúng vị trí
            int pi = partition(list, low, high, ascending);

            // Gọi đệ quy sắp xếp 2 nửa
            quickSort(list, low, pi - 1, ascending);
            quickSort(list, pi + 1, high, ascending);
        }
    }

    private static int partition(List<ProductDTO> list, int low, int high, boolean ascending) {
        // Chọn phần tử chốt (pivot) là phần tử cuối cùng
        ProductDTO pivot = list.get(high);
        int i = (low - 1); // Chỉ số của phần tử nhỏ hơn

        for (int j = low; j < high; j++) {
            boolean condition;
            // Ở đây ta cài đặt xếp hạng sản phẩm theo GIÁ TIỀN (Price)
            Double priceJ = list.get(j).getPrice() != null ? list.get(j).getPrice() : 0.0;
            Double pricePivot = pivot.getPrice() != null ? pivot.getPrice() : 0.0;

            if (ascending) {
                condition = priceJ <= pricePivot; // Tăng dần
            } else {
                condition = priceJ >= pricePivot; // Giảm dần
            }

            if (condition) {
                i++;
                swap(list, i, j); // Đổi chỗ
            }
        }
        // Đưa phần tử chốt vào đúng vị trí (đứng giữa 2 nửa)
        swap(list, i + 1, high);
        return i + 1;
    }

    // Hàm hoán vị 2 vị trí trong List
    private static void swap(List<ProductDTO> list, int i, int j) {
        ProductDTO temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }
}