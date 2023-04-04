package com.example.fibath.ui.statement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fibath.R;import com.example.fibath.classes.api.APIInterface;
import com.example.fibath.classes.model.FundResponse;


import com.example.fibath.classes.networking.Internet.CheckInternetConnection;
import com.example.fibath.classes.networking.communication.ServerCommunicator;
import com.example.fibath.classes.user.User;
import com.example.fibath.ui.home.NewHome;
import com.example.fibath.ui.statement.adapters.statement.StatementAdapter;
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

public class FundTransactionHistory extends AppCompatActivity {
    @BindView(R.id.activity_user_statement_newton_cradle_loading)
    RotateLoading activityUserStatementNewtonCradleLoading;
    Toolbar my_statement_screen_toolbar;
    RecyclerView userStatementRecyclerview;
    private StatementAdapter statementAdapter;
    private List<FundResponse> statementList = new ArrayList<>();
    String receiverId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_statement);
        new CheckInternetConnection(this).checkConnection();
        ButterKnife.bind(this);
        receiverId = getIntent().getStringExtra("receiverId");
        initUI();
        my_statement_screen_toolbar.setNavigationOnClickListener(v -> {
//                getApplicationContext().startActivity(new Intent(getApplicationContext(), Home.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//            getApplicationContext().startActivity(new Intent(getApplicationContext(), Voucher.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            getApplicationContext().startActivity(new Intent(getApplicationContext(), NewHome.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        });
    }

    // 1.1:region Init UI
    private void initUI() {
        my_statement_screen_toolbar = findViewById(R.id.my_statement_screen_toolbar);
        userStatementRecyclerview = findViewById(R.id.user_statement_screen_recyclerview);
        initRecyclerView();
        HashMap<String, String> map = new HashMap<>();
        map.put("receiver_id", receiverId);
        getTransactionHistory(map);
    }
//end region

    //1.2 region Init RecyclerView
    private void initRecyclerView() {
        statementAdapter = new StatementAdapter(getApplicationContext(), statementList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        userStatementRecyclerview.setLayoutManager(linearLayoutManager);
        userStatementRecyclerview.setHasFixedSize(false);
        userStatementRecyclerview.setAdapter(statementAdapter);
    }

    //endregion
    //region get Transaction History
    private void getTransactionHistory(HashMap<String, String> receiverId) {
        activityUserStatementNewtonCradleLoading.start();

        APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
        myInterface.getChildUserTransaction(User.getUserSessionToken(), APP_VERSION, receiverId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<ArrayList<FundResponse>>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Response<ArrayList<FundResponse>> responseStatements) {
                if (responseStatements.code() == 200) {
                    activityUserStatementNewtonCradleLoading.stop();

                    System.out.println("********************************************************");
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Preferences", MODE_PRIVATE);
                    assert sharedPreferences != null;
                    boolean showDeductedTransactions = sharedPreferences.getString("ShowDeductedTransactions", "1").equals("1");

                    if (!showDeductedTransactions) {
                        ArrayList<FundResponse> finalStatements = new ArrayList<>();
                        for (int i = 0; i < responseStatements.body().size(); i++) {
                            if (!responseStatements.body().get(i).getTransactionType().equals("Deducted") || !responseStatements.body().get(i).getTransactionType().equals("Non Trans Deduct")) {
                                finalStatements.add(responseStatements.body().get(i));
                            }
                        }
                        statementAdapter.add(finalStatements, "DEBIT");
                    } else {
                        statementAdapter.add(responseStatements.body(), "DEBIT");
                    }
                    System.out.println("********************************************************");
                } else if (responseStatements.code() == 400) {
                    activityUserStatementNewtonCradleLoading.stop();
                    Toast.makeText(getApplicationContext(), "You Don't Have Any Transaction", Toast.LENGTH_SHORT).show();
                } else {
                    activityUserStatementNewtonCradleLoading.stop();
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.someting_went_worong), Toast.LENGTH_SHORT).show();
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


    @Override
    protected void onResume() {
        super.onResume();
        new CheckInternetConnection(this).checkConnection();
    }
}
