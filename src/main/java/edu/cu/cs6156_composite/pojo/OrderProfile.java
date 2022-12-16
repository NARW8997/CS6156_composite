package edu.cu.cs6156_composite.pojo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class OrderProfile {
    private Integer orderId;
    private String accountId;
    private User user;
    private Date orderTime;
    private Integer total;
    private Integer restId;

    private Restaurant restaurant;
    private List<Dish> dishList;
}