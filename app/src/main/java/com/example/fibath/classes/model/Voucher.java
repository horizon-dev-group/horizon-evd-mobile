package com.example.fibath.classes.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Voucher {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("voucher_amount")
    @Expose
    private String voucherAmount;
    @SerializedName("voucher_batch_number")
    @Expose
    private String voucherBatchNumber;
    @SerializedName("voucher_serial_number")
    @Expose
    private String voucherSerialNumber;
    @SerializedName("voucher_number")
    @Expose
    private String voucherNumber;
    @SerializedName("voucher_expiry_date")
    @Expose
    private String voucherExpiryDate;
    @SerializedName("voucher_status")
    @Expose
    private String voucherStatus;
    @SerializedName("__v")
    @Expose
    private Integer v;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;





    @SerializedName("voucherRowNumber")
    @Expose
    private int voucherRowNumber;

    @SerializedName("bulkId")
    @Expose
    private String bulkId;

    public String getBulkId() {
        return bulkId;
    }

    public void setBulkId(String bulkId) {
        this.bulkId = bulkId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVoucherAmount() {
        return voucherAmount;
    }

    public void setVoucherAmount(String voucherAmount) {
        this.voucherAmount = voucherAmount;
    }

    public String getVoucherBatchNumber() {
        return voucherBatchNumber;
    }

    public void setVoucherBatchNumber(String voucherBatchNumber) {
        this.voucherBatchNumber = voucherBatchNumber;
    }
    public void setVoucherRowNumber(int voucherRowNumber) {
        this.voucherRowNumber = voucherRowNumber;
    }

    public String getVoucherSerialNumber() {
        return voucherSerialNumber;
    }

    public void setVoucherSerialNumber(String voucherSerialNumber) {
        this.voucherSerialNumber = voucherSerialNumber;
    }

    public String getVoucherNumber() {
        return voucherNumber;
    }
    public int getVoucherRowNumber() {
        return voucherRowNumber;
    }

    public void setVoucherNumber(String voucherNumber) {
        this.voucherNumber = voucherNumber;
    }

    public String getVoucherExpiryDate() {
        return voucherExpiryDate;
    }

    public void setVoucherExpiryDate(String voucherExpiryDate) {
        this.voucherExpiryDate = voucherExpiryDate;
    }

    public String getVoucherStatus() {
        return voucherStatus;
    }

    public void setVoucherStatus(String voucherStatus) {
        this.voucherStatus = voucherStatus;
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
