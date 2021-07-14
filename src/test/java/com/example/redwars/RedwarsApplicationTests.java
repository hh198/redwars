package com.example.redwars;

import com.example.redwars.service.RedService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RedwarsApplicationTests {

    @Test
    void contextLoads() {
    }
    @Autowired
    RedService redService;

}
