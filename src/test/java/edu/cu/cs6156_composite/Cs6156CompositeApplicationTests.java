package edu.cu.cs6156_composite;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import edu.cu.cs6156_composite.pojo.Dish;
import edu.cu.cs6156_composite.pojo.OrderProfileDetails;
import edu.cu.cs6156_composite.service.CompositeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@SpringBootTest
class Cs6156CompositeApplicationTests {
    @Autowired
    private CompositeService compositeService;

    @Test
    void test02() {
    }
}
