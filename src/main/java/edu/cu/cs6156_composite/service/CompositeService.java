package edu.cu.cs6156_composite.service;

import edu.cu.cs6156_composite.pojo.OrderProfileDetails;


public interface CompositeService {

    OrderProfileDetails selectByProfileId(Integer id, String token);

    String saveOrderProfileDetails(OrderProfileDetails orderProfileDetails, String token, String accountId);
}
