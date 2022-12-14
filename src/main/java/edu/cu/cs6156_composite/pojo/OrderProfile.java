package edu.cu.cs6156_composite.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class OrderProfile {
    private Integer orderId;

    private String accountId;

    private Date orderTime;

    private Integer total;

    private Integer restId;
}