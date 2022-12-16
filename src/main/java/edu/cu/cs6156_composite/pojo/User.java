package edu.cu.cs6156_composite.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String accountID;
    private String firstName;
    private String lastName;
    private String email;
    private String middleName;
}
