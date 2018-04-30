/*
 * Copyright (c) 2018. University of Applied Sciences and Arts Northwestern Switzerland FHNW.
 * All rights reserved.
 */

package ch.fhnw.digibp.api;

import org.camunda.bpm.engine.ProcessEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(path = "/api/food/snap/v1")
public class FoodSnapEndpoint {

    @Autowired
    private Map<String, BlockingQueue<String>> queues;

    @Autowired
    private ProcessEngine processEngine;

    @PostMapping(path = "/order", consumes = "multipart/form-data",  produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public FoodSnapResponse postSnap(@ModelAttribute FoodSnapRequest foodSnapRequest) throws InterruptedException, IOException {
        Map<String, Object> processVars = new HashMap<>();
        processVars.put("customerName", foodSnapRequest.getCustomerName());
        processVars.put("customerAddress", foodSnapRequest.getCustomerAddress());
        processVars.put("customerEmail", foodSnapRequest.getCustomerEmail());
        processVars.put("imageFile", foodSnapRequest.getImageFile().getBytes());
        String processInstanceId = processEngine.getRuntimeService().startProcessInstanceByMessage("food-shot-service-ai-async_order-received", processVars).getProcessInstanceId();
        BlockingQueue<String> blockingQueue = new SynchronousQueue<>();
        queues.put(processInstanceId, blockingQueue);
        String result = (String) blockingQueue.poll(10, TimeUnit.SECONDS);
        if (result!=null) {
            return new FoodSnapResponse("Your food has been classified as: " + result);
        }
        return new FoodSnapResponse("Your food will be manually classified.");
    }

    private static class FoodSnapRequest {
        private String customerName;
        private String customerAddress;
        private String customerEmail;
        private MultipartFile imageFile;

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getCustomerAddress() {
            return customerAddress;
        }

        public void setCustomerAddress(String customerAddress) {
            this.customerAddress = customerAddress;
        }

        public String getCustomerEmail() {
            return customerEmail;
        }

        public void setCustomerEmail(String customerEmail) {
            this.customerEmail = customerEmail;
        }

        public MultipartFile getImageFile() {
            return imageFile;
        }

        public void setImageFile(MultipartFile imageFile) {
            this.imageFile = imageFile;
        }
    }

    private static class FoodSnapResponse {
        private String message;

        FoodSnapResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

}
