package edu.cu.cs6156_composite;
import edu.cu.cs6156_composite.pojo.OrderProfile;
import edu.cu.cs6156_composite.service.CompositeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class Cs6156CompositeApplicationTests {
    @Autowired
    private CompositeService compositeService;
    @Test
    void test02() {
        OrderProfile profile = new OrderProfile();
        profile.setOrderId(26);
        profile.setRestId(250);
        System.out.println(compositeService.updateOrderProfile(profile));
    }
}
