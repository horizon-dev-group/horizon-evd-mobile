package com.example.fibath.ui.OfflineVoucher;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.fibath.classes.api.APIInterface;
import com.example.fibath.classes.model.SyncResponse;
import com.example.fibath.classes.model.SoldVoucherResponse;
import com.example.fibath.classes.networking.communication.ServerCommunicator;
import com.example.fibath.classes.state.StateManager;
import com.example.fibath.classes.user.User;
import com.example.fibath.ui.OfflineVoucher.database.OfflineDBManager;
import com.example.fibath.ui.OfflineVoucher.database.model.DownloadedVouchers;
import com.example.fibath.ui.intro.Intro;
import com.example.fibath.ui.login.Login;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

import static com.example.fibath.classes.api.APIInterface.APP_VERSION;
import static com.example.fibath.classes.api.APIInterface.BASE_URL;

public class OfflineVouchersManager {
    OfflineDBManager offlineDBManager;
    boolean syncFromSplash = false;
    Context context;
    public static boolean isSyncing = false;

    public OfflineVouchersManager(Context context) {
        offlineDBManager = new OfflineDBManager(context);
        this.context = context;
    }

    public OfflineVouchersManager(Context context, boolean syncFromSplash) {
        offlineDBManager = new OfflineDBManager(context);
        this.context = context;
        this.syncFromSplash = syncFromSplash;
    }

    public String getUnSyncedVouchersCount() {
        int checkUserData = offlineDBManager.checkRecord();
        if (checkUserData == 0) {
            return "NO VOUCHERS IN OFFLINE STOCK";
        } else {
            List<DownloadedVouchers> checkVoucherData = offlineDBManager.getAllUnSyncedVouchers();
            int checkUnSyncVoucherSize = checkVoucherData.size();
            if (checkUnSyncVoucherSize > 0) {
                if (checkUnSyncVoucherSize == 1) {
                    return checkUnSyncVoucherSize + " VOUCHER IS NOT SYNCED. PLEASE CONNECT TO A NETWORK TO SYNC.";
                } else {
                    return checkUnSyncVoucherSize + " VOUCHERS ARE NOT SYNCED.  PLEASE CONNECT TO A NETWORK TO SYNC.";
                }
            } else {
                return "EVERYTHING SYNCED";
            }
        }
    }

    public void sync() {
        isSyncing = true;
        Toast.makeText(context, "Syncing Offline Vouchers", Toast.LENGTH_SHORT).show();
        int checkUserData = offlineDBManager.checkRecord();
        if (checkUserData == 0) {
            Toast.makeText(context, "No Voucher is Synced", Toast.LENGTH_SHORT).show();
            System.out.println("$$$$From Downloads$$$$");
            downloadPurchasedVoucher();
        } else {
            List<DownloadedVouchers> checkSoldVoucherData = offlineDBManager.getAllUnSyncedVouchers();
            List<DownloadedVouchers> checkUnsoldVoucherData = offlineDBManager.getAllFreshVouchers();
            int checkUnSyncVoucherSize = checkSoldVoucherData.size();
            int checkUnsoldVoucherSize = checkUnsoldVoucherData.size();
            if (checkUnSyncVoucherSize > 0 || checkUnsoldVoucherSize > 0) {
                JsonObject obj = new JsonObject();
                JsonObject soldVoucher = new JsonObject();
                JsonObject unSoldVoucher = new JsonObject();
                HashMap<String, String> map = new HashMap<>();
                System.out.println("sold size  is" + checkUnSyncVoucherSize);
                System.out.println("unsold size is" + checkUnsoldVoucherSize);

                for (int i = 0; i < checkSoldVoucherData.size(); i++) {
                    System.out.println("Voucher id is" + checkSoldVoucherData.get(i).getVoucherId());
//                    map.put(String.valueOf(checkSoldVoucherData.get(i).getVoucherId()), String.valueOf(checkSoldVoucherData.get(i).getVoucherId()));
                    soldVoucher.addProperty(String.valueOf(checkSoldVoucherData.get(i).getVoucherId()), String.valueOf(checkSoldVoucherData.get(i).getVoucherId()));
                }
                System.out.println("map is" + map.keySet());
//


                for (int i = 0; i < checkUnsoldVoucherSize; i++) {
                    System.out.println("Voucher id is" + checkUnsoldVoucherData.get(i).getVoucherId());
                    unSoldVoucher.addProperty(String.valueOf(checkUnsoldVoucherData.get(i).getVoucherId()), String.valueOf(checkUnsoldVoucherData.get(i).getVoucherId()));
                }
//                unSoldVoucher.add("unSoldVoucher", unSoldVoucher);
                obj.add("soldVoucher", soldVoucher);
                obj.add("unSoldVoucher", unSoldVoucher);
                System.out.println("object is " + obj.toString());
                syncRemoteData(obj, checkSoldVoucherData);
            } else {
//                Toast.makeText(this, "All Voucher Are Synced", Toast.LENGTH_SHORT).show();
                System.out.println("All Voucher Are Synced");
                isSyncing = false;
                if (syncFromSplash) {
                    if (!StateManager.isAppFirstStart()) {
                        context.startActivity(new Intent(context, Intro.class));
                    } else {
                        context.startActivity(new Intent(context, Login.class));
                    }
                }
            }
        }
    }

    public static boolean isSyncing() {
        return isSyncing;
    }

    //region update voucher status remotely
    private void syncRemoteData(JsonObject jsonObject, List<DownloadedVouchers> updateVouchers) {
        APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
        myInterface.retailerSyncDownloadedVouchers(User.getUserSessionToken(), APP_VERSION, jsonObject)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<SyncResponse>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(@NonNull Response<SyncResponse> responseTransactionHistory) {
                int fiveCount = 0;
                if (responseTransactionHistory.code() == 201) {
//                    Toast.makeText(context, "Sync Completed" + responseTransactionHistory.body().getSoldVouchersSyncedFromOthers().size(), Toast.LENGTH_SHORT).show();
                    System.out.println("alreadySyncedVouchersFromServer " + responseTransactionHistory.body().getSoldVouchersSyncedFromOthers().size());
                    System.out.println("newVouchersDownloadedFromOtherDevice " + responseTransactionHistory.body().getNewVouchersDownloadedFromOtherDevice().size());
                    List<SoldVoucherResponse> alreadySyncedVouchersFromServer = responseTransactionHistory.body().getSoldVouchersSyncedFromOthers();
                    List<SoldVoucherResponse> newVouchersDownloadedFromOtherDevice = responseTransactionHistory.body().getNewVouchersDownloadedFromOtherDevice();
                    if (updateVouchers.size() > 0) {
                        offlineDBManager.updateSyncStatus(updateVouchers);
                    }

                    if (alreadySyncedVouchersFromServer.size() > 0) {
                        offlineDBManager.updateSyncStatusFromServerDirectly(alreadySyncedVouchersFromServer);

                    }
                    if (newVouchersDownloadedFromOtherDevice.size() > 0) {
                        for (int i = 0; i < newVouchersDownloadedFromOtherDevice.size(); i++) {
                            System.out.println("@@newVouchersDownloadedFromOtherDevice@@ " + newVouchersDownloadedFromOtherDevice.get(i).getVoucherId().getId());

                            offlineDBManager.insertVouchers(newVouchersDownloadedFromOtherDevice.get(i).getVoucherId().getId(), newVouchersDownloadedFromOtherDevice.get(i).getVoucherId().getVoucherAmount(), newVouchersDownloadedFromOtherDevice.get(i).getVoucherId().getVoucherNumber(), newVouchersDownloadedFromOtherDevice.get(i).getVoucherId().getVoucherSerialNumber(), newVouchersDownloadedFromOtherDevice.get(i).getVoucherId().getVoucherExpiryDate(), newVouchersDownloadedFromOtherDevice.get(i).getBulkId(),newVouchersDownloadedFromOtherDevice.get(i).getReDownloaded());
                        }
                    }
                    isSyncing = false;
                } else if (responseTransactionHistory.code() == 400) {
                    isSyncing = false;
                    Toast.makeText(context.getApplicationContext(), "Unable to Sync Your Data", Toast.LENGTH_SHORT).show();
                }
                if (syncFromSplash) {
                    isSyncing = false;
                    if (!StateManager.isAppFirstStart()) {
                        Intent introIntent = new Intent(context, Intro.class);
                        introIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(introIntent);
                    } else {
                        Intent introIntent = new Intent(context, Login.class);
                        introIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(introIntent);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                isSyncing = false;

                System.out.println("has" + e);
            }

            @Override
            public void onComplete() {
                isSyncing = false;
            }
        });
    }
    //end region

    //region download purchased vouchers
    private void downloadPurchasedVoucher() {
        System.out.println("token is" + User.getUserSessionToken());
        APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
        myInterface.retailerDownloadVouchers(User.getUserSessionToken(), APP_VERSION)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<ArrayList<SoldVoucherResponse>>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(@NonNull Response<ArrayList<SoldVoucherResponse>> responseTransactionHistory) {
                int fiveCount = 0;
                System.out.println("KKKKKKKKKK");
                if (responseTransactionHistory.code() == 200) {
                    for (int i = 0; i < responseTransactionHistory.body().size(); i++) {
                        offlineDBManager.insertVouchers(responseTransactionHistory.body().get(i).getVoucherId().getId(), responseTransactionHistory.body().get(i).getVoucherId().getVoucherAmount(), responseTransactionHistory.body().get(i).getVoucherId().getVoucherNumber(), responseTransactionHistory.body().get(i).getVoucherId().getVoucherSerialNumber(), responseTransactionHistory.body().get(i).getVoucherId().getVoucherExpiryDate(), responseTransactionHistory.body().get(i).getBulkId(),responseTransactionHistory.body().get(i).getReDownloaded());
                    }
                    Toast.makeText(context, "Sync Completed", Toast.LENGTH_SHORT).show();
                    System.out.println("Sync Completed");
                } else if (responseTransactionHistory.code() == 400) {
                    System.out.println("Unable to Sync Your Data");
                    Toast.makeText(context, "Unable to Sync Your Data", Toast.LENGTH_SHORT).show();
                }
                if (syncFromSplash) {
                    if (!StateManager.isAppFirstStart()) {
                        Intent introIntent = new Intent(context, Intro.class);
                        introIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(introIntent);
//                        context.startActivity(new Intent(context, Intro.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    } else {
                        Intent loginIntent = new Intent(context, Login.class);
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(loginIntent);
//                        context.startActivity(new Intent(context, Login.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                }
                isSyncing = false;

                System.out.println("erro is" + responseTransactionHistory.code());
            }

            @Override
            public void onError(Throwable e) {

                isSyncing = false;

                System.out.println("has" + e);
            }

            @Override
            public void onComplete() {
                isSyncing = false;
            }
        });
    }
    //endregion
}
