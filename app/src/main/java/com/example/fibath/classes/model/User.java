package com.example.fibath.classes.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class User {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("userType")
    @Expose
    private String userType;
    @SerializedName("userParentId")
    @Expose
    private String userParentId;
    @SerializedName("posNumber")
    @Expose
    private String posNumber;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("Region")
    @Expose
    private String region;
    @SerializedName("City")
    @Expose
    private String city;
    @SerializedName("Sub_City")
    @Expose
    private String subCity;
    @SerializedName("Zone")
    @Expose
    private String zone;
    @SerializedName("House_No")
    @Expose
    private String houseNo;
    @SerializedName("TerminalName_Model")
    @Expose
    private String terminalNameModel;
    @SerializedName("TerminalQty")
    @Expose
    private String terminalQty;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("__v")
    @Expose
    private Integer v;
    @SerializedName("account")
    @Expose
    private String account;
    @SerializedName("TerminalSerials")
    @Expose
    private List<Object> terminalSerials = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserParentId() {
        return userParentId;
    }

    public void setUserParentId(String userParentId) {
        this.userParentId = userParentId;
    }

    public String getPosNumber() {
        return posNumber;
    }

    public void setPosNumber(String posNumber) {
        this.posNumber = posNumber;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSubCity() {
        return subCity;
    }

    public void setSubCity(String subCity) {
        this.subCity = subCity;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getHouseNo() {
        return houseNo;
    }

    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }

    public String getTerminalNameModel() {
        return terminalNameModel;
    }

    public void setTerminalNameModel(String terminalNameModel) {
        this.terminalNameModel = terminalNameModel;
    }

    public String getTerminalQty() {
        return terminalQty;
    }

    public void setTerminalQty(String terminalQty) {
        this.terminalQty = terminalQty;
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

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public List<Object> getTerminalSerials() {
        return terminalSerials;
    }

    public void setTerminalSerials(List<Object> terminalSerials) {
        this.terminalSerials = terminalSerials;
    }

}

