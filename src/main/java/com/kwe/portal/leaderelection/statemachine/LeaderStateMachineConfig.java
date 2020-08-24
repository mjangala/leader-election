package com.kwe.portal.leaderelection.statemachine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;

import java.util.EnumSet;

@Configuration
@EnableStateMachine(name = "leaderStateMachine")
@EnableScheduling
@Slf4j
public class LeaderStateMachineConfig extends EnumStateMachineConfigurerAdapter<LeaderState, LeaderEvent> {


    @Override
    public void configure(StateMachineConfigurationConfigurer<LeaderState, LeaderEvent> config) throws Exception {
        config.withConfiguration()
                .autoStartup(true)
                .listener(listener());
    }

    @Override
    public void configure(StateMachineStateConfigurer<LeaderState, LeaderEvent> states) throws Exception {
        states.withStates()
                .initial(LeaderState.NOT_A_LEADER)
                .states(EnumSet.allOf(LeaderState.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<LeaderState, LeaderEvent> transitions) throws Exception {
        transitions.withExternal()
                .source(LeaderState.NOT_A_LEADER).target(LeaderState.LEADER).event(LeaderEvent.ELECTED_AS_LEADER)
                .and()
                .withExternal()
                .source(LeaderState.LEADER).target(LeaderState.NOT_A_LEADER).event(LeaderEvent.REMOVED_AS_LEADER);
/*                .and()
                .withExternal()
                .source(States.SUSPENDED).target(States.AVAILABLE).event(Events.AVAILABLE)
                .and()
                .withExternal()
                .source(States.AVAILABLE).target(States.SUSPENDED).event(Events.NOT_AVAILABLE)
                .and()
                .withExternal()
                .source(States.AVAILABLE).target(States.DRAINING).event(Events.FORCE_STOP);*/
    }

    private StateMachineListener<LeaderState, LeaderEvent> listener() {
        return new StateMachineListenerAdapter<LeaderState, LeaderEvent>() {
            @Override
            public void stateChanged(org.springframework.statemachine.state.State from, org.springframework.statemachine.state.State to) {
                if (from != null) {
                    log.info("Leader Election State change to " + from.getId() + " = " + to.getId());
                } else {
                    log.info("Leader Election Initial State is " + to.getId());
                }
            }
        };
    }
}
