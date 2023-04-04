package com.example.fibath.classes.voucher;

import com.example.fibath.classes.model.Voucher;
import com.example.fibath.ui.OfflineVoucher.database.model.DownloadedVouchers;
import java.util.List;

public class VoucherBulkPrint {
    private static   List<com.example.fibath.classes.model.Voucher>    bulkPrint =null;
    private static   List<DownloadedVouchers>    offlineBulkPrint =null;
    private static boolean isVouchersLoaded = false;

    public static List<Voucher> getBulkPrint() {
        return bulkPrint;
    }

    public static void setBulkPrint(List<Voucher> bulkPrint) {
        VoucherBulkPrint.bulkPrint = bulkPrint;
    }

    public static boolean isIsVouchersLoaded() {
        return isVouchersLoaded;
    }

    public static void setIsVouchersLoaded(boolean isVouchersLoaded) {
        VoucherBulkPrint.isVouchersLoaded = isVouchersLoaded;
    }

    public static List<DownloadedVouchers> getOfflineBulkPrint() {
        return offlineBulkPrint;
    }

    public static void setOfflineBulkPrint(List<DownloadedVouchers> offlineBulkPrint) {
        VoucherBulkPrint.offlineBulkPrint = offlineBulkPrint;
    }
}
