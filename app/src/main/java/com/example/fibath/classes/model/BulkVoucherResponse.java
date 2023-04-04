package com.example.fibath.classes.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BulkVoucherResponse {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("voucher_id")
    @Expose
    private Voucher voucherId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("userParentId")
    @Expose
    private String userParentId;
    @SerializedName("voucher_status")
    @Expose
    private String voucherStatus;
    @SerializedName("opening_balance")
    @Expose
    private String openingBalance;
    @SerializedName("closing_balance")
    @Expose
    private String closingBalance;
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
    @SerializedName("voucherType")
    @Expose
    private String voucherType;


    public String getVoucherSerialNumber() {
        return voucherSerialNumber;
    }

    public void setVoucherSerialNumber(String voucherSerialNumber) {
        this.voucherSerialNumber = voucherSerialNumber;
    }

    @SerializedName("voucher_serial_number")
    @Expose
    private String voucherSerialNumber;

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
        return voucherId;
    }

    public void setVoucherId(Voucher voucherId) {
        this.voucherId = voucherId;
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

    public String getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(String openingBalance) {
        this.openingBalance = openingBalance;
    }

    public String getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(String closingBalance) {
        this.closingBalance = closingBalance;
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

    public String getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(String voucherType) {
        this.voucherType = voucherType;
    }

}
