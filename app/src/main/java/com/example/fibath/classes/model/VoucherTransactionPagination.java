package com.example.fibath.classes.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VoucherTransactionPagination {
    @SerializedName("transaction")
    @Expose
    private List<VoucherTransaction> transaction = null;
    @SerializedName("totalPages")
    @Expose
    private Integer totalPages;
    @SerializedName("currentPage")
    @Expose
    private Integer currentPage;

    public List<VoucherTransaction> getTransaction() {
        return transaction;
    }

    public void setTransaction(List<VoucherTransaction> transaction) {
        this.transaction = transaction;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }
}
