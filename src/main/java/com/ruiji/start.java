package com.ruiji;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@Slf4j
@SpringBootApplication
@ServletComponentScan
public class start {
    public static void main(String[] args) {
        SpringApplication.run(start.class,args);
        log.info("项目启动成功。。。");
    }
}
