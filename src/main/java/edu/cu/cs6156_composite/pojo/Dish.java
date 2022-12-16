package edu.cu.cs6156_composite.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dish {
    private Integer dishId;
    private String dishName;
    private String flavor;
    private String dishDescription;
    private String serveSize;
    private Integer restId;
}
