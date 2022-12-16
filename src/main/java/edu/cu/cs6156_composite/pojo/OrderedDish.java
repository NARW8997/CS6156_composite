package edu.cu.cs6156_composite.pojo;

import lombok.Data;

@Data
public class OrderedDish {
    /**
     * Primary key in ordered_dishes
     */
    private Integer dishId;

    /**
     * Foreign key link to order_profile
     */
    private Integer orderId;

    /**
     * what dish did the client ordered
     */
    private Integer orderedDishId;

}