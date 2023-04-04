package com.example.fibath.classes.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FundResponse {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("sender_id")
    @Expose
    private User senderId;
    @SerializedName("receiver_id")
    @Expose
    private User receiverId;
    @SerializedName("transaction_amount")
    @Expose
    private String transactionAmount;
    @SerializedName("transaction_commission")
    @Expose
    private String transactionCommission;
    @SerializedName("fund_type")
    @Expose
    private String fundType;
    @SerializedName("deductedTransaction")
    @Expose
    private String deductedTransaction;
    @SerializedName("deductedBy")
    @Expose
    private User deductedBy;
    @SerializedName("fund_amount")
    @Expose
    private String fundAmount;
    @SerializedName("transaction_type")
    @Expose
    private String transactionType;
    @SerializedName("transaction_id")
    @Expose
    private String transactionId;
    @SerializedName("transaction_status")
    @Expose
    private String transactionStatus;
    @SerializedName("remark")
    @Expose
    private String remark;
    @SerializedName("deductable")
    @Expose
    private String deductable;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("__v")
    @Expose
    private Integer v;

    @SerializedName("due_date")
@Expose
private String dueDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getSenderId() {
        return senderId;
    }

    public void setSenderId(User senderId) {
        this.senderId = senderId;
    }

    public User getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(User receiverId) {
        this.receiverId = receiverId;
    }

    public String getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getTransactionCommission() {
        return transactionCommission;
    }

    public void setTransactionCommission(String transactionCommission) {
        this.transactionCommission = transactionCommission;
    }

    public String getFundType() {
        return fundType;
    }

    public void setFundType(String fundType) {
        this.fundType = fundType;
    }

    public String getDeductedTransaction() {
        return deductedTransaction;
    }

    public void setDeductedTransaction(String deductedTransaction) {
        this.deductedTransaction = deductedTransaction;
    }

    public User getDeductedBy() {
        return deductedBy;
    }

    public void setDeductedBy(User deductedBy) {
        this.deductedBy = deductedBy;
    }

    public String getFundAmount() {
        return fundAmount;
    }

    public void setFundAmount(String fundAmount) {
        this.fundAmount = fundAmount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDeductable() {
        return deductable;
    }

    public void setDeductable(String deductable) {
        this.deductable = deductable;
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

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

public String getDueDate(){
        return dueDate;
        }

public void setDueDate(String dueDate){
        this.dueDate=dueDate;
        }

        }