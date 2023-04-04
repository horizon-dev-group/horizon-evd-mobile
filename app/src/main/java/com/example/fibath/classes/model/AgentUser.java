package com.example.fibath.classes.model;



import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AgentUser {


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
        @SerializedName("phone")
        @Expose
        private String phone;
        @SerializedName("TerminalSerials")
        @Expose
        private List<Object> terminalSerials = null;
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
        private AccountResponse account;

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

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public List<Object> getTerminalSerials() {
            return terminalSerials;
        }

        public void setTerminalSerials(List<Object> terminalSerials) {
            this.terminalSerials = terminalSerials;
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

        public AccountResponse getAccount() {
            return account;
        }

        public void setAccount(AccountResponse account) {
            this.account = account;
        }


}
