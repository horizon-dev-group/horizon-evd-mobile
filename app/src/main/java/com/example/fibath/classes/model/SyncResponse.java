package com.example.fibath.classes.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SyncResponse {
    @SerializedName("soldVouchersSyncedFromOthers")
    @Expose
    private List<SoldVoucherResponse> soldVouchersSyncedFromOthers = null;
    @SerializedName("newVouchersDownloadedFromOtherDevice")
    @Expose
    private List<SoldVoucherResponse> newVouchersDownloadedFromOtherDevice = null;

    public List<SoldVoucherResponse> getSoldVouchersSyncedFromOthers() {
        return soldVouchersSyncedFromOthers;
    }

    public void setSoldVouchersSyncedFromOthers(List<SoldVoucherResponse> soldVouchersSyncedFromOthers) {
        this.soldVouchersSyncedFromOthers = soldVouchersSyncedFromOthers;
    }

    public List<SoldVoucherResponse> getNewVouchersDownloadedFromOtherDevice() {
        return newVouchersDownloadedFromOtherDevice;
    }

    public void setNewVouchersDownloadedFromOtherDevice(List<SoldVoucherResponse> newVouchersDownloadedFromOtherDevice) {
        this.newVouchersDownloadedFromOtherDevice = newVouchersDownloadedFromOtherDevice;
    }
}
