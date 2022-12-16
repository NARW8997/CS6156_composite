package edu.cu.cs6156_composite.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class Restaurant {
    private Integer restId;
    private String restName;
    private String restLocation;
    private String restSize;

}