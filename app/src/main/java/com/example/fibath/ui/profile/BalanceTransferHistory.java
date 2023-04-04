package com.example.fibath.ui.profile;

import static com.example.fibath.classes.api.APIInterface.APP_VERSION;
import static com.example.fibath.classes.api.APIInterface.BASE_URL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.fibath.R;import com.example.fibath.classes.api.APIInterface;
import com.example.fibath.classes.model.FundResponse;
import com.example.fibath.classes.networking.communication.ServerCommunicator;
import com.example.fibath.classes.user.User;
import com.example.fibath.ui.statement.adapters.statement.StatementAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class BalanceTransferHistory extends AppCompatActivity {
    private StatementAdapter statementAdapter;
    private RecyclerView transactionWithUserList;
    private List<FundResponse> statementList = new ArrayList<>();
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_transfer_history);
        userID = getIntent().getStringExtra("userID");
        transactionWithUserList = findViewById(R.id.accountHistoryRecyclerView);
        initRecyclerView();
    }


    // region Init RecyclerView
    private void initRecyclerView() {
        statementAdapter = new StatementAdapter(getApplicationContext(), statementList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        transactionWithUserList.setLayoutManager(linearLayoutManager);
        transactionWithUserList.setHasFixedSize(false);
        transactionWithUserList.setAdapter(statementAdapter);
        HashMap<String, String> map = new HashMap<>();
        map.put("receiver_id", userID);
        getTransactionHistory(map);
    }

    //endregion
    //region get Transaction History
    private void getTransactionHistory(HashMap<String, String> receiverId) {

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
                    double totalTransactions = 0;
                    for (int i = 0; i < responseStatements.body().size(); i++) {
                        totalTransactions += Double.parseDouble(responseStatements.body().get(0).getTransactionAmount());
                    }
//                    profile_direct_signup_description.setText(String.valueOf(totalTransactions) + " " + getResources().getString(R.string.default_currency_short));

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
                } else if (responseStatements.code() == 400) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.user_profile_no_transaction), Toast.LENGTH_SHORT).show();
                } else {
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
}