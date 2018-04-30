/*
 * Copyright (c) 2018. University of Applied Sciences and Arts Northwestern Switzerland FHNW.
 * All rights reserved.
 */

package ch.fhnw.digibp.api;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Named;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

@Named
public class FoodClassified implements JavaDelegate {

    @Autowired
    private Map<String, BlockingQueue<String>> queues;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        queues.get(execution.getProcessInstanceId()).put((String) execution.getVariable("foodType"));
    }
}
