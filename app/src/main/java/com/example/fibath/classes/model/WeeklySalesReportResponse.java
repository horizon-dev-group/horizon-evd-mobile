package com.example.fibath.classes.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeeklySalesReportResponse {
    @SerializedName("totalCardMoney")
    @Expose
    private List<TotalCardMoney> totalCardMoney = null;

    public List<TotalCardMoney> getTotalCardMoney() {
        return totalCardMoney;
    }

    public void setTotalCardMoney(List<TotalCardMoney> totalCardMoney) {
        this.totalCardMoney = totalCardMoney;
    }
}
