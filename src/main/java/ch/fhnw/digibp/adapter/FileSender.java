/*
 * Copyright (c) 2018. University of Applied Sciences and Arts Northwestern Switzerland FHNW.
 * All rights reserved.
 */

package ch.fhnw.digibp.adapter;

import org.apache.commons.io.IOUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.web.client.RestTemplate;


import javax.inject.Named;
import java.io.*;

@Named
public class FileSender {

    public String sendBinary(DelegateExecution execution, String url, String fileName){
        RestTemplate restTemplate = new RestTemplate();
        OutputStream outputStream = new ByteArrayOutputStream();
        try {
            ByteArrayInputStream byteArrayInputStream = null;
            Object file = execution.getVariable(fileName);
            if(!(file instanceof ByteArrayInputStream)) {
                byteArrayInputStream = new ByteArrayInputStream((byte[]) file);
            } else{
                byteArrayInputStream = (ByteArrayInputStream) file;
            }
            IOUtils.copy(byteArrayInputStream, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return restTemplate.postForObject(url, ((ByteArrayOutputStream) outputStream).toByteArray(), String.class);
    }
}