package com.fixedit.fixeditstaff.Models;

/**
 * Created by AliAh on 20/06/2018.
 */

public class ServiceCountModel {
    SubServiceModel service;
    int quantity;
    long time;

    public ServiceCountModel() {
    }

    public ServiceCountModel(SubServiceModel service, int quantity, long time) {
        this.service = service;
        this.quantity = quantity;
        this.time = time;
    }

    public SubServiceModel getService() {
        return service;
    }

    public void setService(SubServiceModel service) {
        this.service = service;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
