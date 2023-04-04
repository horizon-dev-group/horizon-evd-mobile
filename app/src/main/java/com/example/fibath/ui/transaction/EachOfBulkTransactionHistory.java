package com.example.fibath.ui.transaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fibath.R;import com.example.fibath.classes.model.Voucher;
import com.example.fibath.ui.voucher.VoucherFilter;
import com.example.fibath.classes.api.APIInterface;
import com.example.fibath.classes.model.BulkVoucherResponse;
import com.example.fibath.classes.networking.communication.ServerCommunicator;
import com.example.fibath.classes.user.User;
import com.example.fibath.classes.voucher.VoucherBulkPrintStatic;
import com.example.fibath.ui.OfflineVoucher.database.OfflineDBManager;
import com.example.fibath.ui.OfflineVoucher.database.model.DownloadedVouchers;
import com.example.fibath.ui.Printer.bluetooth.BluetoothPrinter;
import com.example.fibath.ui.transaction.adapters.RecyclerItemClickListener;
import com.example.fibath.ui.transaction.adapters.transactionHistory.EachOfBulkTransactionHistoryAdapter;
import com.example.fibath.ui.transaction.adapters.transactionHistory.OfflineEachOfBulkTransactionHistoryAdapter;
import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

import static com.example.fibath.classes.api.APIInterface.APP_VERSION;
import static com.example.fibath.classes.api.APIInterface.BASE_URL;

public class EachOfBulkTransactionHistory extends AppCompatActivity {
    @BindView(R.id.activity_transaction_history_newton_cradle_loading)
    RotateLoading activityTransactionHistoryNewtonCradleLoading;
    @BindView(R.id.print_all_btn)
    Button print_all_btn;
    @BindView(R.id.filter_btn)
    Button filter_btn;
    Toolbar transactionHistoryScreenToolbar;
    RecyclerView transactionHistoryRecyclerview;
    private EachOfBulkTransactionHistoryAdapter transactionHistoryAdapter;
    private OfflineEachOfBulkTransactionHistoryAdapter offlineEachOfBulkTransactionHistoryAdapter;
    private List<BulkVoucherResponse> voucherList = new ArrayList<>();
    private ArrayList<BulkVoucherResponse> multiselect_list = new ArrayList<>();
    private List<DownloadedVouchers> downloadedVouchersList = new ArrayList<>();
    private OfflineDBManager offlineDBManager;
    private ArrayList<DownloadedVouchers> downloadedVouchersList_multiselect_list = new ArrayList<>();
    private TextView emptyVoucherText,serialStart,serialEnd;
    private String bulk_id;
    public List<com.example.fibath.classes.model.Voucher> bulkPinList = new ArrayList<>();
    public List<com.example.fibath.classes.model.Voucher> selectedPinList = new ArrayList<>();
    public List<DownloadedVouchers> selectedOfflinePinList = new ArrayList<>();
    private String printContent = "voucher";
    private String logo = "horizon_ethiotelecom_logo.png";
    ActionMode mActionMode;
    Menu context_menu;
    boolean isMultiSelect = false;
    String applicationVersion;
    ArrayList<Integer> deletedListPosition = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.each_of_transaction_history);
//        new CheckInternetConnection(this).checkConnection();
        ButterKnife.bind(this);
        initUI();
//        transactionHistoryScreenToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getApplicationContext().startActivity(new Intent(getApplicationContext(), Home.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
////                Toast.makeText(getApplicationContext(),"Back Button Pressed",Toast.LENGTH_SHORT).show();
//
//            }
//        });
    }

    // 1.1:region Init UI
    private void initUI() {
        transactionHistoryScreenToolbar = findViewById(R.id.transaction_history_screen_toolbar);
        transactionHistoryRecyclerview = findViewById(R.id.trasnsaction_history_screen_recyclerview);
        serialStart=findViewById(R.id.lastSerial);
        serialEnd=findViewById(R.id.firstSerial);
        emptyVoucherText = findViewById(R.id.empty_voucher_view);
        bulk_id = getIntent().getStringExtra("TRANSACTION_ID");
        System.out.println("bulk id is " + bulk_id);
        if (isNetworkConnected()) {
            initRecyclerView();
            getTransactionHistory();
        } else {
            offlineDBManager = new OfflineDBManager(this);
            initOfflineRecyclerView();
        }
        filter_btn.setOnClickListener(e->{
            Intent intent=new Intent(getApplicationContext(), VoucherFilter.class);
            intent.putExtra("TRANSACTION_ID", bulk_id);
            startActivity(intent);
        });

        print_all_btn.setOnClickListener(e -> {
            printAll();
        });
    }
//end region



    //1.2 region Init RecyclerView
    private void initRecyclerView() {
        emptyVoucherText.setVisibility(View.GONE);
        transactionHistoryAdapter = new EachOfBulkTransactionHistoryAdapter(getApplicationContext(), voucherList, multiselect_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        transactionHistoryRecyclerview.setLayoutManager(linearLayoutManager);
        transactionHistoryRecyclerview.setHasFixedSize(false);
        transactionHistoryRecyclerview.setAdapter(transactionHistoryAdapter);
        transactionHistoryRecyclerview.addOnItemTouchListener(new RecyclerItemClickListener(this, transactionHistoryRecyclerview, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect)
                    multi_select(position, "online");
                else
                    Toast.makeText(getApplicationContext(), "Details Page", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    multiselect_list = new ArrayList<BulkVoucherResponse>();
                    isMultiSelect = true;

                    if (mActionMode == null) {
                        mActionMode = startActionMode(mActionModeCallback);
                    }
                }

                multi_select(position, "online");

            }
        }));

    }

    //endregion

    //region init Offline RecyclerView
    private void initOfflineRecyclerView() {
        toggleEmptyVoucherView();
        downloadedVouchersList.addAll(offlineDBManager.getAllDownloadedVouchersByBulkId(bulk_id));
        System.out.println("offline redownload"+downloadedVouchersList.get(0).getVoucherReDownload());
        offlineEachOfBulkTransactionHistoryAdapter = new OfflineEachOfBulkTransactionHistoryAdapter(getApplicationContext(), downloadedVouchersList, downloadedVouchersList_multiselect_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        transactionHistoryRecyclerview.setLayoutManager(linearLayoutManager);
        transactionHistoryRecyclerview.setHasFixedSize(false);
        transactionHistoryRecyclerview.setAdapter(offlineEachOfBulkTransactionHistoryAdapter);
        transactionHistoryRecyclerview.addOnItemTouchListener(new RecyclerItemClickListener(this, transactionHistoryRecyclerview, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect)
                    multi_select(position, "offline");
                else
                    Toast.makeText(getApplicationContext(), "Details Page", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    downloadedVouchersList_multiselect_list = new ArrayList<DownloadedVouchers>();
                    isMultiSelect = true;

                    if (mActionMode == null) {
                        mActionMode = startActionMode(mActionModeCallback);
                    }
                }

                multi_select(position, "offline");

            }
        }));
    }
    //endregion


    //region get Transaction History
    private void getTransactionHistory() {
        activityTransactionHistoryNewtonCradleLoading.start();
        try {
            applicationVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("**********Application Version is************* " + applicationVersion);

        HashMap<String, String> map = new HashMap<>();
        map.put("bulk_id", bulk_id);
        map.put("application_version", applicationVersion);

        APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
        myInterface.retailerGetEachOfBulkVoucherTransaction(User.getUserSessionToken(), APP_VERSION, map)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<ArrayList<BulkVoucherResponse>>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Response<ArrayList<BulkVoucherResponse>> responseTransactionHistory) {
                int fiveCount = 0;


                if (responseTransactionHistory.code() == 200) {
                    activityTransactionHistoryNewtonCradleLoading.stop();
//                    voucherList=  responseTransactionHistory.body().get(0).getVoucherId();
                    System.out.println("********************************************************");
                    transactionHistoryAdapter.add(responseTransactionHistory.body());
                    voucherList = responseTransactionHistory.body();
                    //print section
                    List<BulkVoucherResponse> lists = responseTransactionHistory.body();

                    serialStart.setText(lists.get(lists.size()-1).getVoucherId().getVoucherSerialNumber());
                    serialEnd.setText(" - "+lists.get(0).getVoucherId().getVoucherSerialNumber());

List<Voucher> copiedList=new ArrayList<>();
                    for (int i = 0; i < lists.size(); i++) {
//                        if (lists.get(i).getVoucherId().getVoucherAmount().equals("5.00")) {
//                            fiveCount += 1;
//                        }
//                        System.out.println("Birr is" + lists.get(i).getVoucherId().getVoucherAmount());
                        bulkPinList.add(lists.get(i).getVoucherId());
                        copiedList.add(lists.get(i).getVoucherId());
                        bulkPinList.get(i).setVoucherRowNumber(i+1);
                        bulkPinList.get(i).setBulkId(lists.get(i).getBulkId());
                        System.out.println("Vou*****"+bulkPinList.get(i).getVoucherRowNumber());

                    }


                } else if (responseTransactionHistory.code() == 400) {
                    activityTransactionHistoryNewtonCradleLoading.stop();

                    Toast.makeText(getApplicationContext(), "You Don't Have Any Transaction", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.someting_went_worong), Toast.LENGTH_SHORT).show();
                    System.out.println("Error code is" + responseTransactionHistory.code());

                }
                System.out.println("five count is+++++" + fiveCount);
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
        VoucherBulkPrintStatic.setOfflineBulkPrint(downloadedVouchersList);
        VoucherBulkPrintStatic.setIsVouchersLoaded(true);
        Intent printerIntent = new Intent();

            printerIntent = new Intent(EachOfBulkTransactionHistory.this, BluetoothPrinter.class);


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

    @Override
    protected void onResume() {
        super.onResume();
//        new CheckInternetConnection(this).checkConnection();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_common_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void multi_select(int position, String type) {
        if (mActionMode != null) {
            switch (type) {
                case "offline":
                    if (downloadedVouchersList_multiselect_list.contains(downloadedVouchersList.get(position)))
                        downloadedVouchersList_multiselect_list.remove(downloadedVouchersList.get(position));
                    else
                        downloadedVouchersList_multiselect_list.add(downloadedVouchersList.get(position));
                    deletedListPosition.add(position);
                    if (downloadedVouchersList_multiselect_list.size() > 0)
                        mActionMode.setTitle("" + downloadedVouchersList_multiselect_list.size());
                    else
                        mActionMode.setTitle("");

                    break;

                case "online":
                    if (multiselect_list.contains(voucherList.get(position)))
                        multiselect_list.remove(voucherList.get(position));
                    else
                        multiselect_list.add(voucherList.get(position));

                    if (multiselect_list.size() > 0)
                        mActionMode.setTitle("" + multiselect_list.size());
                    else
                        mActionMode.setTitle("");

            }


            refreshAdapter(type);

        }
    }


    public void refreshAdapter(String type) {
        if (type == "offline") {
            offlineEachOfBulkTransactionHistoryAdapter.selectedVoucherList = downloadedVouchersList_multiselect_list;
            offlineEachOfBulkTransactionHistoryAdapter.transactionList = downloadedVouchersList;
            offlineEachOfBulkTransactionHistoryAdapter.notifyDataSetChanged();
        } else {
            transactionHistoryAdapter.selectedVoucherList = multiselect_list;
            transactionHistoryAdapter.transactionList = voucherList;
            transactionHistoryAdapter.notifyDataSetChanged();
        }

    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_multi_select, menu);
            context_menu = menu;

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            MenuItem menuItemDelete = menu.findItem(R.id.action_delete);
            if (isNetworkConnected()) {
                menuItemDelete.setVisible(false);
            } else {
                menuItemDelete.setVisible(true);

            }

            return false; // Return false if nothing is done
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch (item.getItemId()) {
                case R.id.action_print:
                    printSelected();

                    return true;
                case R.id.action_delete:
                    deleteSelected();
                default:
                    return false;
            }

        }


        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            if (isNetworkConnected()) {
                isMultiSelect = false;
                multiselect_list = new ArrayList<BulkVoucherResponse>();
                refreshAdapter("online");
            } else {
                isMultiSelect = false;
                downloadedVouchersList_multiselect_list = new ArrayList<DownloadedVouchers>();
                refreshAdapter("offline");
            }

        }
    };

    private void deleteSelected() {
        selectedOfflinePinList.addAll(downloadedVouchersList_multiselect_list);
        System.out.println("@@@selected item to delte before remove" + downloadedVouchersList.size());
        System.out.println("@@@selected item to delte list positon before remove" + deletedListPosition.size());

        offlineDBManager.deleteVoucher(selectedOfflinePinList);
        if (mActionMode != null) {

            for (int i = 0; i < downloadedVouchersList_multiselect_list.size(); i++) {
                downloadedVouchersList.remove(downloadedVouchersList_multiselect_list.get(i));

//                offlineEachOfBulkTransactionHistoryAdapter.notifyItemRemoved(deletedListPosition.get(i));
//                offlineEachOfBulkTransactionHistoryAdapter.notifyItemRangeChanged(deletedListPosition.get(i),downloadedVouchersList.size());
            }
            mActionMode.finish();
            System.out.println("@@@selected item to delte after remove" + downloadedVouchersList.size());
            System.out.println("@@@selected item to delte list positon after remove" + deletedListPosition.size());
            toggleEmptyVoucherView();

        }

    }

    public void toggleEmptyVoucherView() {
        if (offlineDBManager.getCountOfPrintedVoucher(bulk_id) > 0) {
            emptyVoucherText.setVisibility(View.GONE);

        } else {
            emptyVoucherText.setVisibility(View.VISIBLE);
            print_all_btn.setVisibility(View.GONE);

        }
    }

    public void printSelected() {
        Intent printerIntent = new Intent();


        VoucherBulkPrintStatic.setIsVouchersLoaded(true);
        SharedPreferences sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

            printerIntent = new Intent(EachOfBulkTransactionHistory.this, BluetoothPrinter.class);


        if (isNetworkConnected()) {
            printerIntent.putExtra("offline", "false");
            printerIntent.putExtra("isReprint", "true");
            printerIntent.putExtra("isBulk", "true");

            for (int i = 0; i < multiselect_list.size(); i++) {
                System.out.println("selected is " + multiselect_list.get(i).getVoucherId().getVoucherSerialNumber());
                selectedPinList.add(multiselect_list.get(i).getVoucherId());
            }
            VoucherBulkPrintStatic.setBulkPrint(selectedPinList);
        } else {
            printerIntent.putExtra("offline", "true");
            printerIntent.putExtra("isReprint", "true");
            printerIntent.putExtra("isBulk", "true");

            printerIntent.putExtra("bulkid", bulk_id);
            System.out.println("Bulk id is From Offline" + bulk_id);
            selectedOfflinePinList.addAll(downloadedVouchersList_multiselect_list);
            VoucherBulkPrintStatic.setOfflineBulkPrint(selectedOfflinePinList);
        }

        startActivity(printerIntent);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}