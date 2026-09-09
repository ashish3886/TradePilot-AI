package com.tradepilot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@EnableScheduling
@SpringBootApplication
public class TradePilotBackendApplication {

    public static void main(String[] args) {
        System.out.println("JVM TimeZone = " + TimeZone.getDefault().getID());
        SpringApplication.run(TradePilotBackendApplication.class, args);
    }

}