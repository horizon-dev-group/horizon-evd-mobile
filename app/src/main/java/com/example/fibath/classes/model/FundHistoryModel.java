package com.example.fibath.classes.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FundHistoryModel {
    @SerializedName("sentQuantity")
    @Expose
    private Float sentQuantity;
    @SerializedName("sentBirrAmount")
    @Expose
    private Float sentBirrAmount;
    @SerializedName("receivedQuantity")
    @Expose
    private Float receivedQuantity;
    @SerializedName("receivedBirrAmount")
    @Expose
    private Float receivedBirrAmount;

    public Float getSentQuantity() {
        return sentQuantity;
    }

    public void setSentQuantity(Float sentQuantity) {
        this.sentQuantity = sentQuantity;
    }

    public Float getSentBirrAmount() {
        return sentBirrAmount;
    }

    public void setSentBirrAmount(Float sentBirrAmount) {
        this.sentBirrAmount = sentBirrAmount;
    }

    public Float getReceivedQuantity() {
        return receivedQuantity;
    }

    public void setReceivedQuantity(Float receivedQuantity) {
        this.receivedQuantity = receivedQuantity;
    }

    public Float getReceivedBirrAmount() {
        return receivedBirrAmount;
    }

    public void setReceivedBirrAmount(Float receivedBirrAmount) {
        this.receivedBirrAmount = receivedBirrAmount;
    }
}