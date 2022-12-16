package edu.cu.cs6156_composite.service;

import edu.cu.cs6156_composite.pojo.OrderProfile;


public interface CompositeService {

    OrderProfile selectByProfileId(Integer id, String token);
}
