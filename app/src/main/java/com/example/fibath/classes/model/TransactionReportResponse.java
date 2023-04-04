package com.example.fibath.classes.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransactionReportResponse {
    @SerializedName("_id")
    @Expose
    private Object id;
    @SerializedName("transaction_amount")
    @Expose
    private Double transactionAmount;

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public Double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(Double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }
}