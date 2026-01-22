package com.javaweb.api;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Beans.*;

@RestController
public class BuildingAPI {
    @GetMapping(value = "/api/building/")
    public Object getBuilding(@RequestParam(value = "name")	{
    	return null;
    }
    @GetMapping(value = "/api/building/")
    public Object getBuilding1(@RequestBody BuildingDTO building) {
    }
}
