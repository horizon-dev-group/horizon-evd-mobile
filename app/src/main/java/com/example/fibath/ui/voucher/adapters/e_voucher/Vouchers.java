package com.example.fibath.ui.voucher.adapters.e_voucher;

public class Vouchers {
    private String id;
    private String operatorId;
    private String voucherAmountId;
    private String userId;
    private String mrp;
    private String amount;
    private String pinGroupNumber;
    private String pinSerialNumber;
    private String pinNumber;
    private String pinExpiryDate;
    private String voucherStatus;
    private String type;
    private String voucherStatusChangeDateTime;
    private String status;
    private String transactionId;
    private String created;
    private String modified;
    public Vouchers(){

    }

    public Vouchers(String id, String operatorId, String voucherAmountId, String userId, String mrp, String amount, String pinGroupNumber, String pinSerialNumber, String pinNumber, String pinExpiryDate, String voucherStatus, String type, String voucherStatusChangeDateTime, String status, String transactionId, String created, String modified) {
        this.id = id;
        this.operatorId = operatorId;
        this.voucherAmountId = voucherAmountId;
        this.userId = userId;
        this.mrp = mrp;
        this.amount = amount;
        this.pinGroupNumber = pinGroupNumber;
        this.pinSerialNumber = pinSerialNumber;
        this.pinNumber = pinNumber;
        this.pinExpiryDate = pinExpiryDate;
        this.voucherStatus = voucherStatus;
        this.type = type;
        this.voucherStatusChangeDateTime = voucherStatusChangeDateTime;
        this.status = status;
        this.transactionId = transactionId;
        this.created = created;
        this.modified = modified;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getVoucherAmountId() {
        return voucherAmountId;
    }

    public void setVoucherAmountId(String voucherAmountId) {
        this.voucherAmountId = voucherAmountId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPinGroupNumber() {
        return pinGroupNumber;
    }

    public void setPinGroupNumber(String pinGroupNumber) {
        this.pinGroupNumber = pinGroupNumber;
    }

    public String getPinSerialNumber() {
        return pinSerialNumber;
    }

    public void setPinSerialNumber(String pinSerialNumber) {
        this.pinSerialNumber = pinSerialNumber;
    }

    public String getPinNumber() {
        return pinNumber;
    }

    public void setPinNumber(String pinNumber) {
        this.pinNumber = pinNumber;
    }

    public String getPinExpiryDate() {
        return pinExpiryDate;
    }

    public void setPinExpiryDate(String pinExpiryDate) {
        this.pinExpiryDate = pinExpiryDate;
    }

    public String getVoucherStatus() {
        return voucherStatus;
    }

    public void setVoucherStatus(String voucherStatus) {
        this.voucherStatus = voucherStatus;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVoucherStatusChangeDateTime() {
        return voucherStatusChangeDateTime;
    }

    public void setVoucherStatusChangeDateTime(String voucherStatusChangeDateTime) {
        this.voucherStatusChangeDateTime = voucherStatusChangeDateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }
}
