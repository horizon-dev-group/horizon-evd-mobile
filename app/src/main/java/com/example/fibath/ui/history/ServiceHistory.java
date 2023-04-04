package com.example.fibath.ui.history;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.fibath.R;import com.example.fibath.ui.statement.FundTransactionHistory;
import com.example.fibath.ui.voucher.Voucher;

public class ServiceHistory extends AppCompatActivity {


    Toolbar serviceHistoryScreenToolbar;
    TextView statement;
    TextView service_history_screen_text_vouchers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_history);
        serviceHistoryScreenToolbar=findViewById(R.id.service_history_screen_toolbar);
        statement=findViewById(R.id.textViewStatement);
        service_history_screen_text_vouchers=findViewById(R.id.service_history_screen_text_vouchers);
        service_history_screen_text_vouchers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getApplicationContext().startActivity(new Intent(getApplicationContext(), VoucherHistory.class));

            }
        });
        statement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getApplicationContext().startActivity(new Intent(getApplicationContext(), FundTransactionHistory.class));
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            serviceHistoryScreenToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    getApplicationContext().startActivity(new Intent(getApplicationContext(), Home.class));
                    getApplicationContext().startActivity(new Intent(getApplicationContext(), Voucher.class));

//                    Toast.makeText(getApplicationContext(),"Back Button Pressed",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
