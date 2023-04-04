package com.example.fibath.ui.home.homeFragments;

import static com.example.fibath.classes.api.APIInterface.APP_VERSION;
import static com.example.fibath.classes.api.APIInterface.BASE_URL;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.example.fibath.R;
import com.example.fibath.databinding.ComingSoonPageBinding;
import com.example.fibath.classes.api.APIInterface;
import com.example.fibath.classes.model.FundHistoryModel;
import com.example.fibath.classes.model.SalesPerFaceValueModel;
import com.example.fibath.classes.model.TotalCardMoney;
import com.example.fibath.classes.model.WeeklySalesReportResponse;
import com.example.fibath.classes.networking.communication.ServerCommunicator;
import com.example.fibath.classes.user.User;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import io.ak1.BubbleTabBar;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class ReportFragment extends Fragment implements DatePickerDialog.OnDateSetListener, OnChartValueSelectedListener {
    private ComingSoonPageBinding binding;

    private WeeklySalesReportResponse totalSalesReportResponse;
    private SalesPerFaceValueModel totalSalesPerFaceValueResponse;
    // region BarChart Related
    private BarChart mChart;
    private String[] labels;
    private Float[] totalSalesPerDay;
    private Float[] totalCommissionPerDay;
    private BarDataSet set1;
    private BarDataSet set2;
    private ArrayList<IBarDataSet> dataSets;
    private BarData data;
    // region Settings

    // region PieChart Related
    PieChart reportCardSalesPerFaceValueChart;
    ArrayList<PieEntry> perFaceValueData;
    ArrayList<PieEntry> perFaceQuantityData;
    ArrayList<PieEntry> perFaceValueDataFinal;
    ArrayList<PieEntry> perFaceQuantityDataFinal;
    // endregion

    boolean hasNoSales = false;
    boolean shouldSkip0Values = true;
    boolean hasDataFetchIssue = false;
    // endregion

    Button reportDayPickerButton;
    Button reportWeekPickerButton;
    Button reportMonthPickerButton;
    TextView reportFilterDateRange;
    TextView totalSalesVolume;
    TextView reportErrorTitle;
    TextView reportErrorTitleDescription;

    TextView reportTotalReceivedFundAmount;
    TextView reportTotalReceivedFundQuantity;
    TextView reportTotalSentFundAmount;
    TextView reportTotalSentFundQuantity;

    TextView reportTotalSalesAmount;
    TextView reportTotalSalesProfitAmount;
    TextView reportTotalSalesQuantity;

    ProgressBar reportChartProgressBar;

    View view;
    Context context;
    Activity activity;

    // region UI Elements
    RecyclerView reportsListRecyclerView;
    BubbleTabBar fundTransactionNavigation;
    TextView fundTransactionTodayReceivedAmount, fundTransactionTodaySentAmount, fundTransactionBalanceTitle, fundTransactionBalance;
//    AnimatedTabLayout transactionTypeChangerTab;
    // endregion

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null) {
            return;
        }
        int index = (int) h.getX();
        Toast.makeText(getContext(), "Total Sales Amount: " + String.format("%.2f", perFaceValueData.get(index).getValue()) + " Br. | Total Sold Quantity: " + perFaceQuantityData.get(index).getValue(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected() {

    }

    public ReportFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ComingSoonPageBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        initUI(view);
        initEventHandlers();
        return view;
    }

//    @NonNull
//    @Override
//    public LayoutInflater onGetLayoutInflater(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
//        LayoutInflater inflater = super.onGetLayoutInflater(savedInstanceState);
//        Context contextThemeWrapper = new ContextThemeWrapper(requireContext(), R.style.ReportPageTheme);
//        return inflater.cloneInContext(contextThemeWrapper);
////        return super.onGetLayoutInflater(savedInstanceState);
//    }

    @Override
    public void onResume() {
        super.onResume();
//        Toast.makeText(getContext(), "Transfer Fragment Resumed", Toast.LENGTH_LONG).show();
    }

    // region Init UI
    private void initUI(View view) {
//        reportsListRecyclerView = view.findViewById(R.id.reportsListRecyclerView);
        mChart = view.findViewById(R.id.salesReportTimeSeries);
        reportDayPickerButton = view.findViewById(R.id.reportDayPickerButton);
        reportWeekPickerButton = view.findViewById(R.id.reportWeekPickerButton);
        reportMonthPickerButton = view.findViewById(R.id.reportMonthPickerButton);
        reportFilterDateRange = view.findViewById(R.id.reportFilterDateRange);
        totalSalesVolume = view.findViewById(R.id.totalSalesVolume);
        reportChartProgressBar = view.findViewById(R.id.reportChartProgressBar);
        reportErrorTitle = view.findViewById(R.id.reportNoDataAvailable);
        reportErrorTitleDescription = view.findViewById(R.id.reportNoDataAvailableDescription);

        reportTotalReceivedFundAmount = view.findViewById(R.id.reportTotalReceivedFundAmount);
        reportTotalReceivedFundQuantity = view.findViewById(R.id.reportTotalReceivedFundQuantity);
        reportTotalSentFundAmount = view.findViewById(R.id.reportTotalSentFundAmount);
        reportTotalSentFundQuantity = view.findViewById(R.id.reportTotalSentFundQuantity);

        reportTotalSalesAmount = view.findViewById(R.id.reportTotalSalesAmount);
        reportTotalSalesProfitAmount = view.findViewById(R.id.reportTotalSalesProfitAmount);
        reportTotalSalesQuantity = view.findViewById(R.id.reportTotalSalesQuantity);

        reportCardSalesPerFaceValueChart = view.findViewById(R.id.reportCardSalesPerFaceValueChart);
//        mChart.setDragEnabled(true);
//        mChart.setPinchZoom(true);
//        setupBarchart();
        getSalesDataForBarchart("FILTER", "week", "", "");
        getBalanceTransactionData("FILTER", "week", "", "");
        getSalesPerFaceValue("FILTER", "week", "", "");
//        setupBarchart1();
//        mChart.setOnChartValueSelectedListener(this);
//        mChart.getDescription().setEnabled(false);
    }
    // endregion

    // region Init Event Handlers
    private void initEventHandlers() {
        // region Header Buttons
        reportDayPickerButton.setOnClickListener(view1 -> {
            reportDayPickerButton.setBackgroundResource(R.drawable.report_border_only_button_background);
            reportWeekPickerButton.setBackgroundColor(Color.TRANSPARENT);
            reportMonthPickerButton.setBackgroundColor(Color.TRANSPARENT);
            getSalesDataForBarchart("FILTER", "today", "", "");
            getBalanceTransactionData("FILTER", "today", "", "");
            getSalesPerFaceValue("FILTER", "today", "", "");
        });

        reportWeekPickerButton.setOnClickListener(view1 -> {
            reportDayPickerButton.setBackgroundColor(Color.TRANSPARENT);
            reportWeekPickerButton.setBackgroundResource(R.drawable.report_border_only_button_background);
            reportMonthPickerButton.setBackgroundColor(Color.TRANSPARENT);
            getSalesDataForBarchart("FILTER", "week", "", "");
            getBalanceTransactionData("FILTER", "week", "", "");
            getSalesPerFaceValue("FILTER", "week", "", "");
        });

        reportMonthPickerButton.setOnClickListener(view1 -> {
            reportDayPickerButton.setBackgroundColor(Color.TRANSPARENT);
            reportWeekPickerButton.setBackgroundColor(Color.TRANSPARENT);
            reportMonthPickerButton.setBackgroundResource(R.drawable.report_border_only_button_background);
            getSalesDataForBarchart("FILTER", "month", "", "");
            getBalanceTransactionData("FILTER", "month", "", "");
            getSalesPerFaceValue("FILTER", "month", "", "");
        });
        // endregion

        // region Error Text Event Handlers
        reportErrorTitle.setOnClickListener(view1 -> {
            if (!hasDataFetchIssue) {
                shouldSkip0Values = false;
                setupSalesBarchart();
                reportErrorTitle.setVisibility(View.GONE);
                reportErrorTitleDescription.setVisibility(View.GONE);
                mChart.setVisibility(View.VISIBLE);
            }
        });

        reportErrorTitleDescription.setOnClickListener(view1 -> {
            if (!hasDataFetchIssue) {
                shouldSkip0Values = false;
                setupSalesBarchart();
                reportErrorTitle.setVisibility(View.GONE);
                reportErrorTitleDescription.setVisibility(View.GONE);
                mChart.setVisibility(View.VISIBLE);
            }
        });
        // endregion

        // region Date Range Picker
        binding.reportDateRangePicker.setOnClickListener(view1 -> {
            Calendar now = Calendar.getInstance();
            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                    ReportFragment.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
//            datePickerDialog.setThemeDark(true);
            datePickerDialog.setAccentColor(R.color.colorPrimaryDark);
            datePickerDialog.show(requireActivity().getFragmentManager(), "Select A Date Range");
        });
        // endregion

        // region Prepare Popup Menu
        PopupMenu popup = new PopupMenu(getActivity(), binding.reportReportSettings);
        popup.getMenuInflater().inflate(R.menu.report_settings, popup.getMenu());
        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper
                            .getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.hide0Values) {
                if (!shouldSkip0Values) {
                    item.setTitle("Show 0 Values");
                    item.setIcon(R.drawable.ic_eye_show_svgrepo_com);
                } else {
                    item.setTitle("Hide 0 Values");
                    item.setIcon(R.drawable.ic_hide_svgrepo_com);
                }
                shouldSkip0Values = !shouldSkip0Values;
                setupSalesBarchart();
            } else if (item.getItemId() == R.id.showChartValue) {
                for (IDataSet set : mChart.getData().getDataSets())
                    set.setDrawValues(!set.isDrawValuesEnabled());

                mChart.setMaxVisibleValueCount(100);
                mChart.invalidate();
            } else if (item.getItemId() == R.id.toggleGraphView) {
                reportErrorTitle.setVisibility(View.GONE);
                reportErrorTitleDescription.setVisibility(View.GONE);
                if (mChart.getVisibility() == View.GONE) {
                    mChart.setVisibility(View.VISIBLE);
                } else {
                    mChart.setVisibility(View.GONE);
                }
            }
            return true;
        });

        binding.reportReportSettings.setOnClickListener(view -> {
            popup.show();//showing popup menu
        });
    }
    // endregion

    // region Setup Barchart
    public void setupSalesBarchart() {
        // region Prepare Main Chart
        mChart.getDescription().setEnabled(false);
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);
        mChart.setDrawMarkers(true);
        mChart.setDrawValueAboveBar(true);
        mChart.setDrawMarkers(true);
        mChart.setNoDataTextColor(Color.WHITE);
        mChart.setHighlightPerTapEnabled(true);
        mChart.setMaxVisibleValueCount(10);
        mChart.getLegend().setEnabled(false); // Hide the legend
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                final String x = mChart.getXAxis().getValueFormatter().getFormattedValue(e.getX(), mChart.getXAxis());
                final String y = mChart.getAxisLeft().getValueFormatter().getFormattedValue(e.getY(), mChart.getAxisLeft());
                Toast.makeText(getContext(), "Date: " + x + " Value: " + y, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });
        // endregion

        // region Prepare Data
        WeeklySalesReportResponse finalProcessedData = prepareChartData(totalSalesReportResponse);
        labels = new String[finalProcessedData.getTotalCardMoney().size() + 2];
        totalSalesPerDay = new Float[finalProcessedData.getTotalCardMoney().size()];
        totalCommissionPerDay = new Float[finalProcessedData.getTotalCardMoney().size()];
        labels[0] = ""; // empty labels so that the names are spread evenly
        float totalSalesSum = 0;
        float totalProfitSum = 0;
        for (int i = 0; i < finalProcessedData.getTotalCardMoney().size(); i++) {
            String[] splattedDate = finalProcessedData.getTotalCardMoney().get(i).getId().split("-");
            labels[i + 1] = splattedDate[2] + "/" + splattedDate[1];
            totalSalesPerDay[i] = finalProcessedData.getTotalCardMoney().get(i).getTotalSoldAmount();
            totalCommissionPerDay[i] = finalProcessedData.getTotalCardMoney().get(i).getTotalCommissionAmount();
            totalSalesSum += finalProcessedData.getTotalCardMoney().get(i).getTotalSoldAmount();
            totalProfitSum += finalProcessedData.getTotalCardMoney().get(i).getTotalCommissionAmount();
        }
        labels[finalProcessedData.getTotalCardMoney().size() + 1] = ""; // empty labels so that the names are spread evenly
        if (finalProcessedData.getTotalCardMoney().size() > 0) {
            String[] reportStartDate = finalProcessedData.getTotalCardMoney().get(0).getId().split("-");
            String[] reportEndDate = finalProcessedData.getTotalCardMoney().get(finalProcessedData.getTotalCardMoney().size() - 1).getId().split("-");
            reportFilterDateRange.setText(reportStartDate[2] + "/" + reportStartDate[1] + "/" + reportStartDate[0] + " - " + reportEndDate[2] + "/" + reportEndDate[1] + "/" + reportEndDate[0]);
        }
        totalSalesVolume.setText(String.format("%.2f", totalSalesSum) + " Br.");
        reportTotalSalesAmount.setText(String.format("%.2f", totalSalesSum) + " Br.");
        reportTotalSalesProfitAmount.setText(String.format("%.2f", totalProfitSum) + " Br.");
        // endregion

        // region xAxis Configuration
        XAxis xAxis = mChart.getXAxis();
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setTextColor(Color.WHITE);
        xAxis.setTextSize(12);
        xAxis.setDrawAxisLine(true);
        xAxis.setAxisLineColor(Color.parseColor("#919191"));
        xAxis.setAxisMinimum(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setAxisMaximum(labels.length - 1.1f);
        // endregion

        // region yAxis Configuration
        YAxis leftYAxis = mChart.getAxisLeft();
        leftYAxis.setTextColor(Color.WHITE);
        leftYAxis.setTextSize(12);
        leftYAxis.setDrawAxisLine(false);
        leftYAxis.setAxisMinimum(0.0f);
        leftYAxis.setDrawGridLines(true);
        leftYAxis.setGranularity(2);
        leftYAxis.setLabelCount(8, true);
        leftYAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftYAxis.setValueFormatter(new LargeValueFormatter());

        YAxis rightYAxis = mChart.getAxisRight();
        rightYAxis.setTextColor(Color.WHITE);
        rightYAxis.setTextSize(12);
        rightYAxis.setDrawAxisLine(false);
        rightYAxis.setAxisMinimum(0.0f);
        rightYAxis.setDrawGridLines(false);
        rightYAxis.setGranularity(2);
        rightYAxis.setLabelCount(8, true);
        rightYAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        rightYAxis.setValueFormatter(new LargeValueFormatter());
        // endregion

        // region Configure Bars
        ArrayList<BarEntry> barOne = new ArrayList<>();
        ArrayList<BarEntry> barTwo = new ArrayList<>();
        for (int i = 0; i < totalSalesPerDay.length; i++) {
            barOne.add(new BarEntry(i, totalSalesPerDay[i]));
            barTwo.add(new BarEntry(i, totalCommissionPerDay[i]));
        }
        // endregion

        // region Configure Set 1
        set1 = new BarDataSet(barOne, "Total Sales");
        set1.setColor(Color.parseColor("#14D699"));
        set1.setHighlightEnabled(true); // allow highlighting for DataSet
//        set1.setDrawValues(true);
        set1.setValueTextColor(Color.WHITE);
        set1.setValueFormatter(new LargeValueFormatter());
        // endregion

        // region Configure Set 2
        set2 = new BarDataSet(barTwo, "Total Profit");
        set2.setColor(Color.parseColor("#C8C520"));
        set2.setHighlightEnabled(true); // allow highlighting for DataSet
//        set2.setDrawValues(true);
        set2.setValueTextColor(Color.WHITE);
        set2.setValueFormatter(new LargeValueFormatter());
        // endregion

        dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);
        dataSets.add(set2);

        float groupSpace = 0.8f;
        float barSpace = 0f;
        float barWidth = 0.1f;
        // (barSpace + barWidth) * 2 + groupSpace = 1
        data = new BarData(dataSets);
        data.setBarWidth(barWidth);
        // so that the entire chart is shown when scrolled from right to left

        mChart.setData(data);
        mChart.setFitBars(true);
        mChart.animateY(700);
        //        mChart.setScaleEnabled(false);
        mChart.setVisibleXRangeMaximum(7f);
        mChart.groupBars(1f, groupSpace, barSpace);

//            for (IDataSet set : mChart.getData().getDataSets())
//                set.setDrawValues(true);
//            set1.setValueFormatter(new LargeValueFormatter());
//            set1.setDrawValues(true);
//            set1.setValueTextColor(Color.WHITE);
//            for (IDataSet set : mChart.getData().getDataSets())
//                set2.setValueFormatter(new LargeValueFormatter());
//            set2.setDrawValues(true);
//            set2.setValueTextColor(Color.WHITE);
//            Toast.makeText(getContext(), "Values Are Visible", Toast.LENGTH_SHORT).show();

        if (hasNoSales) {
            reportErrorTitle.setText("No Sales");
            reportErrorTitle.setVisibility(View.VISIBLE);
            reportErrorTitleDescription.setText("You haven't sold in this period, Tap Here to show the graph.");
            reportErrorTitleDescription.setVisibility(View.VISIBLE);
            mChart.setVisibility(View.GONE);
        } else {
            reportErrorTitle.setVisibility(View.GONE);
            reportErrorTitleDescription.setVisibility(View.GONE);
            mChart.setVisibility(View.VISIBLE);
        }

        mChart.invalidate();
    }

    // endregion

    // region Setup Pie Chart
    // region Setup Pie Chart
    private void preparePieChart() {
        perFaceValueData = new ArrayList<>();
        perFaceQuantityData = new ArrayList<>();
        ArrayList<Integer> perFaceValueDataColors = new ArrayList<>();

        if (totalSalesPerFaceValueResponse.getFiveBirrQuantity() > 0) {
            perFaceValueData.add(new PieEntry(totalSalesPerFaceValueResponse.getFiveBirrSale(), "5 Birr Sale"));
            perFaceQuantityData.add(new PieEntry(totalSalesPerFaceValueResponse.getFiveBirrQuantity(), "5 Birr Sale"));
            perFaceValueDataColors.add(Color.parseColor("#A40E4C"));
        }

        if (totalSalesPerFaceValueResponse.getTenBirrQuantity() > 0) {
            perFaceValueData.add(new PieEntry(totalSalesPerFaceValueResponse.getTenBirrSale(), "10 Birr Sale"));
            perFaceQuantityData.add(new PieEntry(totalSalesPerFaceValueResponse.getTenBirrQuantity(), "10 Birr Sale"));
            perFaceValueDataColors.add(Color.parseColor("#34D1BF"));
        }

        if (totalSalesPerFaceValueResponse.getFifteenBirrQuantity() > 0) {
            perFaceValueData.add(new PieEntry(totalSalesPerFaceValueResponse.getFifteenBirrSale(), "15 Birr Sale"));
            perFaceQuantityData.add(new PieEntry(totalSalesPerFaceValueResponse.getFifteenBirrQuantity(), "15 Birr Sale"));
            perFaceValueDataColors.add(Color.parseColor("#D81E5B"));
        }

        if (totalSalesPerFaceValueResponse.getTwentyBirrQuantity() > 0) {
            perFaceValueData.add(new PieEntry(totalSalesPerFaceValueResponse.getTwentyBirrSale(), "20 Birr Sale"));
            perFaceQuantityData.add(new PieEntry(totalSalesPerFaceValueResponse.getTwentyBirrQuantity(), "20 Birr Sale"));
            perFaceValueDataColors.add(Color.parseColor("#70C1B3"));
        }

        if (totalSalesPerFaceValueResponse.getTwentyFiveBirrQuantity() > 0) {
            perFaceValueData.add(new PieEntry(totalSalesPerFaceValueResponse.getTwentyFiveBirrSale(), "25 Birr Sale"));
            perFaceQuantityData.add(new PieEntry(totalSalesPerFaceValueResponse.getTwentyFiveBirrQuantity(), "25 Birr Sale"));
            perFaceValueDataColors.add(Color.parseColor("#ACC3A6"));
        }

        if (totalSalesPerFaceValueResponse.getFiftyBirrQuantity() > 0) {
            perFaceValueData.add(new PieEntry(totalSalesPerFaceValueResponse.getFiftyBirrSale(), "50 Birr Sale"));
            perFaceQuantityData.add(new PieEntry(totalSalesPerFaceValueResponse.getFiftyBirrQuantity(), "50 Birr Sale"));
            perFaceValueDataColors.add(Color.parseColor("#F5D6BA"));
        }

        if (totalSalesPerFaceValueResponse.getHundredBirrQuantity() > 0) {
            perFaceValueData.add(new PieEntry(totalSalesPerFaceValueResponse.getHundredBirrSale(), "100 Birr Sale"));
            perFaceQuantityData.add(new PieEntry(totalSalesPerFaceValueResponse.getHundredBirrQuantity(), "100 Birr Sale"));
            perFaceValueDataColors.add(Color.parseColor("#B6A39E"));
        }

        if (totalSalesPerFaceValueResponse.getTwoHundredBirrQuantity() > 0) {
            perFaceValueData.add(new PieEntry(totalSalesPerFaceValueResponse.getTwoHundredBirrSale(), "200 Birr Sale"));
            perFaceQuantityData.add(new PieEntry(totalSalesPerFaceValueResponse.getTwoHundredBirrQuantity(), "200 Birr Sale"));
            perFaceValueDataColors.add(Color.parseColor("#A9E5BB"));
        }

        if (totalSalesPerFaceValueResponse.getTwoHundredFiftyBirrQuantity() > 0) {
            perFaceValueData.add(new PieEntry(totalSalesPerFaceValueResponse.getTwoHundredFiftyBirrSale(), "250 Birr Sale"));
            perFaceQuantityData.add(new PieEntry(totalSalesPerFaceValueResponse.getTwoHundredFiftyBirrQuantity(), "250 Birr Sale"));
            perFaceValueDataColors.add(Color.parseColor("#5C374C"));
        }

        if (totalSalesPerFaceValueResponse.getFiveHundredBirrQuantity() > 0) {
            perFaceValueData.add(new PieEntry(totalSalesPerFaceValueResponse.getFiveHundredBirrSale(), "500 Birr Sale"));
            perFaceQuantityData.add(new PieEntry(totalSalesPerFaceValueResponse.getFiveHundredBirrQuantity(), "500 Birr Sale"));
            perFaceValueDataColors.add(Color.parseColor("#F49D6E"));
        }

        if (totalSalesPerFaceValueResponse.getOneThousandBirrQuantity() > 0) {
            perFaceValueData.add(new PieEntry(totalSalesPerFaceValueResponse.getOneThousandBirrSale(), "1000 Birr Sale"));
            perFaceQuantityData.add(new PieEntry(totalSalesPerFaceValueResponse.getOneThousandBirrQuantity(), "1000 Birr Sale"));
            perFaceValueDataColors.add(Color.parseColor("#F72C25"));
        }

        float totalSalesQuantity = totalSalesPerFaceValueResponse.getFiveBirrQuantity() +
                totalSalesPerFaceValueResponse.getTenBirrQuantity() +
                totalSalesPerFaceValueResponse.getFifteenBirrQuantity() +
                totalSalesPerFaceValueResponse.getTwentyBirrQuantity() +
                totalSalesPerFaceValueResponse.getTwentyFiveBirrQuantity() +
                totalSalesPerFaceValueResponse.getFiftyBirrQuantity() +
                totalSalesPerFaceValueResponse.getHundredBirrQuantity() +
                totalSalesPerFaceValueResponse.getTwoHundredBirrQuantity() +
                totalSalesPerFaceValueResponse.getTwoHundredFiftyBirrQuantity() +
                totalSalesPerFaceValueResponse.getFiveHundredBirrQuantity() +
                totalSalesPerFaceValueResponse.getOneThousandBirrQuantity();

        reportTotalSalesQuantity.setText(String.valueOf(totalSalesQuantity));

        perFaceValueDataFinal = new ArrayList<>();
        perFaceQuantityDataFinal = new ArrayList<>();

        ArrayList<Integer> perFaceValueDataColorsFinal = new ArrayList<>();
        for (int i = 0; i < perFaceValueData.size(); i++) {
            if (perFaceQuantityData.get(i).getValue() > 0) {
                System.out.println("FACE VALUE" + perFaceValueData.get(i).getValue());
                System.out.println("QuANTITY VALUE" + perFaceQuantityData.get(i).getValue());
                perFaceValueDataFinal.add(perFaceValueData.get(i));
                perFaceValueDataColorsFinal.add(perFaceValueDataColors.get(i));
            }
        }

        PieDataSet dataSet = new PieDataSet(perFaceValueDataFinal, "");
        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new LargeValueFormatter());
        reportCardSalesPerFaceValueChart.setData(pieData);
        dataSet.setColors(perFaceValueDataColorsFinal);
        reportCardSalesPerFaceValueChart.animateY(1400, Easing.EaseInOutQuad);
        reportCardSalesPerFaceValueChart.setCenterText("Per Face Value");
        reportCardSalesPerFaceValueChart.getLegend().setEnabled(false);
        reportCardSalesPerFaceValueChart.setCenterTextSize(14);
        reportCardSalesPerFaceValueChart.setDrawEntryLabels(false);
        reportCardSalesPerFaceValueChart.setScrollBarSize(50);
        reportCardSalesPerFaceValueChart.getDescription().setEnabled(false);
//        reportCardSalesPerFaceValueChart.setExtraOffsets(5, 10, 5, 5);
        reportCardSalesPerFaceValueChart.setDragDecelerationFrictionCoef(0.95f);
        reportCardSalesPerFaceValueChart.setDrawHoleEnabled(true);
        reportCardSalesPerFaceValueChart.setHoleColor(Color.WHITE);
        reportCardSalesPerFaceValueChart.setTransparentCircleColor(Color.WHITE);
        reportCardSalesPerFaceValueChart.setTransparentCircleAlpha(110);
        reportCardSalesPerFaceValueChart.setHoleRadius(58f);
        reportCardSalesPerFaceValueChart.setTransparentCircleRadius(61f);
        reportCardSalesPerFaceValueChart.setDrawCenterText(true);
        reportCardSalesPerFaceValueChart.setRotationAngle(0);
        reportCardSalesPerFaceValueChart.setRotationEnabled(true);
        reportCardSalesPerFaceValueChart.setHighlightPerTapEnabled(true);
        // add a selection listener
        reportCardSalesPerFaceValueChart.setOnChartValueSelectedListener(this);

        Legend l = reportCardSalesPerFaceValueChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        reportCardSalesPerFaceValueChart.setEntryLabelColor(Color.WHITE);
        reportCardSalesPerFaceValueChart.setEntryLabelTextSize(12f);
    }
    // endregion

    // region Prepare Chart Data
    private WeeklySalesReportResponse prepareChartData(WeeklySalesReportResponse unprocessedReport) {
        WeeklySalesReportResponse finalReport = new WeeklySalesReportResponse();
        List<TotalCardMoney> finalTotalCardMoney = new ArrayList<TotalCardMoney>();

//        public void setTotalCardMoney(List< TotalCardMoney > totalCardMoney) {
//            this.totalCardMoney = totalCardMoney;
//        }
//        totalSalesReportResponse
        if (shouldSkip0Values) {
            int positiveValueCounter = 0;
            for (int i = 0; i < unprocessedReport.getTotalCardMoney().size(); i++) {
                if (unprocessedReport.getTotalCardMoney().get(i).getTotalSoldAmount() >= 1) {
                    positiveValueCounter++;
                    System.out.println("Total: " + unprocessedReport.getTotalCardMoney().get(i).getTotalSoldAmount());
                    System.out.println("Commission: " + unprocessedReport.getTotalCardMoney().get(i).getTotalCommissionAmount());
                    finalTotalCardMoney.add(unprocessedReport.getTotalCardMoney().get(i));
                }
            }

            if (positiveValueCounter > 0) {
                finalReport.setTotalCardMoney(finalTotalCardMoney);
                hasNoSales = false;
            } else {
                finalReport.setTotalCardMoney(finalTotalCardMoney);
                hasNoSales = true;
            }

            return finalReport;
        } else {
            return unprocessedReport;
        }
    }
    // endregion

    // region Get Barchart Data
    private void getSalesDataForBarchart(String type, String currentQuery, String startDate, String endDate) {
        reportChartProgressBar.setVisibility(View.VISIBLE);
        reportErrorTitle.setVisibility(View.GONE);
        reportErrorTitleDescription.setVisibility(View.GONE);
        mChart.setVisibility(View.GONE);

        HashMap<String, String> agentData = new HashMap<>();
        agentData.put("filterStartDate", startDate);
        agentData.put("filterEndDate", endDate);
        agentData.put("user_id", User.getUserSessionId());

        if (type.equals("FILTER")) {
            APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
            myInterface.getFilteredSalesReport(User.getUserSessionToken(), APP_VERSION, agentData, "false", currentQuery)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<WeeklySalesReportResponse>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Response<WeeklySalesReportResponse> weeklySalesReport) {
                            if (weeklySalesReport.code() == 200) {
                                totalSalesReportResponse = weeklySalesReport.body();
                                hasDataFetchIssue = false;
                                setupSalesBarchart();
                            } else if (weeklySalesReport.code() == 400) {
                                hasDataFetchIssue = true;
                            } else {
                                hasDataFetchIssue = true;
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            System.out.println("has Error: " + e.toString());
                            reportErrorTitle.setText("Something Went Wrong");
                            reportErrorTitle.setVisibility(View.VISIBLE);
                            reportErrorTitleDescription.setText("Unable to fetch data, please check your internet.");
                            reportErrorTitleDescription.setVisibility(View.VISIBLE);
                            reportChartProgressBar.setVisibility(View.GONE);
                            hasDataFetchIssue = true;
                        }

                        @Override
                        public void onComplete() {
                            reportChartProgressBar.setVisibility(View.GONE);
                        }
                    });
        } else if (type.equals("DATE_RANGE")) {
            APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
            myInterface.getFilteredSalesReport(User.getUserSessionToken(), APP_VERSION, agentData, "true", currentQuery)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<WeeklySalesReportResponse>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Response<WeeklySalesReportResponse> weeklySalesReport) {
                            if (weeklySalesReport.code() == 200) {
                                totalSalesReportResponse = weeklySalesReport.body();
                                hasDataFetchIssue = false;
                                setupSalesBarchart();
//                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.dialog_fund_request_sent), Toast.LENGTH_SHORT).show();
//                    dialogFundRequestAmount.setText("");
//                    dialogFundRequestBankName.setText("");
//                    dialogFundRequestTransactionRef.setText("");
//                    dialogFundRequestCRV.setText("");
//                    dialogFundRequestRemark.setText("");
//
//                    MaterialDialog dialog = new MaterialDialog.Builder(FundRequest.this)
//                            .customView(R.layout.dialog_lottie_message, false)
//                            .cancelable(true)
//                            .autoDismiss(true)
//                            .build();
//                    assert dialog.getCustomView() != null;
//                    LottieAnimationView dialogLottieAnimation = dialog.getCustomView().findViewById(R.id.dialogLottieAnimation);
//                    TextView dialogLottieTitle = dialog.getCustomView().findViewById(R.id.dialogLottieTitle);
//                    TextView dialogLottieDescription = dialog.getCustomView().findViewById(R.id.dialogLottieDescription);
//                    dialogLottieAnimation.setAnimation("success-dialog.json");
//                    dialogLottieTitle.setText(getResources().getString(R.string.dialog_success));
//                    dialogLottieDescription.setText(getResources().getString(R.string.dialog_fund_request_sent_title));
//                    if (!dialog.isShowing()) dialog.show();
//                    getAllSentRequests();
                            } else if (weeklySalesReport.code() == 400) {
                                reportErrorTitle.setText("Something Went Wrong");
                                reportErrorTitle.setVisibility(View.VISIBLE);
                                reportErrorTitleDescription.setText("Unable to fetch data, please check your internet.");
                                reportErrorTitleDescription.setVisibility(View.VISIBLE);
                                mChart.setVisibility(View.GONE);
                                hasDataFetchIssue = true;
//                    System.out.println("error is" + weeklySalesReport.body());
//
////                    Toast.makeText(getApplicationContext(), "Unable To Register Agents", Toast.LENGTH_SHORT).show();
//
////                    final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(MyAgents.this)
////                            .setBackgroundColor(R.color.colorPrimary)
////                            .setimageResource(R.drawable.ic_question_mark)
////                            .setTextTitle("Error")
////                            .setTitleColor(R.color.colorLightWhite)
////                            .setTextSubTitle("User already exists.")
////                            .setSubtitleColor(R.color.colorLightWhite)
////                            .setBodyColor(R.color.red)
////                            .setPositiveButtonText("Yes")
////                            .setNegativeButtonText("No")
////                            .setOnNegativeClicked((view12, dialog) -> dialog.dismiss())
////                            .setPositiveColor(R.color.colorLightWhite)
////                            .setOnPositiveClicked((view1, dialog) -> {
////                                dialog.dismiss();
////                            })
////                            .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
////                            .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
////                            .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
////                            .build();
////                    alert.show();
//
//                    final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(ComingSoonFragment.this)
//                            .setBackgroundColor(R.color.colorPrimaryWarning)
//                            .setimageResource(R.drawable.ic_dialog_warning)
//                            .setTextTitle(getResources().getString(R.string.dialog_error))
//                            .setTitleColor(R.color.colorLightWhite)
//                            .setTextSubTitle(getResources().getString(R.string.dialog_fund_request_unable_to_create_request))
//                            .setSubtitleColor(R.color.colorLightWhite)
//                            .setBodyColor(R.color.red)
//                            .setPositiveButtonText(getResources().getString(R.string.dialog_okay))
//                            .setPositiveColor(R.color.colorLightWhite)
//                            .setOnPositiveClicked((view1, dialog) -> {
//                                dialog.dismiss();
//                            })
//                            .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
//                            .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
//                            .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
//                            .build();
//                    alert.show();
                            } else {
                                reportErrorTitle.setText("Something Went Wrong");
                                reportErrorTitle.setVisibility(View.VISIBLE);
                                reportErrorTitleDescription.setText("Unable to fetch data, please check your internet.");
                                reportErrorTitleDescription.setVisibility(View.VISIBLE);
                                mChart.setVisibility(View.GONE);
                                hasDataFetchIssue = true;
//                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.someting_went_worong), Toast.LENGTH_SHORT).show();
//                    System.out.println("Error code is" + weeklySalesReport.code());
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            System.out.println("has Error: " + e.toString());
                            reportErrorTitle.setText("Something Went Wrong");
                            reportErrorTitle.setVisibility(View.VISIBLE);
                            reportErrorTitleDescription.setText("Unable to fetch data, please check your internet.");
                            reportErrorTitleDescription.setVisibility(View.VISIBLE);
                            reportChartProgressBar.setVisibility(View.GONE);
                            hasDataFetchIssue = true;
                        }

                        @Override
                        public void onComplete() {
                            reportChartProgressBar.setVisibility(View.GONE);
                        }
                    });
        }
    }
    // endregion

    // region Get Balance Transaction Data
    private void getBalanceTransactionData(String type, String currentQuery, String startDate, String endDate) {
        HashMap<String, String> agentData = new HashMap<>();
        agentData.put("filterStartDate", startDate);
        agentData.put("filterEndDate", endDate);
        agentData.put("user_id", User.getUserSessionId());

        if (type.equals("FILTER")) {
            APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
            myInterface.getFundTransactionHistory(User.getUserSessionToken(), APP_VERSION, agentData, "false", currentQuery)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<FundHistoryModel>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Response<FundHistoryModel> fundTransactionHistory) {
                            if (fundTransactionHistory.code() == 200) {
                                reportTotalReceivedFundAmount.setText(String.format("%.2f", fundTransactionHistory.body().getReceivedBirrAmount()) + " Br.");
                                reportTotalReceivedFundQuantity.setText(fundTransactionHistory.body().getReceivedQuantity() + " Tr.");
                                reportTotalSentFundAmount.setText(String.format("%.2f", fundTransactionHistory.body().getSentBirrAmount()) + " Br.");
                                reportTotalSentFundQuantity.setText(fundTransactionHistory.body().getSentQuantity() + " Tr.");
                            } else if (fundTransactionHistory.code() == 400) {
                                reportTotalReceivedFundAmount.setText("-" + " Br.");
                                reportTotalReceivedFundQuantity.setText("-" + " Tr.");
                                reportTotalSentFundAmount.setText("-" + " Br.");
                                reportTotalSentFundQuantity.setText("-" + " Tr.");
                            } else {
                                reportTotalReceivedFundAmount.setText("-" + " Br.");
                                reportTotalReceivedFundQuantity.setText("-" + " Tr.");
                                reportTotalSentFundAmount.setText("-" + " Br.");
                                reportTotalSentFundQuantity.setText("-" + " Tr.");
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            reportTotalReceivedFundAmount.setText("-" + " Br.");
                            reportTotalReceivedFundQuantity.setText("-" + " Tr.");
                            reportTotalSentFundAmount.setText("-" + " Br.");
                            reportTotalSentFundQuantity.setText("-" + " Tr.");
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else if (type.equals("DATE_RANGE")) {
            APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
            myInterface.getFundTransactionHistory(User.getUserSessionToken(), APP_VERSION, agentData, "true", currentQuery)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<FundHistoryModel>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Response<FundHistoryModel> fundTransactionHistory) {
                            if (fundTransactionHistory.code() == 200) {
                                reportTotalReceivedFundAmount.setText(String.format("%.2f", fundTransactionHistory.body().getReceivedBirrAmount()) + " Br.");
                                reportTotalReceivedFundQuantity.setText(String.format("%.2f", fundTransactionHistory.body().getReceivedQuantity()) + " Tr.");
                                reportTotalSentFundAmount.setText(String.format("%.2f", fundTransactionHistory.body().getSentBirrAmount()) + " Br.");
                                reportTotalSentFundQuantity.setText(String.format("%.2f", fundTransactionHistory.body().getSentQuantity()) + " Tr.");
                            } else if (fundTransactionHistory.code() == 400) {
                                reportTotalReceivedFundAmount.setText("-" + " Br.");
                                reportTotalReceivedFundQuantity.setText("-" + " Tr.");
                                reportTotalSentFundAmount.setText("-" + " Br.");
                                reportTotalSentFundQuantity.setText("-" + " Tr.");
                            } else {
                                reportTotalReceivedFundAmount.setText("-" + " Br.");
                                reportTotalReceivedFundQuantity.setText("-" + " Tr.");
                                reportTotalSentFundAmount.setText("-" + " Br.");
                                reportTotalSentFundQuantity.setText("-" + " Tr.");
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            reportTotalReceivedFundAmount.setText("-" + " Br.");
                            reportTotalReceivedFundQuantity.setText("-" + " Tr.");
                            reportTotalSentFundAmount.setText("-" + " Br.");
                            reportTotalSentFundQuantity.setText("-" + " Tr.");
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }
    // endregion

    // region Get Sales Per Face Value
    private void getSalesPerFaceValue(String type, String currentQuery, String startDate, String endDate) {
        HashMap<String, String> agentData = new HashMap<>();
        agentData.put("filterStartDate", startDate);
        agentData.put("filterEndDate", endDate);
        agentData.put("user_id", User.getUserSessionId());

        if (type.equals("FILTER")) {
            APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
            myInterface.getSalesPerFaceValue(User.getUserSessionToken(), APP_VERSION, agentData, "false", currentQuery)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<SalesPerFaceValueModel>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Response<SalesPerFaceValueModel> fundTransactionHistory) {
                            if (fundTransactionHistory.code() == 200) {
                                totalSalesPerFaceValueResponse = fundTransactionHistory.body();
                                preparePieChart();
                            } else if (fundTransactionHistory.code() == 400) {

                            } else {

                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else if (type.equals("DATE_RANGE")) {
            APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
            myInterface.getSalesPerFaceValue(User.getUserSessionToken(), APP_VERSION, agentData, "true", currentQuery)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<SalesPerFaceValueModel>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Response<SalesPerFaceValueModel> fundTransactionHistory) {
                            if (fundTransactionHistory.code() == 200) {
                                totalSalesPerFaceValueResponse = fundTransactionHistory.body();
                                preparePieChart();
                            } else if (fundTransactionHistory.code() == 400) {

                            } else {

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
    }
    // endregion


    //new onDateSet
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
        String date = "You picked the following date: From- " + dayOfMonth + "/" + (++monthOfYear) + "/" + year + " To " + dayOfMonthEnd + "/" + (++monthOfYearEnd) + "/" + yearEnd;
        String startDate = year + "-" + monthOfYear + "-" + dayOfMonth;
        String endDate = yearEnd + "-" + monthOfYearEnd + "-" + dayOfMonthEnd;
        getSalesDataForBarchart("DATE_RANGE", "", startDate, endDate);
        getBalanceTransactionData("DATE_RANGE", "", startDate, endDate);
        getSalesPerFaceValue("DATE_RANGE", "", startDate, endDate);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
