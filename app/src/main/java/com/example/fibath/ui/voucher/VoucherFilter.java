package com.example.fibath.ui.voucher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.fibath.R;import com.example.fibath.classes.api.APIInterface;
import com.example.fibath.classes.model.BulkVoucherResponse;
import com.example.fibath.classes.model.ErrorResponse;
import com.example.fibath.classes.networking.communication.ServerCommunicator;
import com.example.fibath.classes.user.User;
import com.example.fibath.classes.voucher.VoucherBulkPrintStatic;
import com.example.fibath.ui.OfflineVoucher.database.OfflineDBManager;
import com.example.fibath.ui.OfflineVoucher.database.model.DownloadedVouchers;
import com.example.fibath.ui.Printer.bluetooth.BluetoothPrinter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
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

public class VoucherFilter extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String[] paths = {"Filter By Serial Number", "Filter By Voucher Number"};
    private List<DownloadedVouchers> downloadedVouchersList = new ArrayList<>();
    private List<DownloadedVouchers> downloadedVouchersListFilteredByNumber = new ArrayList<>();
    private OfflineDBManager offlineDBManager;

    EditText serialStart;
    EditText serialEnd;
    Button print;
    Spinner dropDown;
    String bulk_id;
    public List<com.example.fibath.classes.model.Voucher> bulkPinList = new ArrayList<>();
    boolean isBluetooth = false;
    int selecteditem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher_filter);

        bulk_id = getIntent().getStringExtra("TRANSACTION_ID");

        serialStart = findViewById(R.id.serialStart);
        serialEnd = findViewById(R.id.serialEnd);
        print = findViewById(R.id.printButton);
        dropDown = findViewById(R.id.dropDown);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(VoucherFilter.this,
                android.R.layout.simple_spinner_item, paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropDown.setAdapter(adapter);
        dropDown.setOnItemSelectedListener(this);
        print.setOnClickListener(e -> {
            if (selecteditem == 0) {

                    if (serialStart.getText().toString().length() < 12 || serialEnd.getText().toString().length() < 12) {
                        Toast.makeText(getApplicationContext(), "Incorrect Serial Number", Toast.LENGTH_SHORT).show();
                    } else {
                        if(isNetworkConnected()) {
                            HashMap<String, Object> bulkMap = new HashMap<>();
                            bulkMap.put("serialStart", Double.parseDouble(serialStart.getText().toString()));
                            bulkMap.put("serialEnd", Double.parseDouble(serialEnd.getText().toString()));
                            bulkMap.put("bulk_id", bulk_id);
                            System.out.println("Bulk iD issssssssss" + bulk_id);
                            filterVoucherBySerialNumber(bulkMap);
                        }else{
                            offlineDBManager = new OfflineDBManager(this);

                            filterOfflineVoucherBySerialNumber(bulk_id,Double.parseDouble(serialStart.getText().toString()),Double.parseDouble(serialEnd.getText().toString()));

                            }
                    }

            } else {


                    if (serialStart.getText().toString() == " " || serialStart.getText().toString().length() < 1 || serialEnd.getText().toString() == " " || serialEnd.getText().toString().length() < 1) {
                        Toast.makeText(getApplicationContext(), "Please Enter Voucher Number", Toast.LENGTH_SHORT).show();

                    } else {

                        if (Integer.parseInt(serialStart.getText().toString()) < 1 || Integer.parseInt(serialEnd.getText().toString()) < 1) {
                            Toast.makeText(getApplicationContext(), "Incorrect Voucher Number", Toast.LENGTH_SHORT).show();
                        } else {
                            if(isNetworkConnected()) {
                                HashMap<String, String> bulkMap = new HashMap<>();

                                bulkMap.put("bulk_id", bulk_id);
                                System.out.println("Bulk iD issssssssss" + bulk_id);
                                filterVoucherByVoucherNumber(bulkMap, Integer.parseInt(serialStart.getText().toString()), Integer.parseInt(serialEnd.getText().toString()));
                            }else {
                                offlineDBManager = new OfflineDBManager(this);

                                filterOfflineVoucherByVoucherNumber(bulk_id,Double.parseDouble(serialStart.getText().toString()),Double.parseDouble(serialEnd.getText().toString()));
                            }
                        }
                    }
                }


        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
//        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);

        switch (position) {
            case 0:
                selecteditem = 0;
                serialStart.setHint("Serial Begin");
                serialEnd.setHint("Serial End");
                break;
            case 1:
                selecteditem = 1;
                serialStart.setHint("Number Begin");
                serialEnd.setHint("Number End");
                break;


        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
    }

    //region filter voucher by Serial Number
    public void filterVoucherBySerialNumber(HashMap<String, Object> voucherFilter) {
        APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
        myInterface.retailerGetVoucherFilter(User.getUserSessionToken(), voucherFilter)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<ArrayList<BulkVoucherResponse>>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(@NonNull Response<ArrayList<BulkVoucherResponse>> voucherFilterResponse) {
                if (voucherFilterResponse.code() == 200) {
                    List<BulkVoucherResponse> lists = voucherFilterResponse.body();
                    for (int i = 0; i < lists.size(); i++) {

                        System.out.println("Birr is" + lists.get(i).getVoucherId().getVoucherAmount());
                        bulkPinList.add(lists.get(i).getVoucherId());
                        bulkPinList.get(i).setVoucherRowNumber(i+1);
                        bulkPinList.get(i).setBulkId(lists.get(i).getBulkId());

                    }

                    printAll();
                } else {
                    switch (voucherFilterResponse.code()) {
                        case 400:
                            Toast.makeText(getApplicationContext(), "You Don't Have Voucher With Those Serial Number", Toast.LENGTH_SHORT).show();
                            Gson gson = new GsonBuilder().create();
                            ErrorResponse mError;
                            try {
                                mError = gson.fromJson(voucherFilterResponse.errorBody().string(), ErrorResponse.class);
//                                FancyToast.makeText(getApplicationContext(), mError.getErrors().get(0).getMsg(), FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                            } catch (IOException e) {
                                // handle failure to read error
                            }
                            break;
                        case 500:
                            System.out.println("500 Errrrrrrrrrrrr");

//                            FancyToast.makeText(getApplicationContext(), "Server Error! Please contact the system administrators.", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                            break;
                        default:
//                            FancyToast.makeText(getApplicationContext(), "Unknown Error! Something went wrong.", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                            break;
                    }
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }
    //endregion

    //region filter voucher by Voucher Number
    public void filterVoucherByVoucherNumber(HashMap<String, String> bulkId, int startNumber, int endNumber) {


        APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
        myInterface.retailerGetEachOfBulkVoucherTransaction(User.getUserSessionToken(), APP_VERSION, bulkId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<ArrayList<BulkVoucherResponse>>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Response<ArrayList<BulkVoucherResponse>> responseTransactionHistory) {
                int fiveCount = 0;


                if (responseTransactionHistory.code() == 200) {
                    List<BulkVoucherResponse> lists = responseTransactionHistory.body();
                    if (endNumber > lists.size() || startNumber > lists.size() || startNumber < 1) {

                        Toast.makeText(getApplicationContext(), "You Don't Have Any Transaction With This Voucher Number", Toast.LENGTH_SHORT).show();


                    } else {

                        for (int i = 0; i <= lists.size(); i++) {
                            if (i >= startNumber - 1 && i <= endNumber - 1) {
                                bulkPinList.add(lists.get(i).getVoucherId());
                                bulkPinList.get(bulkPinList.size()-1).setVoucherRowNumber(i+1);
                                System.out.println("I isssssssssssssss"+i);
                                String bulkIdis=lists.get(0).getBulkId();
                                bulkPinList.get(bulkPinList.size()-1).setBulkId(bulk_id);


                            }

                        }

                        printAll();

                    }


                } else if (responseTransactionHistory.code() == 400) {

                    Toast.makeText(getApplicationContext(), "You Don't Have Any Transaction", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.someting_went_worong), Toast.LENGTH_SHORT).show();
                    System.out.println("Error code is" + responseTransactionHistory.code());

                }
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("has" + e);
            }

            @Override
            public void onComplete() {

            }
        });
    }
    //endregion


    public void printAll() {
        VoucherBulkPrintStatic.setBulkPrint(bulkPinList);
//        VoucherBulkPrintStatic.setOfflineBulkPrint(downloadedVouchersList);
        VoucherBulkPrintStatic.setIsVouchersLoaded(true);
        Intent printerIntent = new Intent();
        SharedPreferences sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);


            printerIntent = new Intent(VoucherFilter.this, BluetoothPrinter.class);


        if (isNetworkConnected()) {
        printerIntent.putExtra("offline", "false");
        printerIntent.putExtra("isReprint", "true");
        printerIntent.putExtra("isBulk", "true");


        } else {
            printerIntent.putExtra("offline", "true");
            printerIntent.putExtra("isReprint", "true");
            printerIntent.putExtra("isBulk", "true");

            printerIntent.putExtra("bulkid", bulk_id);
        }

        startActivity(printerIntent);
    }

    //region filter offline vouchers by serial number
    public void filterOfflineVoucherBySerialNumber(String bulkId, Double serialStart, Double serialEnd) {
        int serialQuantity=offlineDBManager.filterDownloadedVouchersBySerialNumber(bulk_id,serialStart,serialEnd).size();



        if(serialQuantity>0){
            downloadedVouchersList.addAll(offlineDBManager.filterDownloadedVouchersBySerialNumber(bulk_id,serialStart,serialEnd));
            VoucherBulkPrintStatic.setOfflineBulkPrint(downloadedVouchersList);
            VoucherBulkPrintStatic.setIsVouchersLoaded(true);
//            for(int i=0;i<serialQuantity;i++){
//                System.out.println("Serial Number Is "+downloadedVouchersList.get(i).getVoucherSerialNumber());
//            }
            printAll();
        }else{
            Toast.makeText(getApplicationContext(),"You Don't have Vouchers With Those serial Numbers",Toast.LENGTH_SHORT).show();
        }


    }
   //endregion

    //region filter offline voucher by voucher number
    public void filterOfflineVoucherByVoucherNumber(String bulkId, Double startNumber, Double endNumber) {
        int voucherQuantity = offlineDBManager.getAllDownloadedVouchersByBulkId(bulk_id).size();

        if (voucherQuantity > 0) {
            downloadedVouchersList.addAll(offlineDBManager.getAllDownloadedVouchersByBulkId(bulk_id));
            VoucherBulkPrintStatic.setOfflineBulkPrint(downloadedVouchersList);
            VoucherBulkPrintStatic.setIsVouchersLoaded(true);

            if (endNumber > downloadedVouchersList.size() || startNumber > downloadedVouchersList.size() || startNumber < 1) {

                Toast.makeText(getApplicationContext(), "You Don't Have Any Transaction With This Voucher Number", Toast.LENGTH_SHORT).show();


            } else {

                for (int i = 0; i <= downloadedVouchersList.size(); i++) {

                    if (i >= startNumber - 1 && i <= endNumber - 1) {
                        downloadedVouchersListFilteredByNumber.add(downloadedVouchersList.get(i));

                    }
                    VoucherBulkPrintStatic.setOfflineBulkPrint(downloadedVouchersListFilteredByNumber);
                    VoucherBulkPrintStatic.setIsVouchersLoaded(true);

                }
                printAll();

            }
        }else{
            Toast.makeText(getApplicationContext(),"You Don't have Vouchers With Those Voucher Numbers",Toast.LENGTH_SHORT).show();

        }
    }
    //endregion

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }



}