package com.team6project.cavallo_mall;

import com.team6project.cavallo_mall.sequence.RedisSequenceImpl;
import com.team6project.cavallo_mall.util.SeqUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.TimeZone;

@SpringBootApplication
@MapperScan(basePackages = "com.team6project.cavallo_mall.dao")
public class CavalloMallApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(CavalloMallApplication.class, args);
    }

    @Bean
    public SeqUtil seqUtil() {
        return new SeqUtil();
    }
    @Bean
    public RedisSequenceImpl redisSequence() {
        return new RedisSequenceImpl();
    }

}

//test comment