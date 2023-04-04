package com.example.fibath.classes.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VoucherTransaction {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("vouchers")
    @Expose
    private List<VoucherTransactionResponse> vouchers = null;

    @SerializedName("max")
    @Expose
    private String max;
    @SerializedName("min")
    @Expose
    private String min;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<VoucherTransactionResponse> getVouchers() {
        return vouchers;
    }

    public void setVouchers(List<VoucherTransactionResponse> vouchers) {
        this.vouchers = vouchers;
    }


    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }


}
