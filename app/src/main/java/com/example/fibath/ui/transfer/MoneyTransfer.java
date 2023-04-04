package com.example.fibath.ui.transfer;

import static com.example.fibath.classes.api.APIInterface.APP_VERSION;
import static com.example.fibath.classes.api.APIInterface.BASE_URL;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.fibath.R;
import com.example.fibath.databinding.MoneyTransferBinding;
import com.example.fibath.classes.api.APIInterface;
import com.example.fibath.classes.model.AccountResponse;
import com.example.fibath.classes.model.ErrorResponse;
import com.example.fibath.classes.model.MoneyTransferResponse;
import com.example.fibath.classes.networking.communication.ServerCommunicator;
import com.example.fibath.classes.state.StateManager;
import com.example.fibath.classes.user.User;
import com.example.fibath.ui.statement.FundTransactionHistory;
import com.geniusforapp.fancydialog.FancyAlertDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class MoneyTransfer extends AppCompatActivity {
    private MoneyTransferBinding binding;
    private AutoSuggestAdapter autoSuggestAdapter;
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Handler handler;
    private String selectedUser = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MoneyTransferBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
//        initUI();
    }

    // region Init UI
    private void initUI() {

        initEventHandlers();
    }
    // endregion

    // region Init Event Handlers
    private void initEventHandlers() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getUserBalance(getApplicationContext());

        binding.vouchersBackButton.setOnClickListener(v -> {
            onBackPressed();
        });
        autoSuggestAdapter = new AutoSuggestAdapter(this,
                R.layout.custom_autocomplete_view);
        binding.vouchersMenuUser.setThreshold(1);
        binding.vouchersMenuUser.setAdapter(autoSuggestAdapter);
//        binding.vouchersMenuUser.setTextCo;
        binding.vouchersMenuUser.setOnItemClickListener(
                (parent, view, position, id) -> {
                    System.out.println("selected item is" + selectedUser);
//                        selectedText.setText(autoSuggestAdapter.getObject(position));
                }
        );
        binding.vouchersMenuUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
                binding.balanceVocuher2.setText(binding.vouchersMenuUser.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        handler = new Handler(msg -> {
            if (msg.what == TRIGGER_AUTO_COMPLETE) {
                if (!TextUtils.isEmpty(binding.vouchersMenuUser.getText())) {
                    searchUser(binding.vouchersMenuUser.getText().toString());
                }
            }
            return false;
        });

        binding.vouchersMenuTransfer.setOnClickListener(v -> {
            if (binding.vouchersMenuUser.getText().toString().equals("") || binding.vouchersMenuAmount.getText().toString().equals("") || selectedUser.equals("")) {
                FancyToast.makeText(getApplicationContext(), "Please Fill The Input ", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();

            } else {
                final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(this)
                        .setBackgroundColor(R.color.colorPrimaryWarning)
                        .setimageResource(R.drawable.ic_dialog_warning)
                        .setTextTitle("Transfer Warning")
                        .setTitleColor(R.color.colorLightWhite)
                        .setTextSubTitle("Are you sure you want to transfer " + binding.vouchersMenuAmount.getText().toString() + " Birr to " + binding.vouchersMenuUser.getText().toString())
                        .setSubtitleColor(R.color.colorLightWhite)
                        .setBodyColor(R.color.red)
                        .setPositiveButtonText("Yes")
                        .setPositiveColor(R.color.colorLightWhite)
                        .setOnPositiveClicked((view1, dialog) -> {
                            HashMap<String, String> bulkMap = new HashMap<>();

                            bulkMap.put("amount", binding.vouchersMenuAmount.getText().toString());
                            bulkMap.put("receiver_id", selectedUser);
                            bulkMap.put("parent_remark", "Direct Agent To Agent Transfer");

                            transferMoney(bulkMap);
                            dialog.dismiss();
                        })
                        .setNegativeButtonText("No")
                        .setNegativeColor(R.color.colorLightWhite)
                        .setOnNegativeClicked((view1, dialog) -> {
                            dialog.dismiss();
                        })
                        .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
                        .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                        .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
                        .build();
                alert.show();
            }
        });

        binding.vouchersMenuAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isNumeric(binding.vouchersMenuAmount.getText().toString())) {
                    if (Double.parseDouble(binding.vouchersMenuAmount.getText().toString()) > 5000) {
                        binding.vouchersMenuTotalAmountToTransfer.setTextColor(Color.RED);
                        binding.vouchersMenuTotalAmountToTransfer.setText(binding.vouchersMenuAmount.getText().toString() + " Br.");
                    } else {
                        binding.vouchersMenuTotalAmountToTransfer.setTextColor(Color.WHITE);
                        binding.vouchersMenuTotalAmountToTransfer.setText(binding.vouchersMenuAmount.getText().toString() + " Br.");
                    }
                } else {
                    binding.vouchersMenuTotalAmountToTransfer.setText("-");
                }
            }
        });

        binding.transferActivityTransferHistory.setOnClickListener(v -> {
            startActivity(new Intent(MoneyTransfer.this, FundTransactionHistory.class));
        });
    }
    // endregion

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    // region Search User
    public void searchUser(String searchText) {
        APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
        myInterface.searchUsers(User.getUserSessionToken(), APP_VERSION, searchText)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<ArrayList<com.example.fibath.classes.model.User>>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(@NonNull Response<ArrayList<com.example.fibath.classes.model.User>> voucherPurchaseResponse) {
                if (voucherPurchaseResponse.code() == 200) {
                    ArrayList<String> stringList = new ArrayList<>();

                    List<com.example.fibath.classes.model.User> lists = voucherPurchaseResponse.body();
                    for (int i = 0; i < lists.size(); i++) {
                        stringList.add(lists.get(i).getName() + ", " + lists.get(i).getPhone());
                        selectedUser = lists.get(i).getId();
                        System.out.println("result is" + lists.get(i).getName());
                    }
                    autoSuggestAdapter.setData(stringList);
                    autoSuggestAdapter.notifyDataSetChanged();
                } else {
                    switch (voucherPurchaseResponse.code()) {
                        case 400:
                            Gson gson = new GsonBuilder().create();
                            ErrorResponse mError;
                            try {
                                mError = gson.fromJson(voucherPurchaseResponse.errorBody().string(), ErrorResponse.class);
//                                FancyToast.makeText(getApplicationContext(), mError.getErrors().get(0).getMsg(), FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                            } catch (IOException e) {
                                // handle failure to read error
                            }
                            break;
                        case 500:
                            FancyToast.makeText(getApplicationContext(), "Server Error! Please contact the system administrators.", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                            break;
                        default:
                            FancyToast.makeText(getApplicationContext(), "Unknown Error! Something went wrong.", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                            break;
                    }
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
//
    }


    //endregion

    //region Transfer Money
    public void transferMoney(HashMap<String, String> transferRequest) {

        MaterialDialog downloadProgress = new MaterialDialog.Builder(this)
                .customView(R.layout.money_transfer_dialog, false)
                .cancelable(false)
                .autoDismiss(false)
                .build();
        downloadProgress.show();
        Context contextForCompleteDialog = this;

        APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
        myInterface.transferMoney(User.getUserSessionToken(), APP_VERSION, transferRequest)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<MoneyTransferResponse>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(@NonNull Response<MoneyTransferResponse> voucherPurchaseResponse) {
                if (voucherPurchaseResponse.code() == 201) {

                    MoneyTransferResponse lists = voucherPurchaseResponse.body();
                    MaterialDialog downloadProgress = new MaterialDialog.Builder(contextForCompleteDialog)
                            .customView(R.layout.money_transfer_complete_dialog, false)

                            .cancelable(true)
                            .autoDismiss(true)
                            .build();
                    downloadProgress.show();
                    getUserBalance(getApplicationContext());
                    binding.vouchersMenuUser.setText("");
                    binding.vouchersMenuAmount.setText("");

                } else {
                    switch (voucherPurchaseResponse.code()) {
                        case 400:
                            downloadProgress.dismiss();
                            Gson gson = new GsonBuilder().create();
                            ErrorResponse mError;
                            try {
                                mError = gson.fromJson(voucherPurchaseResponse.errorBody().string(), ErrorResponse.class);
                                FancyToast.makeText(getApplicationContext(), mError.getErrors().get(0).getMsg(), FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                            } catch (IOException e) {
                                // handle failure to read error
                            }
                            break;
                        case 500:
                            downloadProgress.dismiss();
                            FancyToast.makeText(getApplicationContext(), "Server Error! Please contact the system administrators.", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                            break;
                        default:
                            downloadProgress.dismiss();
                            FancyToast.makeText(getApplicationContext(), "Unknown Error! Something went wrong.", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                            break;
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("has" + e);
                downloadProgress.dismiss();
            }

            @Override
            public void onComplete() {
                downloadProgress.dismiss();
            }
        });
//
    }


    //endregion

    // region Get User Balance
    public void getUserBalance(final Context passedContext) {
        binding.balanceVocuher3.setText("LOADING BALANCE");
        String token = User.getUserSessionToken();
        System.out.println("######balance is");

        APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
        myInterface.retailerAccountInformation(token, APP_VERSION)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<AccountResponse>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(@NonNull Response<AccountResponse> balanceEndPointResponseData) {

                if (balanceEndPointResponseData.code() == 200) {

                    StateManager.setBalance(String.valueOf(balanceEndPointResponseData.body().getBalance()));
                    DecimalFormat df = new DecimalFormat();
                    df.setMaximumFractionDigits(2);
                    Double balance = 0d;
                    try {
                        balance = Double.parseDouble(String.valueOf(balanceEndPointResponseData.body().getBalance()));
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Unable to get balance.", Toast.LENGTH_SHORT).show();
                    }
                    binding.balanceVocuher3.setText(String.format("%.0f", balance) + " Birr");
                } else {
                    Toast.makeText(passedContext, getResources().getString(R.string.someting_went_worong), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {

                System.out.println("######balance is on Error");

                System.out.println("has" + e);
            }

            @Override
            public void onComplete() {
                System.out.println("######balance is +ON Completed");

            }
        });
    }
    // endregion

}
