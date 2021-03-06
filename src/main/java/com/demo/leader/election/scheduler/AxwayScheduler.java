package com.demo.leader.election.scheduler;

import com.demo.leader.election.statemachine.LeaderEvent;
import com.demo.leader.election.statemachine.LeaderState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AxwayScheduler {

    @Autowired
    @Qualifier("leaderStateMachine")
    private StateMachine<LeaderState, LeaderEvent> stateMachine;

    @Scheduled(fixedDelay = 2000)
    public void axwayQueueListener(){
        LeaderState state = stateMachine.getState().getId();
        if(state.compareTo(LeaderState.LEADER) == 0){
            log.info("I am the Leader.....");
        }
    }
}
