package com.example.fibath.classes.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SoldVoucherResponse {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("voucher_id")
    @Expose
    private Voucher voucher = null;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("userParentId")
    @Expose
    private String userParentId;
    @SerializedName("voucher_status")
    @Expose
    private String voucherStatus;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("commission")
    @Expose
    private String commission;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("print_type")
    @Expose
    private String printType;
    @SerializedName("bulk_id")
    @Expose
    private String bulkId;
    @SerializedName("faceValue")
    @Expose
    private String faceValue;
    @SerializedName("downloaded")
    @Expose
    private String downloaded;
    @SerializedName("reDownloaded")
    @Expose
    private int reDownloaded;
    @SerializedName("__v")
    @Expose
    private Integer v;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Voucher getVoucherId() {
        return voucher;
    }

    public void setVoucherId(Voucher voucherId) {
        this.voucher = voucherId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserParentId() {
        return userParentId;
    }

    public void setUserParentId(String userParentId) {
        this.userParentId = userParentId;
    }

    public String getVoucherStatus() {
        return voucherStatus;
    }

    public void setVoucherStatus(String voucherStatus) {
        this.voucherStatus = voucherStatus;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrintType() {
        return printType;
    }

    public void setPrintType(String printType) {
        this.printType = printType;
    }

    public String getBulkId() {
        return bulkId;
    }

    public void setBulkId(String bulkId) {
        this.bulkId = bulkId;
    }

    public String getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(String faceValue) {
        this.faceValue = faceValue;
    }

    public String getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(String downloaded) {
        this.downloaded = downloaded;
    }

    public int getReDownloaded() {
        return reDownloaded;
    }

    public void setReDownloaded(int reDownloaded) {
        this.reDownloaded = reDownloaded;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
