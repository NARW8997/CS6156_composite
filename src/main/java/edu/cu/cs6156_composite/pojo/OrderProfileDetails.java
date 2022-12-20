package edu.cu.cs6156_composite.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class OrderProfileDetails {
    private Integer orderId;
    private String accountId;
    private User user;

    private Date orderTime;
    private Integer total;
    private Integer restId;
    private Restaurant restaurant;
    private List<Dish> dishList;
}