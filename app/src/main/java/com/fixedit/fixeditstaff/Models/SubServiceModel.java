package com.fixedit.fixeditstaff.Models;

public class SubServiceModel {
    String id,name;
    boolean active;
    int timeHour,timeMin;
    String parentService;
    int min,max;
    String measureUnit;

    public SubServiceModel(String id, String name, boolean active, int timeHour, int timeMin, String parentService, int min, int max, String measureUnit) {
        this.id = id;
        this.name = name;
        this.active = active;
        this.timeHour = timeHour;
        this.timeMin = timeMin;
        this.parentService = parentService;
        this.min = min;
        this.max = max;
        this.measureUnit = measureUnit;
    }

    public String getMeasureUnit() {
        return measureUnit;
    }

    public void setMeasureUnit(String measureUnit) {
        this.measureUnit = measureUnit;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getTimeHour() {
        return timeHour;
    }

    public void setTimeHour(int timeHour) {
        this.timeHour = timeHour;
    }

    public int getTimeMin() {
        return timeMin;
    }

    public void setTimeMin(int timeMin) {
        this.timeMin = timeMin;
    }

    public String getParentService() {
        return parentService;
    }

    public void setParentService(String parentService) {
        this.parentService = parentService;
    }

    public SubServiceModel() {
    }

}
