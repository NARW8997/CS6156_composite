package edu.cu.cs6156_composite.controller;

import edu.cu.cs6156_composite.controller.utils.R;
import edu.cu.cs6156_composite.pojo.OrderProfileDetails;
import edu.cu.cs6156_composite.service.CompositeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private CompositeService compositeService;

    @GetMapping("/{id}")
    public R selectProfileById(@PathVariable Integer id, @RequestHeader("Authorization") String token) {
        OrderProfileDetails profile = compositeService.selectByProfileId(id, token);
        return new R(true, profile);
    }

    @PostMapping("/{accountId}")
    public R saveProfile(@RequestBody OrderProfileDetails orderProfileDetails, @PathVariable String accountId) {
        String s = compositeService.saveOrderProfileDetails(orderProfileDetails, null, accountId);
        return new R(true, s);
    }

}
