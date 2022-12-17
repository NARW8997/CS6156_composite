package edu.cu.cs6156_composite.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSONArray;
import edu.cu.cs6156_composite.controller.utils.R;
import edu.cu.cs6156_composite.pojo.*;
import edu.cu.cs6156_composite.service.CompositeService;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class CompositeServiceImpl implements CompositeService {

    @Value("https://e3pejg5go6.execute-api.us-east-1.amazonaws.com/")
    private String gateWayUrl;

    @Override
    public OrderProfileDetails selectByProfileId(Integer id, String token) {
        String profileUrl = gateWayUrl + "orderProfile/" + id;
        String resultStr = sendGet(profileUrl, token);
        R result = JSONObject.parseObject(resultStr).toJavaObject(R.class);
        String s = result.getData().toString();
        OrderProfileDetails profile =  JSONObject.parseObject(s).toJavaObject(OrderProfileDetails.class);
        Restaurant restaurant = selectRestaurantById(profile.getRestId());
        User user = selectUserById(profile.getAccountId(), token);
        List<Dish> dishes = selectAllDishByProfileId(profile.getOrderId());
        profile.setDishList(dishes);
        profile.setUser(user);
        profile.setRestaurant(restaurant);
        return profile;
    }

    /**
     * save orderProfileDetails into orderProfile and orderDish respectively
     * @param orderProfileDetails
     * @param token
     * @return
     */
    @Override
    public String saveOrderProfileDetails(OrderProfileDetails orderProfileDetails, String token, String accountId) {
        // set account id
        orderProfileDetails.setAccountId(accountId);
        // convert from orderProfileDetails to orderProfile obj
        OrderProfile orderProfile = toOrderProfile(orderProfileDetails);
        // send post request to create order Profile
        String s = saveOrderProfile(orderProfile);
        System.out.println("send post request to save orderProfile ---> " + s);
        // get orderId
        R r = JSON.parseObject(s, R.class);
        Integer orderId = (Integer) r.getData();
        // convert from dish list to orderedDishes list
        List<Dish> dishList = orderProfileDetails.getDishList();
        List<OrderedDish> orderedDishes = toOrderedDishes(dishList, orderId);
        // send post request to create batch orderedDishes
        String s1 = saveOrderedDish(orderedDishes);
        System.out.println("send post request to save batch orderDishes ---> " + s1);
        return s + s1;
    }

    @Override
    public String removeOrderProfileById(Integer pid) {
        String url = gateWayUrl + "orderProfile/" + pid;
        String s = sendDelete(url, null);
        return s;
    }


    @Override
    public String updateOrderProfile(OrderProfile orderProfile) {
        String url = gateWayUrl + "orderProfile";
        String jsonString = JSON.toJSONString(orderProfile);
        String s = sendPut(url, null, jsonString);
        return s;
    }

    /**
     * save OrderProfile into Order service by calling /insert in order service
     * @param orderProfile
     * @return
     */
    public String saveOrderProfile(OrderProfile orderProfile) {
        String url = gateWayUrl + "orderProfile";
        String jsonStr = JSON.toJSONString(orderProfile);
        String res = sendPostByJson(url, null, jsonStr);
        return res;
    }

    public OrderProfile toOrderProfile(OrderProfileDetails orderProfileDetails) {
        OrderProfile orderProfile = new OrderProfile();
        orderProfile.setOrderTime(orderProfileDetails.getOrderTime());
        orderProfile.setTotal(orderProfileDetails.getTotal());
        orderProfile.setAccountId(orderProfileDetails.getAccountId());
        orderProfile.setRestId(orderProfileDetails.getRestId());
        return orderProfile;
    }


    public List<OrderedDish> toOrderedDishes(List<Dish> dishList, Integer orderId) {
        List<OrderedDish> res = new ArrayList<>();
        // convert from dish to orderDish
        for (Dish dish : dishList) {
            OrderedDish orderedDish = new OrderedDish();
            // set order id matches the order Profile
            orderedDish.setOrderId(orderId);
            // set orderedDishId
            orderedDish.setOrderedDishId(dish.getDishId());
            res.add(orderedDish);
        }
        return res;
    }

    public String saveOrderedDish(Collection<OrderedDish> orderedDishCollection) {
        String url = gateWayUrl + "orderDish/" + "batch";
        String jsonStr = JSON.toJSONString(orderedDishCollection);
        String res = sendPostByJson(url, null, jsonStr);
        return res;
    }


    public User selectUserById(String uid, String token) {
        String userUrl = gateWayUrl + "users/" + uid;
        String result = sendGet(userUrl, token);
        return JSONObject.parseObject(result).toJavaObject(User.class);
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
            OrderedDish dish = JSONObject.parseObject(orderDish.toString()).toJavaObject(OrderedDish.class);
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
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Content-type", "application/json; charset=utf-8");
            if (token != null) {
                httpGet.setHeader("Authorization",token);
            }
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(httpGet);
            String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            System.out.println(body);
            return body;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String sendPostByEntity(String url, String token, List<NameValuePair> list) {
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        // create post request
        HttpPost httpPost = new HttpPost(url);
        // set entity for post request ---> list
        if (list != null) {
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(list, Consts.UTF_8);
            httpPost.setEntity(formEntity);
        }

        if (token != null) {
            httpPost.setHeader("Authorization",token);
        }
        CloseableHttpResponse response = null;
        try {
            response = closeableHttpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String toStrRes = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            EntityUtils.consume(entity);
            return toStrRes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (closeableHttpClient != null) {
                try {
                    closeableHttpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String sendPostByJson(String url, String token, String jsonObj) {
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        // create post request
        HttpPost httpPost = new HttpPost(url);
        if (token != null) {
            httpPost.setHeader("Authorization",token);
        }
        // set entity for post request ---> list
        StringEntity jsonEntity = new StringEntity(jsonObj, Consts.UTF_8);
        jsonEntity.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpPost.setEntity(jsonEntity);
        CloseableHttpResponse response = null;
        try {
            response = closeableHttpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String toStrRes = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            EntityUtils.consume(entity);
            return toStrRes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (closeableHttpClient != null) {
                try {
                    closeableHttpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String sendDelete(String url, String token) {
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        // create post request
        HttpDelete httpDelete = new HttpDelete(url);
        if (token != null) {
            httpDelete.setHeader("Authorization",token);
        }
        httpDelete.setHeader("Content-type", "application/json");
        httpDelete.setHeader("DataEncoding", "UTF-8");
        CloseableHttpResponse response = null;
        try {
            response = closeableHttpClient.execute(httpDelete);
            HttpEntity entity = response.getEntity();
            String toStrRes = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            EntityUtils.consume(entity);
            return toStrRes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (closeableHttpClient != null) {
                try {
                    closeableHttpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static String sendPut(String url, String token, String jsonObj) {
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        // create post request
        HttpPut httpPut = new HttpPut(url);
        if (token != null) {
            httpPut.setHeader("Authorization",token);
        }
        StringEntity jsonEntity = new StringEntity(jsonObj, Consts.UTF_8);
        jsonEntity.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpPut.setEntity(jsonEntity);
//        httpPut.setHeader("Content-type", "application/json");
        CloseableHttpResponse response = null;
        try {
            response = closeableHttpClient.execute(httpPut);
            HttpEntity entity = response.getEntity();
            String toStrRes = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            EntityUtils.consume(entity);
            return toStrRes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (closeableHttpClient != null) {
                try {
                    closeableHttpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
 }
