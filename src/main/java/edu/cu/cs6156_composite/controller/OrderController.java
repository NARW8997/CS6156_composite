package edu.cu.cs6156_composite.controller;

import edu.cu.cs6156_composite.controller.utils.R;
import edu.cu.cs6156_composite.pojo.OrderProfile;
import edu.cu.cs6156_composite.service.CompositeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private CompositeService compositeService;

    @GetMapping("/{id}")
    public R selectProfileById(@PathVariable Integer id, @RequestHeader("Authorization") String token) {
        OrderProfile profile = compositeService.selectByProfileId(id, token);
        return new R(true, profile);
    }

}
