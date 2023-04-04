package com.example.fibath.ui.OfflineVoucher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidstudy.networkmanager.Tovuti;
import com.example.fibath.R;import com.example.fibath.ui.OfflineVoucher.database.OfflineDBManager;
import com.example.fibath.ui.OfflineVoucher.database.model.DownloadedVouchers;
import com.example.fibath.ui.helpers.UIHelpers;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class DownloadedVoucher extends AppCompatActivity implements OnChartValueSelectedListener {
    TabLayout tabLayout;
    TextView availableQuantity;
    TextView selectedFaceValue;
    TextView offline_vouchers_balance;
    TextView offline_vouchers_sync_status;
    ImageView offline_vouchers_back_button;
    ArrayList<DownloadedVouchers> downloadedVouchers = new ArrayList<>();
    OfflineDBManager offlineDBManager;
    ArrayList NoOfEmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloaded_voucher);

        PieChart pieChart = findViewById(R.id.any_chart_view);
        availableQuantity = findViewById(R.id.offline_vouchers_available_quantity);
        selectedFaceValue = findViewById(R.id.offline_vouchers_selected_facevalue);
        offline_vouchers_balance = findViewById(R.id.offline_vouchers_balance);
        offline_vouchers_sync_status = findViewById(R.id.offline_vouchers_sync_status);
        offline_vouchers_back_button = findViewById(R.id.offline_vouchers_back_button);
        offlineDBManager = new OfflineDBManager(this);
        downloadedVouchers.addAll(offlineDBManager.getAllUnPrintedVoucher());
        NoOfEmp = new ArrayList();
        for (int i = 0; i < downloadedVouchers.size(); i++) {
            NoOfEmp.add(new PieEntry(Float.parseFloat(downloadedVouchers.get(i).getVoucherCount()), downloadedVouchers.get(i).getVoucherAmount() + "Birr"));
        }
        PieDataSet dataSet = new PieDataSet(NoOfEmp, "");
        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieChart.animateXY(300, 300);
        pieChart.setCenterText("Downloaded Vouchers");
        pieChart.setCenterTextSize(14);
        pieChart.setDrawEntryLabels(false);
        pieChart.setScrollBarSize(50);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setDrawCenterText(true);
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
        // add a selection listener
        pieChart.setOnChartValueSelectedListener(this);
        offline_vouchers_balance.setText(getOfflineAvailableQuantity(""));

        offline_vouchers_back_button.setOnClickListener(v -> onBackPressed());

        String syncStatus = new OfflineVouchersManager(getApplicationContext()).getUnSyncedVouchersCount();
        if (syncStatus.equals("EVERYTHING SYNCED")) {
            offline_vouchers_sync_status.setTextColor(Color.GREEN);
        } else if (syncStatus.endsWith("NOT SYNCED.")) {
            offline_vouchers_sync_status.setTextColor(Color.RED);
        }
        offline_vouchers_sync_status.setText(syncStatus);

        offline_vouchers_sync_status.setOnClickListener(v -> {
            String syncStatus1 = new OfflineVouchersManager(getApplicationContext()).getUnSyncedVouchersCount();
            if (syncStatus1.equals("EVERYTHING SYNCED")) {
                offline_vouchers_sync_status.setTextColor(Color.GREEN);
            } else if (syncStatus1.endsWith("NOT SYNCED.")) {
                offline_vouchers_sync_status.setTextColor(Color.RED);
            }
            offline_vouchers_sync_status.setText(syncStatus1);
        });

        UIHelpers.makeWindowTransparent(this);
        Tovuti.from(this).monitor((connectionType, isConnected, isFast) -> {
            if (isConnected) {
//                if(!OfflineVouchersManager.isSyncing()){
//                    new OfflineVouchersManager(getApplicationContext()).sync();
//                }else{
////                    Toast.makeText(getApplicationContext(),"Sync Already In Progress  Downloaded Voucher",Toast.LENGTH_LONG).show();
//                }
            }
        });
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null) {
            return;
        }
        int index = (int) h.getX();
        selectedFaceValue.setText(downloadedVouchers.get(index).getVoucherAmount() + " Birr Card");
        availableQuantity.setText(String.valueOf(e.getY()));
    }

    // region Get Offline Available Vouchers Quantity
    private String getOfflineAvailableQuantity(String faceValue) {
        List<DownloadedVouchers> unprintedDownloadedVouchers = offlineDBManager.getAllUnPrintedVoucher();
        int total = 0;
        for (int i = 0; i < unprintedDownloadedVouchers.size(); i++) {
//            if (faceValue.equals(unprintedDownloadedVouchers.get(i).getVoucherAmount())) {
            total += Integer.parseInt(unprintedDownloadedVouchers.get(i).getVoucherCount());
//            }
        }
        return String.valueOf(total);
    }
    // endregion

    @Override
    protected void onStop() {
        Tovuti.from(this).stop();
        super.onStop();
    }

    @Override
    public void onNothingSelected() {
        selectedFaceValue.setText("SELECT CARD ABOVE");
        availableQuantity.setText("0");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}