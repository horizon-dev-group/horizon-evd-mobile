package com.example.fibath.ui.statement;

import android.os.Bundle;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.example.fibath.R;import com.example.fibath.classes.networking.Internet.CheckInternetConnection;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FundTransactionHistoryDetail extends AppCompatActivity {


    @BindView(R.id.user_statement_detail_screen_text_amount)
    TextView userStatementDetailTextAmount;
    @BindView(R.id.user_statement_detail_screen_text_date)
    TextView userStatementDetailTextDate;
//    @BindView(R.id.user_statement_detail_screen_text_opening_balance)
    TextView userStatementDetailTextOpeningBalance;
//    @BindView(R.id.user_statement_detail_screen_text_closing_balance)
    TextView userStatementDetailTextClosingBalance;
    @BindView(R.id.user_statement_detail_screen_text_response)
    TextView transactionHistoryDetailTextResponse;
    @BindView(R.id.user_statement_detail_screen_text_transaction_id)
    TextView transactionHistoryDetailTextTransactionId;
    private String userStatementDetailAmount;
    private String userStatementDetailDate;
//    private String userStatementDetailOpeningBalance;
//    private String userStatementDetailClosingBalance;
    private String userStatementDetailResponse;
    private String userStatementDetailTransactionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_statement_detail);
        new CheckInternetConnection(this).checkConnection();
        ButterKnife.bind(this);
        getIntentData();
        initUI();
    }

    // 1.1:region Init UI
    private void initUI() {

        getTransactionDetail();
    }
    //endregion


    private void getTransactionDetail() {
        userStatementDetailTextAmount.setText(userStatementDetailAmount);
        userStatementDetailTextDate.setText(userStatementDetailDate);
//        userStatementDetailTextOpeningBalance.setText(userStatementDetailOpeningBalance);
//        userStatementDetailTextClosingBalance.setText(userStatementDetailClosingBalance);
        transactionHistoryDetailTextResponse.setText(userStatementDetailResponse);
        transactionHistoryDetailTextTransactionId.setText(userStatementDetailTransactionId);
    }

    private void getIntentData() {
        userStatementDetailAmount = getIntent().getStringExtra("STATEMENT_AMOUNT");
        userStatementDetailDate = getIntent().getStringExtra("STATEMENT_DATE");
//        userStatementDetailOpeningBalance = getIntent().getStringExtra("STATEMENT_OPENING_BALANCE");
//        userStatementDetailClosingBalance = getIntent().getStringExtra("STATEMENT_CLOSING_BALANCE");
        userStatementDetailResponse = getIntent().getStringExtra("STATEMENT_RESPONSE");
        userStatementDetailTransactionId = getIntent().getStringExtra("STATEMENT_TRANSACTION_ID");
    }

    @Override
    protected void onResume() {
        super.onResume();
        new CheckInternetConnection(this).checkConnection();
    }
}
