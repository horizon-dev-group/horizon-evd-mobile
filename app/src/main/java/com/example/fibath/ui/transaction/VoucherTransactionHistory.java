package com.example.fibath.ui.transaction;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fibath.R;import com.example.fibath.classes.api.APIInterface;

import com.example.fibath.classes.model.VoucherTransaction;
import com.example.fibath.classes.model.VoucherTransactionPagination;
import com.example.fibath.classes.networking.communication.ServerCommunicator;

import com.example.fibath.classes.state.StateManager;
import com.example.fibath.classes.user.User;
import com.example.fibath.ui.OfflineVoucher.database.OfflineDBManager;
import com.example.fibath.ui.OfflineVoucher.database.model.DownloadedVouchers;
import com.example.fibath.ui.home.NewHome;
import com.example.fibath.ui.transaction.adapters.transactionHistory.OfflineBulkTransactionHistoryAdapter;
import com.example.fibath.ui.transaction.adapters.transactionHistory.TransactionHistoryAdapter;
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

public class VoucherTransactionHistory extends AppCompatActivity {
    @BindView(R.id.activity_transaction_history_newton_cradle_loading)
    RotateLoading activityTransactionHistoryNewtonCradleLoading;
    Toolbar transactionHistoryScreenToolbar;
    RecyclerView transactionHistoryRecyclerview;
    NestedScrollView nested_scroll_view;
    private TransactionHistoryAdapter transactionHistoryAdapter;
    private OfflineBulkTransactionHistoryAdapter offlineBulkTransactionHistoryAdapter;
    private List<VoucherTransaction> voucherList = new ArrayList<>();
    private List<DownloadedVouchers> downloadedVouchersList = new ArrayList<>();
    private OfflineDBManager offlineDBManager;
    private TextView transactionHistoryTextBalance;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 5;
    private int currentPage = PAGE_START;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_history);
        if (isNetworkConnected()) {
            progressBar = (ProgressBar) findViewById(R.id.main_progress);
            initUI();
            initRecyclerView();
            Toast.makeText(getApplicationContext(), "Network Connected", Toast.LENGTH_SHORT).show();
        } else {
            offlineDBManager = new OfflineDBManager(this);
            initUI();
            initOfflineRecyclerView();
            Toast.makeText(getApplicationContext(), "Network Disconnected ", Toast.LENGTH_SHORT).show();

        }
        ButterKnife.bind(this);

        transactionHistoryScreenToolbar.setNavigationOnClickListener(v -> {
//            getApplicationContext().startActivity(new Intent(getApplicationContext(), Home.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//            getApplicationContext().startActivity(new Intent(getApplicationContext(), Voucher.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            getApplicationContext().startActivity(new Intent(getApplicationContext(), NewHome.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        });
    }

    // 1.1:region Init UI
    private void initUI() {
        transactionHistoryScreenToolbar = findViewById(R.id.transaction_history_screen_toolbar);
        transactionHistoryRecyclerview = findViewById(R.id.trasnsaction_history_screen_recyclerview);
        transactionHistoryTextBalance = findViewById(R.id.transaction_history_screen_text_balance);
        nested_scroll_view = findViewById(R.id.nested_scroll_view);
    }
    // end region

    //1.2 region Init RecyclerView
    private void initRecyclerView() {
        Double balance = 0d;
        try {
            balance = Double.parseDouble(String.valueOf(StateManager.getBalance()));
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Unable to get balance.", Toast.LENGTH_SHORT).show();
        }
        transactionHistoryTextBalance.setText(String.format("%.2f", balance) + " Birr");
        transactionHistoryAdapter = new TransactionHistoryAdapter(getApplicationContext(), voucherList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        transactionHistoryRecyclerview.setLayoutManager(linearLayoutManager);
        transactionHistoryRecyclerview.setHasFixedSize(false);
        transactionHistoryRecyclerview.setAdapter(transactionHistoryAdapter);
        nested_scroll_view.setOnScrollChangeListener(new PaginationScrollListenerForNestedScrollView(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                System.out.println("load more items++++++++++++++++++++++++++++++");
                isLoading = true;
                currentPage += 1;

//                 mocking network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LoadMore();
                    }
                }, 1000);

            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });


        getTransactionHistory();

    }

    //endregion

    //region init offline recyclerview
    private void initOfflineRecyclerView() {
        downloadedVouchersList.addAll(offlineDBManager.getAllBulkDownloadedVouchers());
        offlineBulkTransactionHistoryAdapter = new OfflineBulkTransactionHistoryAdapter(getApplicationContext(), downloadedVouchersList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        transactionHistoryRecyclerview.setLayoutManager(linearLayoutManager);
//        transactionHistoryRecyclerview.setHasFixedSize(false);
        transactionHistoryRecyclerview.setAdapter(offlineBulkTransactionHistoryAdapter);
    }
    //endregion


    //region get Transaction History
    private void getTransactionHistory() {
//        activityTransactionHistoryNewtonCradleLoading.start();
        HashMap<String, String> requestBody = new HashMap<>();

        HashMap<String, Integer> pageNumber = new HashMap<>();
        pageNumber.put("page", currentPage);

        APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
        myInterface.retailerGetVoucherTransaction(User.getUserSessionToken(), APP_VERSION, pageNumber)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<VoucherTransactionPagination>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Response<VoucherTransactionPagination> responseTransactionHistory) {

                progressBar.setVisibility(View.GONE);

                if (responseTransactionHistory.code() == 200) {
//                    activityTransactionHistoryNewtonCradleLoading.stop();
//                    voucherList=  responseTransactionHistory.body().get(0).getVoucherId();
                    System.out.println("********************************************************");
                    transactionHistoryAdapter.addAll(responseTransactionHistory.body().getTransaction());
//                    transactionHistoryTextBalance.setText(String.valueOf(StateManager.getBalance())+"0 Birr");
                    System.out.println("********************************************************");


                } else if (responseTransactionHistory.code() == 400) {
//                    activityTransactionHistoryNewtonCradleLoading.stop();

                    Toast.makeText(getApplicationContext(), "You Don't Have Any Transaction", Toast.LENGTH_SHORT).show();
//                    transactionHistoryAdapter.removeLoadingFooter();
//                    progressBar.setVisibility(View.GONE);

                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.someting_went_worong), Toast.LENGTH_SHORT).show();
                    System.out.println("Error code is" + responseTransactionHistory.code());

                }
                if (currentPage <= TOTAL_PAGES) transactionHistoryAdapter.addLoadingFooter();
                else isLastPage = true;
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


    private void LoadMore() {
        HashMap<String, Integer> pageNumber = new HashMap<>();
        pageNumber.put("page", currentPage);
        System.out.println("Current page is" + currentPage);
        APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
        myInterface.retailerGetVoucherTransaction(User.getUserSessionToken(), APP_VERSION, pageNumber)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<VoucherTransactionPagination>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Response<VoucherTransactionPagination> responseTransactionHistory) {
                transactionHistoryAdapter.removeLoadingFooter();
                isLoading = false;
//                System.out.println(" Really ******************************************************** "+responseTransactionHistory.code());


                if (responseTransactionHistory.code() == 200) {
//                    voucherList=  responseTransactionHistory.body().get(0).getVoucherId();
                    System.out.println("********************************************************");
                    transactionHistoryAdapter.addAll(responseTransactionHistory.body().getTransaction());
                    TOTAL_PAGES = responseTransactionHistory.body().getTotalPages();

//                    transactionHistoryTextBalance.setText(String.valueOf(StateManager.getBalance())+"0 Birr");
                    System.out.println(" Really ********************************************************" + TOTAL_PAGES);


                } else if (responseTransactionHistory.code() == 400) {
//                    activityTransactionHistoryNewtonCradleLoading.stop();

                    Toast.makeText(getApplicationContext(), "You Don't Have Any Transaction", Toast.LENGTH_SHORT).show();
//                    transactionHistoryAdapter.removeLoadingFooter();
//progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.someting_went_worong), Toast.LENGTH_SHORT).show();
                    System.out.println("Error code is" + responseTransactionHistory.code());

                }
                if (currentPage != TOTAL_PAGES) transactionHistoryAdapter.addLoadingFooter();
                else isLastPage = true;
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


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        new CheckInternetConnection(this).checkConnection();
    }
}
