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
        if(queues.containsKey(execution.getProcessInstanceId())) {
            String foodType = (String) execution.getVariable("foodType");
            String foodClasses = (String) execution.getVariable("foodClasses");
            if(foodType==null) {
                queues.get(execution.getProcessInstanceId()).put("Your food will be manually classified. Watson classified it as: " + foodClasses);
            } else {
                queues.get(execution.getProcessInstanceId()).put("Your food has been classified as: " + foodType + ". Watson classified it as: " + foodClasses);
            }
            queues.remove(execution.getProcessInstanceId());
        }
    }
}
