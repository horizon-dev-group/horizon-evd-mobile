package com.example.fibath.classes.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TotalCardMoney {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("total_sold_amount")
    @Expose
    private Float totalSoldAmount;
    @SerializedName("total_commission_amount")
    @Expose
    private Float totalCommissionAmount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Float getTotalSoldAmount() {
        return totalSoldAmount;
    }

    public void setTotalSoldAmount(Float totalSoldAmount) {
        this.totalSoldAmount = totalSoldAmount;
    }

    public Float getTotalCommissionAmount() {
        return totalCommissionAmount;
    }

    public void setTotalCommissionAmount(Float totalCommissionAmount) {
        this.totalCommissionAmount = totalCommissionAmount;
    }
}