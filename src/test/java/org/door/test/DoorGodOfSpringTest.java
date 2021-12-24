package org.door.test;

import org.door.spring.ano.LimitingScanner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author: YuanChangShuai
 * @Date: 2021/12/23 17:36
 * @Description:
 **/
@SpringBootApplication
@LimitingScanner
public class DoorGodOfSpringTest {


    public static void main(String[] args) {
        SpringApplication.run(DoorGodOfSpringTest.class, args);
    }

}
