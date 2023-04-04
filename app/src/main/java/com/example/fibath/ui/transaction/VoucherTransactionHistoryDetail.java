package com.example.fibath.ui.transaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.example.fibath.R;import com.example.fibath.ui.Printer.bluetooth.BluetoothPrinter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VoucherTransactionHistoryDetail extends AppCompatActivity {
    @BindView(R.id.transaction_history_detail_text_transaction_amount)
    TextView transactionHistoryDetailTextTransactionAmount;
    @BindView(R.id.transaction_history_detail_text_transaction_date)
    TextView transactionHistoryDetailTextTransactionDate;
    @BindView(R.id.transaction_history_detail_text_transaction_serial_number)
    TextView transactionHistoryDetailTextTransactionSerialNumber;
    @BindView(R.id.transaction_history_detail_text_transaction_status)
    TextView transactionHistoryDetailTextTransactionStatus;
    @BindView(R.id.transaction_history_detail_btn_print)
    TextView transactionHistoryDetailButtonPrint;

    private String transactionAmount;
    private String transactionDate;
    private String transactionExpiryDate;
    private String transactionSerialNumber;
    private String transactionStatus;
    private String transactionPinNumber;
    private int transactionReDownloaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_history_detail);
        ButterKnife.bind(this);
        getIntentData();
        initUI();
    }

    // 1.1:region Init UI
    private void initUI() {
        transactionHistoryDetailButtonPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent printerIntent=new Intent();


                    printerIntent = new Intent(VoucherTransactionHistoryDetail.this, BluetoothPrinter.class);


//                Intent printerIntent = new Intent(VoucherTransactionHistoryDetail.this, PrinterOptionStatic.class);
                printerIntent.putExtra("AMOUNT",  transactionAmount);
                printerIntent.putExtra("EXPIRY_DATE", transactionExpiryDate);
                printerIntent.putExtra("SERIAL_NUMBER", transactionSerialNumber);
                printerIntent.putExtra("PIN_NUMBER",transactionPinNumber);
                printerIntent.putExtra("TRANSACTION_REDOWNLOAD",transactionReDownloaded);
                printerIntent.putExtra("offline", "true");
                printerIntent.putExtra("isReprint", "true");
                printerIntent.putExtra("isBulk", "false");
                startActivity(printerIntent);
            }
        });
        getTransactionDetail();

    }
    //endregion

    private void getTransactionDetail() {
        transactionHistoryDetailTextTransactionAmount.setText(transactionAmount);
        transactionHistoryDetailTextTransactionDate.setText(transactionDate);
        transactionHistoryDetailTextTransactionSerialNumber.setText(transactionSerialNumber);
        transactionHistoryDetailTextTransactionStatus.setText(transactionStatus);
    }

    private void getIntentData() {
        transactionAmount = getIntent().getStringExtra("TRANSACTION_AMOUNT");
        transactionDate = getIntent().getStringExtra("TRANSACTION_DATE");
        transactionExpiryDate = getIntent().getStringExtra("TRANSACTION_EXPIRY_DATE");
        transactionSerialNumber = getIntent().getStringExtra("TRANSACTION_SERIAL_NUMBER");
        transactionStatus = getIntent().getStringExtra("TRANSACTION_STATUS");
        transactionReDownloaded = getIntent().getIntExtra("TRANSACTION_REDOWNLOAD",0);
        transactionPinNumber=getIntent().getStringExtra("TRANSACTION_PIN_NUMBER");
    }
}
