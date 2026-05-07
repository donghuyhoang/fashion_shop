package com.javaweb.api;

import com.javaweb.service.ColorService;
import model.ItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;


@CrossOrigin
@RestController
@RequestMapping("/api/colors")
public class ColorAPI {

    @Autowired
        private ColorService colorService;

    @GetMapping
    public List<ItemDTO> getColors() {
        return colorService.findAll();
    }
}