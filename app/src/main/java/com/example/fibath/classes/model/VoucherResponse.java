package com.example.fibath.classes.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VoucherResponse {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("voucher")
    @Expose
    private Voucher voucher;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Voucher getVoucher() {
        return voucher;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }
}
