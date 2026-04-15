package com.javaweb.api;

import com.javaweb.service.SizeService;
import model.ItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/sizes")
public class SizeAPI {

    @Autowired
    private SizeService sizeService;

    @GetMapping
    public List<ItemDTO> getSizes() {
        return sizeService.findAll();
    }
}