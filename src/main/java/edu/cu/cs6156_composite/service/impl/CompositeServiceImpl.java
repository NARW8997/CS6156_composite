package edu.cu.cs6156_composite.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSONArray;
import edu.cu.cs6156_composite.controller.utils.R;
import edu.cu.cs6156_composite.pojo.*;
import edu.cu.cs6156_composite.service.CompositeService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class CompositeServiceImpl implements CompositeService {

    @Value("https://e3pejg5go6.execute-api.us-east-1.amazonaws.com/")
    private String gateWayUrl;

    @Override
    public OrderProfile selectByProfileId(Integer id, String token) {
        String profileUrl = gateWayUrl + "orderProfile/" + id;
        String resultStr = sendGet(profileUrl, token);
        R result = JSONObject.parseObject(resultStr).toJavaObject(R.class);
        String s = result.getData().toString();
        OrderProfile profile =  JSONObject.parseObject(s).toJavaObject(OrderProfile.class);
        Restaurant restaurant = selectRestaurantById(profile.getRestId());
        User user = selectUserById(profile.getAccountId(), token);
        List<Dish> dishes = selectAllDishByProfileId(profile.getOrderId());
        profile.setDishList(dishes);
        profile.setUser(user);
        profile.setRestaurant(restaurant);
        return profile;
    }

    public User selectUserById(String uid, String token) {
        String userUrl = gateWayUrl + "users/" + uid;
        String result = sendGet(userUrl, token);
        return JSONObject.parseObject(result).toJavaObject(User.class);
    }

    private HttpHeaders headers(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_UTF8_VALUE);
        headers.setContentType(MediaType.valueOf("application/json"));
        headers.set(HttpHeaders.AUTHORIZATION, token);
        System.out.println(headers);
        return headers;
    }

    public Restaurant selectRestaurantById(Integer rid) {
        String restUrl = gateWayUrl + "restaurants/" + rid;
        String result = sendGet(restUrl, null);
        return JSONObject.parseObject(result).toJavaObject(Restaurant.class);
    }

    public Dish selectDishById(Integer did) {
        String restUrl = gateWayUrl + "dishes/" + did;
        String result = sendGet(restUrl, null);
        return JSONObject.parseObject(result).toJavaObject(Dish.class);
    }

    public List<OrderedDish> selectOrderedDishByProfileId(Integer pid) {
        String restUrl = gateWayUrl + "orderDish/Profile/" + pid;
        String resultStr = sendGet(restUrl, null);
        R result = JSONObject.parseObject(resultStr).toJavaObject(R.class);
        JSONArray array = (JSONArray) result.getData();
        List<OrderedDish> res = new ArrayList<>();
        for (Object orderDish : array) {
            OrderedDish dish =  JSONObject.parseObject(orderDish.toString()).toJavaObject(OrderedDish.class);
            res.add(dish);
        }
        return res;
    }

    public List<Dish> selectAllDishByProfileId(Integer pid) {
        List<OrderedDish> orderedDishes = selectOrderedDishByProfileId(pid);
        List<Dish> res = new ArrayList<>();
        for (OrderedDish orderedDish : orderedDishes) {
            Dish dish = selectDishById(orderedDish.getOrderedDishId());
            res.add(dish);
        }
        return res;
    }

    public static String sendGet(String url,String token) {
        try {
            HttpGet HttpGet = new HttpGet(url);
            HttpGet.setHeader("Content-type", "application/json; charset=utf-8");
            if (token != null) {
                HttpGet.setHeader("Authorization",token);
            }
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(HttpGet);
            String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            System.out.println(body);
            return body;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
 }
