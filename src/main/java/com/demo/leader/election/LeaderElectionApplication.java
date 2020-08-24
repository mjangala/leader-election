package com.demo.leader.election;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication

public class LeaderElectionApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeaderElectionApplication.class, args);
    }
}
