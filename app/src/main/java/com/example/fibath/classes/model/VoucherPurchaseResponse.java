package com.example.fibath.classes.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VoucherPurchaseResponse {
    @SerializedName("voucher")
    @Expose
    private List<Voucher> voucher = null;
    @SerializedName("faceValue")
    @Expose
    private String faceValue;
    @SerializedName("remains")
    @Expose
    private Integer remains;
    @SerializedName("bulk_id")
    @Expose
    private String bulk_id;


    public List<Voucher> getVoucher() {
        return voucher;
    }

    public void setVoucher(List<Voucher> voucher) {
        this.voucher = voucher;
    }

    public String getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(String faceValue) {
        this.faceValue = faceValue;
    }

    public Integer getRemains() {
        return remains;
    }

    public void setRemains(Integer remains) {
        this.remains = remains;
    }

    public String getBulk_id() {
        return bulk_id;
    }

    public void setBulk_id(String bulk_id) {
        this.bulk_id = bulk_id;
    }
}
