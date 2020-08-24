package com.demo.leader.election;

import com.demo.leader.election.statemachine.LeaderEvent;
import com.demo.leader.election.statemachine.LeaderState;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ApplicationSuccessListener implements ApplicationListener<ApplicationReadyEvent> {


    @Autowired
    private WebServerInitializer webServerInitializer;

    @Autowired
    @Qualifier("leaderStateMachine")
    private StateMachine<LeaderState, LeaderEvent> stateMachine;

    private static final String PATH = "/services/leader-election";

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("Application is ready hence checking for leader");
        checkIfLeader();
    }

    private void checkIfLeader() {
        CuratorFramework client;
        LeaderLatch example;
        String zkConnString = "127.0.0.1:2181";
        try {
            client = CuratorFrameworkFactory.newClient(zkConnString, new ExponentialBackoffRetry(1000, 3));
            example = new LeaderLatch(client, PATH);
            client.start();
            example.start();
            example.await();
            if (example.hasLeadership()) {
                log.info("I am the leader: " + webServerInitializer.getPort());
                stateMachine.sendEvent(LeaderEvent.ELECTED_AS_LEADER);
            } else {
                log.info("I am not the leader");
                stateMachine.sendEvent(LeaderEvent.REMOVED_AS_LEADER);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
