package com.zc.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisApplicationTests {

    @Autowired
    private RedisClientTemplate clientTemplate;

    @Test
    public void baseSet() {
        clientTemplate.getOps4Value().set("obj", new MyObject(2, "myObj2", "book"));
    }

    @Test
    public void baseTimeoutSet() {
        clientTemplate.getOps4Value().set("o2", "anyTime", 60);
    }

    @Test
    public void baseGet() {
        String test = clientTemplate.getOps4Value().get("o2");
        System.out.println(test);
    }

    @Test
    public void baseObjGet() {
        MyObject object = clientTemplate.getOps4Value().get("obj", MyObject.class);
//        String object = clientTemplate.getOps4Value().get("test", String.class);
        System.out.println(object.toString());
    }


}
