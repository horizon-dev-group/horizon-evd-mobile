package com.example.fibath.ui.OfflineVoucher.database.model;

/**
 * Created by solomon on 4/15/2021.
 */

public class DownloadedVouchers {
    public static final String TABLE_NAME = "voucher";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_VOUCHER_AMOUNT = "voucher_amount";
    public static final String COLUMN_VOUCHER_ID = "voucher_id";
    public static final String COLUMN_VOUCHER_NUMBER = "voucher_number";
    public static final String COLUMN_VOUCHER_SERIAL_NUMBER = "voucher_serial_number";
    public static final String COLUMN_VOUCHER_EXPIRY_DATE = "voucher_expiry_date";
    public static final String COLUMN_VOUCHER_RETAILER_NAME = "voucher_retailer_name";
    public static final String COLUMN_VOUCHER_STATUS = "voucher_status";
    public static final String COLUMN_VOUCHER_BULK_ID = "voucher_bulk_id";
    public static final String COLUMN_VOUCHER_SYNC = "voucher_sync";
    public static final String COLUMN_VOUCHER_REDOWNLOAD = "voucher_redownload";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private int id;
    private String voucherId;
    private String voucherAmount;
    private String voucherNumber;
    private String voucherSerialNumber;
    private String voucherExpiryDate;
    private String voucherRetailerName;
    private String voucherStatus;
    private String voucherBulkId;
    private String voucherSync;
    private int voucherReDownload;
    private String voucherCount;
    private String timestamp;


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_VOUCHER_ID + " TEXT NOT NULL UNIQUE,"
                    + COLUMN_VOUCHER_AMOUNT + " TEXT,"
                    + COLUMN_VOUCHER_NUMBER + " TEXT,"
                    + COLUMN_VOUCHER_SERIAL_NUMBER + " TEXT NOT NULL UNIQUE,"
                    + COLUMN_VOUCHER_EXPIRY_DATE + " TEXT,"
                    + COLUMN_VOUCHER_RETAILER_NAME + " TEXT,"
                    + COLUMN_VOUCHER_STATUS + " TEXT,"
                    + COLUMN_VOUCHER_BULK_ID+ " TEXT,"
                    + COLUMN_VOUCHER_SYNC+ " TEXT,"
                    + COLUMN_VOUCHER_REDOWNLOAD+ " INTEGER,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    public DownloadedVouchers() {
    }

    public DownloadedVouchers(int id,String voucherId, String voucherAmount, String voucherNumber, String voucherSerialNumber, String voucherExpiryDate, String voucherRetailerName, String voucherStatus,String voucherBulkId,String voucherSync,int voucherReDownload, String timestamp) {
        this.id = id;
        this.voucherId = voucherId;
        this.voucherAmount = voucherAmount;
        this.voucherNumber = voucherNumber;
        this.voucherSerialNumber = voucherSerialNumber;
        this.voucherExpiryDate = voucherExpiryDate;
        this.voucherRetailerName = voucherRetailerName;
        this.voucherStatus = voucherStatus;
        this.timestamp = timestamp;
        this.voucherBulkId = voucherBulkId;
        this.voucherSync = voucherSync;
        this.voucherReDownload=voucherReDownload;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }

    public String getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(String voucherId) {
        this.voucherId = voucherId;
    }

    public String getVoucherAmount() {
        return voucherAmount;
    }

    public void setVoucherAmount(String voucherAmount) {
        this.voucherAmount = voucherAmount;
    }

    public String getVoucherNumber() {
        return voucherNumber;
    }

    public void setVoucherNumber(String voucherNumber) {
        this.voucherNumber = voucherNumber;
    }

    public String getVoucherSerialNumber() {
        return voucherSerialNumber;
    }

    public void setVoucherSerialNumber(String voucherSerialNumber) {
        this.voucherSerialNumber = voucherSerialNumber;
    }

    public String getVoucherExpiryDate() {
        return voucherExpiryDate;
    }

    public void setVoucherExpiryDate(String voucherExpiryDate) {
        this.voucherExpiryDate = voucherExpiryDate;
    }

    public String getVoucherRetailerName() {
        return voucherRetailerName;
    }

    public void setVoucherRetailerName(String voucherRetailerName) {
        this.voucherRetailerName = voucherRetailerName;
    }


    public String getVoucherStatus() {
        return voucherStatus;
    }

    public void setVoucherStatus(String voucherStatus) {
        this.voucherStatus = voucherStatus;
    }

    public String getVoucherBulkId() {
        return voucherBulkId;
    }

    public void setVoucherBulkId(String voucherBulkId) {
        this.voucherBulkId = voucherBulkId;
    }

    public String getVoucherSync() {
        return voucherSync;
    }

    public void setVoucherSync(String voucherSync) {
        this.voucherSync = voucherSync;
    }

    public String getVoucherCount() {
        return voucherCount;
    }

    public void setVoucherCount(String voucherCount) {
        this.voucherCount = voucherCount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getVoucherReDownload() {
        return voucherReDownload;
    }

    public void setVoucherReDownload(int voucherReDownload) {
        this.voucherReDownload = voucherReDownload;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
