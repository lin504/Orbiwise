package com.lony.orbiwise;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Orbiwise 在线旅游平台启动类
 *
 * @author lin504
 */
@SpringBootApplication
@MapperScan("com.lony.orbiwise.mapper")
public class OrbiwiseApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrbiwiseApplication.class, args);
    }

}
