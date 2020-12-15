package com.fixedit.fixeditstaff.Models;

public class CouponModel {
   String couponId,couponName,couponCode;
   int discount;
   long time;
   boolean active;
   long couponStartTime,couponEndTime;
    public CouponModel() {
    }

    public CouponModel(String couponId, String couponName, String couponCode, int discount, long time, boolean active, long couponStartTime, long couponEndTime) {
        this.couponId = couponId;
        this.couponName = couponName;
        this.couponCode = couponCode;
        this.discount = discount;
        this.time = time;
        this.active = active;
        this.couponStartTime = couponStartTime;
        this.couponEndTime = couponEndTime;
    }


    public long getTime() {
        return time;
    }




    public long getCouponStartTime() {
        return couponStartTime;
    }

    public void setCouponStartTime(long couponStartTime) {
        this.couponStartTime = couponStartTime;
    }

    public long getCouponEndTime() {
        return couponEndTime;
    }

    public void setCouponEndTime(long couponEndTime) {
        this.couponEndTime = couponEndTime;
    }
    public void setTime(long time) {
        this.time = time;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }
}
