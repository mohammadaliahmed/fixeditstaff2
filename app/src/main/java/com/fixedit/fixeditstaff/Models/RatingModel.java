package com.fixedit.fixeditstaff.Models;

public class RatingModel {
    String id,comments,ratedBy,ratingTo,orderId,serviceName;
    long time;
    float rating;
    String ratedByName;

    public RatingModel(String id, String comments, String ratedBy, String ratingTo, String ratedByName,
                       String orderId, String serviceName, long time, float rating) {
        this.id = id;
        this.comments = comments;
        this.ratedBy = ratedBy;
        this.orderId = orderId;
        this.ratingTo = ratingTo;
        this.serviceName = serviceName;
        this.time = time;
        this.rating = rating;
        this.ratedByName = ratedByName;
    }

    public RatingModel() {
    }

    public String getRatingTo() {
        return ratingTo;
    }

    public void setRatingTo(String ratingTo) {
        this.ratingTo = ratingTo;
    }

    public String getRatedByName() {
        return ratedByName;
    }

    public void setRatedByName(String ratedByName) {
        this.ratedByName = ratedByName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getRatedBy() {
        return ratedBy;
    }

    public void setRatedBy(String ratedBy) {
        this.ratedBy = ratedBy;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
