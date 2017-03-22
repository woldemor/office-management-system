package com.netcracker.model.dto;


import com.netcracker.model.entity.FrontendNotification;
import com.netcracker.model.entity.Request;

import java.sql.Timestamp;

public class FrontendNotificationDTO {

    private String subject = "";
    private Timestamp creationTime;
    private Request request;


    public FrontendNotificationDTO() {
    }

    public FrontendNotificationDTO(String subject, Timestamp creationTime, Request request) {
        this.subject = subject;
        this.creationTime = creationTime;
        this.request = request;
    }


    public FrontendNotificationDTO(FrontendNotification frontendNotification){
        this.subject = frontendNotification.getSubject();
        this.creationTime = frontendNotification.getCreationTime();
        this.request = frontendNotification.getRequest();
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Timestamp getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Timestamp creationTime) {
        this.creationTime = creationTime;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }
    

}
