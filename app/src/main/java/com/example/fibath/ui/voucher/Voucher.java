package com.example.fibath.ui.voucher;

import static com.example.fibath.classes.api.APIInterface.APP_VERSION;
import static com.example.fibath.classes.api.APIInterface.BASE_URL;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidstudy.networkmanager.Tovuti;
import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;
import com.dantsu.escposprinter.exceptions.EscPosEncodingException;
import com.dantsu.escposprinter.exceptions.EscPosParserException;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;
import com.example.fibath.R;
import com.example.fibath.databinding.VoucherBinding;
import com.example.fibath.classes.api.APIInterface;
import com.example.fibath.classes.encryption.CryptLib;
import com.example.fibath.classes.model.AccountResponse;
import com.example.fibath.classes.model.ErrorResponse;
import com.example.fibath.classes.model.VoucherPurchaseResponse;
import com.example.fibath.classes.networking.communication.ServerCommunicator;
import com.example.fibath.classes.state.StateManager;
import com.example.fibath.classes.user.User;
import com.example.fibath.classes.voucher.VoucherBulkPrint;
import com.example.fibath.services.BluetoothBroadcastReceiver;
import com.example.fibath.ui.OfflineVoucher.database.OfflineDBManager;
import com.example.fibath.ui.OfflineVoucher.database.model.DownloadedVouchers;
import com.example.fibath.ui.Printer.bluetooth.BluetoothPrinter;
import com.example.fibath.ui.Printer.bluetooth.BluetoothPrintersConnections;
import com.example.fibath.ui.Printer.bluetooth.SelectedDevice;
import com.example.fibath.ui.agents.MyAgents;
import com.example.fibath.ui.drawer.DrawerAdapter;
import com.example.fibath.ui.drawer.DrawerItem;
import com.example.fibath.ui.drawer.SimpleItem;
import com.example.fibath.ui.drawer.SpaceItem;
import com.example.fibath.ui.helpers.UIHelpers;
import com.example.fibath.ui.profile.Profile;
import com.example.fibath.ui.setting.UserSetting;
import com.example.fibath.ui.statement.FundTransaction;
import com.example.fibath.ui.statement.FundTransactionHistory;
import com.example.fibath.ui.transaction.VoucherTransactionHistory;
import com.geniusforapp.fancydialog.FancyAlertDialog;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.NoSuchPaddingException;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class Voucher extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener {
    private VoucherBinding binding;

    // region Bottom Sheet Dialog
    BottomSheetDialog bottomSheetDialog;
    EditText amountSelector;
    TextView selectedAmount;
    TextView voucherBluetoothSelector;
    ImageView bluetoothPrinterDisconnector;
    ImageView voucherConnectBluetooth;
    TextView currentlySelectedCard;
    Button voucherPrint;
    Button buyAirtime;
    Button voucherDownload;
    // endregion

    // region Activity Status
    boolean singlePrintEnabled = false;
    boolean buyingAirtime = false;
    boolean isConnected = false;
    boolean isSmallPrinter = true;
    boolean isBluetooth = false;
    String selectedCardFaceValue = "";
    // endregion

    public List<com.example.fibath.classes.model.Voucher> bulkPinList = new ArrayList<>();
    private OfflineDBManager offlineDBManager;
    CryptLib cryptLib;
    AlertDialog alert;

    // region Drawer Items

    private static final int DRAWER_MENU_ITEM_DASHBOARD = 0;
    private static final int DRAWER_MENU_ITEM_TRANSACTIONS = 1;
    private static final int DRAWER_MENU_ITEM_MY_VOUCHERS_HISTORY = 2;
//    private static final int DRAWER_MENU_ITEM_Bank = 3;

    private static final int DRAWER_MENU_ITEM_MY_ACCOUNT = 4;
    private static final int DRAWER_MENU_ITEM_SETTINGS = 5;
    private static final int DRAWER_MENU_ITEM_LOGOUT = 6;

    private String[] screenTitles;
    private Drawable[] screenIcons;

    private SlidingRootNav drawer;
    // endregion

    // region On Create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = VoucherBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        try {
            initUI();
            initDrawer(savedInstanceState);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }
    // endregion

    // region Init UI
    private void initUI() throws NoSuchAlgorithmException, NoSuchPaddingException {
        UIHelpers.makeWindowTransparent(this);

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bootomsheet_print_new_voucher);

        // region Bottom sheet Dialog
        amountSelector = bottomSheetDialog.findViewById(R.id.voucher_amount_picker);
        selectedAmount = bottomSheetDialog.findViewById(R.id.print_btmsheet_selected_amount);
        voucherBluetoothSelector = bottomSheetDialog.findViewById(R.id.voucher_bluetooth_selector);
        bluetoothPrinterDisconnector = bottomSheetDialog.findViewById(R.id.voucher_bluetooth_printer_disconnect);
        currentlySelectedCard = bottomSheetDialog.findViewById(R.id.voucher_currently_selected_card);
        voucherPrint = bottomSheetDialog.findViewById(R.id.voucher_print);
        buyAirtime = bottomSheetDialog.findViewById(R.id.voucher_send_airtime);
        voucherDownload = bottomSheetDialog.findViewById(R.id.voucher_download);
        voucherConnectBluetooth = bottomSheetDialog.findViewById(R.id.voucherConnectBluetooth);
        // endregion

        // region Initialize Bluetooth State
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            voucherBluetoothSelector.setText("DOES NOT SUPPORT BLUETOOTH PRINTER");
//            binding.vouchersBluetoothButton.tint
            binding.vouchersBluetoothButton.setColorFilter(Color.parseColor("#D11616"));
            voucherBluetoothSelector.setBackgroundColor(Color.parseColor("#D11616"));
            voucherBluetoothSelector.setTextColor(Color.parseColor("#FFFFFF"));
            voucherBluetoothSelector.setEnabled(false);
            voucherPrint.setEnabled(false);
            voucherPrint.setBackgroundResource(R.drawable.btn_rounded_grayed);
        } else if (!mBluetoothAdapter.isEnabled()) {
            // Bluetooth is not enabled :)
            voucherBluetoothSelector.setText("TURN ON BLUETOOTH");
            binding.vouchersBluetoothButton.setColorFilter(Color.parseColor("#D11616"));
            binding.vouchersPrinterStatus.setText("BLUETOOTH OFF");
            binding.vouchersPrinterStatus.setTextColor(Color.RED);
            voucherBluetoothSelector.setBackgroundColor(Color.parseColor("#D11616"));
            voucherBluetoothSelector.setTextColor(Color.parseColor("#FFFFFF"));
            voucherBluetoothSelector.setEnabled(false);
            voucherPrint.setEnabled(false);
            voucherPrint.setBackgroundResource(R.drawable.btn_rounded_grayed);
        } else {
            // Bluetooth is enabled
        }
        // endregion

//        SharedPreferences sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
//        Toast.makeText(getApplicationContext(), "HomePageMode: " + sharedPreferences.getString("HomePageMode", ""), Toast.LENGTH_LONG).show();
//        if (sharedPreferences.getString("HomePageMode", "1").equals("1")) {
//            binding.vouchersBackButton.setImageResource(R.drawable.ic_home);
//            binding.vouchersSettingButton.setVisibility(View.VISIBLE);
            binding.vouchersTransactionsButton.setVisibility(View.VISIBLE);

//            binding.vouchersSettingButton.setOnClickListener(view -> {
//                Intent intent = new Intent(getApplicationContext(), UserSetting.class);
//                startActivity(intent);
//            });

            binding.vouchersTransactionsButton.setOnClickListener(view -> {
                Intent intent = new Intent(getApplicationContext(), VoucherTransactionHistory.class);
                startActivity(intent);
            });
//        }

        setupEventHandlers();

        setupBroadcastListeners();

        init();
    }

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // region Setup Event Handlers
    private void setupEventHandlers() {
        // region Vouchers On Click
        binding.vouchers5birrCard.setOnClickListener(view -> {
            selectedCardFaceValue = "5.00";
            if (!singlePrintEnabled) {
                amountSelector.setText("1");
                selectedAmount.setText("1 Voucher Selected");
                currentlySelectedCard.setText("5 Birr");
                bottomSheetDialog.show();
            } else {
                singlePrint(selectedCardFaceValue);
            }
        });

        binding.vouchers10birrCard.setOnClickListener(view -> {
            selectedCardFaceValue = "10.00";
            if (!singlePrintEnabled) {
                amountSelector.setText("1");
                selectedAmount.setText("1 Voucher Selected");
                currentlySelectedCard.setText("10 Birr");
                bottomSheetDialog.show();
            } else {
                singlePrint(selectedCardFaceValue);
            }
        });

        binding.vouchers15birrCard.setOnClickListener(view -> {
            selectedCardFaceValue = "15.00";
            if (!singlePrintEnabled) {
                amountSelector.setText("1");
                selectedAmount.setText("1 Voucher Selected");
                currentlySelectedCard.setText("15 Birr");
                bottomSheetDialog.show();
            } else {
                singlePrint(selectedCardFaceValue);
            }
        });

        binding.vouchers25birrCard.setOnClickListener(view -> {
            selectedCardFaceValue = "25.00";
            if (!singlePrintEnabled) {
                amountSelector.setText("1");
                selectedAmount.setText("1 Voucher Selected");
                currentlySelectedCard.setText("25 Birr");
                bottomSheetDialog.show();
            } else {
                singlePrint(selectedCardFaceValue);
            }
        });

        binding.vouchers50birrCard.setOnClickListener(view -> {
            selectedCardFaceValue = "50.00";
            if (!singlePrintEnabled) {
                amountSelector.setText("1");
                selectedAmount.setText("1 Voucher Selected");
                currentlySelectedCard.setText("50 Birr");
                bottomSheetDialog.show();
            } else {
                singlePrint(selectedCardFaceValue);
            }
        });

        binding.vouchers100birrCard.setOnClickListener(view -> {
            selectedCardFaceValue = "100.00";
            if (!singlePrintEnabled) {
                amountSelector.setText("1");
                selectedAmount.setText("1 Voucher Selected");
                currentlySelectedCard.setText("100 Birr");
                bottomSheetDialog.show();
            } else {
                singlePrint(selectedCardFaceValue);
            }
        });

        binding.vouchers250birrCard.setOnClickListener(view -> {
            selectedCardFaceValue = "250.00";
            if (!singlePrintEnabled) {
                amountSelector.setText("1");
                selectedAmount.setText("1 Voucher Selected");
                currentlySelectedCard.setText("250 Birr");
                bottomSheetDialog.show();
            } else {
                final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(this)
                        .setBackgroundColor(R.color.colorPrimary)
                        .setimageResource(R.drawable.ic_question_mark)
                        .setTextTitle("Confirmation")
                        .setTitleColor(R.color.colorLightWhite)
                        .setTextSubTitle("Do you really want to print 250 Birr Card?")
                        .setSubtitleColor(R.color.colorLightWhite)
                        .setBodyColor(R.color.red)
                        .setPositiveButtonText("Yes")
                        .setNegativeButtonText("No")
                        .setOnNegativeClicked((view12, dialog) -> dialog.dismiss())
                        .setPositiveColor(R.color.colorLightWhite)
                        .setOnPositiveClicked((view1, dialog) -> {
                            singlePrint(selectedCardFaceValue);
                        })
                        .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
                        .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                        .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
                        .build();
                alert.show();
            }
        });

        binding.vouchers500birrCard.setOnClickListener(view -> {
            selectedCardFaceValue = "500.00";
            if (!singlePrintEnabled) {
                amountSelector.setText("1");
                selectedAmount.setText("1 Voucher Selected");
                currentlySelectedCard.setText("500 Birr");
                bottomSheetDialog.show();
            } else {
                final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(this)
                        .setBackgroundColor(R.color.colorPrimary)
                        .setimageResource(R.drawable.ic_question_mark)
                        .setTextTitle("Confirmation")
                        .setTitleColor(R.color.colorLightWhite)
                        .setTextSubTitle("Do you really want to print 500 Birr Card?")
                        .setSubtitleColor(R.color.colorLightWhite)
                        .setBodyColor(R.color.red)
                        .setPositiveButtonText("Yes")
                        .setNegativeButtonText("No")
                        .setOnNegativeClicked((view12, dialog) -> dialog.dismiss())
                        .setPositiveColor(R.color.colorLightWhite)
                        .setOnPositiveClicked((view1, dialog) -> {
                            singlePrint(selectedCardFaceValue);
                        })
                        .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
                        .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                        .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
                        .build();
                alert.show();
            }
        });

        binding.vouchers1000birrCard.setOnClickListener(view -> {
            selectedCardFaceValue = "1000.00";
            if (!singlePrintEnabled) {
                amountSelector.setText("1");
                selectedAmount.setText("1 Voucher Selected");
                currentlySelectedCard.setText("1000 Birr");
                bottomSheetDialog.show();
            } else {
                final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(this)
                        .setBackgroundColor(R.color.colorPrimary)
                        .setimageResource(R.drawable.ic_question_mark)
                        .setTextTitle("Confirmation")
                        .setTitleColor(R.color.colorLightWhite)
                        .setTextSubTitle("Do you really want to print 1000 Birr Card?")
                        .setSubtitleColor(R.color.colorLightWhite)
                        .setBodyColor(R.color.red)
                        .setPositiveButtonText("Yes")
                        .setNegativeButtonText("No")
                        .setOnNegativeClicked((view12, dialog) -> dialog.dismiss())
                        .setPositiveColor(R.color.colorLightWhite)
                        .setOnPositiveClicked((view1, dialog) -> {
                            singlePrint(selectedCardFaceValue);
                        })
                        .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
                        .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                        .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
                        .build();
                alert.show();
            }
        });
        // endregion

        // region Bottom Sheet Buttons
        amountSelector.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
////                Toast.makeText(getApplicationContext(),"event called",Toast.LENGTH_SHORT).show();
//                if (amountSelector.length() > 0) {
//                    Toast.makeText(getApplicationContext(),"event called",Toast.LENGTH_SHORT).show();
//
//                    if (isNumeric(amountSelector.getText().toString())) {
//                        if (Integer.parseInt(amountSelector.getText().toString()) == 1) {
//                            buyAirtime.setEnabled(true);
//                            buyAirtime.setBackgroundResource(R.drawable.btn_rounded);
////                            if (SelectedDevice.isDeviceSelected()) {
//                                voucherPrint.setEnabled(true);
//                                voucherPrint.setBackgroundResource(R.drawable.btn_rounded);
////                            }
//                            voucherDownload.setEnabled(true);
//                            voucherDownload.setBackgroundResource(R.drawable.btn_rounded);
                selectedAmount.setText("voucher selected " + amountSelector.getText().toString());
//                        }
//
////
////                        else if (Integer.parseInt(amountSelector.getText().toString()) == 0) {
////                            buyAirtime.setEnabled(false);
////                            buyAirtime.setBackgroundResource(R.drawable.btn_rounded_grayed);
////                            voucherPrint.setEnabled(false);
////                            voucherPrint.setBackgroundResource(R.drawable.btn_rounded_grayed);
////                            voucherDownload.setEnabled(false);
////                            voucherDownload.setBackgroundResource(R.drawable.btn_rounded_grayed);
////                        }
////
////                        else if (Integer.parseInt(amountSelector.getText().toString()) > 300) {
////                            buyAirtime.setEnabled(false);
////                            buyAirtime.setBackgroundResource(R.drawable.btn_rounded_grayed);
////                            voucherPrint.setEnabled(false);
////                            voucherPrint.setBackgroundResource(R.drawable.btn_rounded_grayed);
////                        } else if (Integer.parseInt(amountSelector.getText().toString()) > 1000) {
////                            buyAirtime.setEnabled(false);
////                            buyAirtime.setBackgroundResource(R.drawable.btn_rounded_grayed);
////                            voucherPrint.setEnabled(false);
////                            voucherPrint.setBackgroundResource(R.drawable.btn_rounded_grayed);
////                            voucherDownload.setEnabled(false);
////                            voucherDownload.setBackgroundResource(R.drawable.btn_rounded_grayed);
////                        }
//
//                        else {
//                            buyAirtime.setEnabled(false);
//                            buyAirtime.setBackgroundResource(R.drawable.btn_rounded_grayed);
////                            if (SelectedDevice.isDeviceSelected()) {
//
//                                voucherPrint.setEnabled(true);
//                                voucherPrint.setBackgroundResource(R.drawable.btn_rounded_grayed);
////                            }
//                            int totalMoney = Integer.parseInt(amountSelector.getText().toString()) * Integer.parseInt(selectedCardFaceValue.split("\\.")[0]);
////                int totalMoney = 0;
//                            selectedAmount.setText(amountSelector.getText().toString() + " Vouchers Selected. " + " Total Price: " + totalMoney + " Br.");
//                        }
//                    } else {
//                        Toast.makeText(getApplicationContext(),"Amount is not number",Toast.LENGTH_SHORT).show();
////                        selectedAmount.setText("");
//                    }
//                } else {
//                    Toast.makeText(getApplicationContext(),"Amount is not number1",Toast.LENGTH_SHORT).show();
//
////                    voucherPrint.setEnabled(false);
////                    voucherPrint.setBackgroundResource(R.drawable.btn_rounded_grayed);
//                }
            }
        });

        voucherPrint.setOnClickListener(view -> {
            if (amountSelector.getText().toString().trim().length() == 0) {
                Toast.makeText(getApplicationContext(), "Amount should not  be Empty", Toast.LENGTH_SHORT).show();

            } else if (Integer.parseInt(amountSelector.getText().toString()) > 500 || Integer.parseInt(amountSelector.getText().toString()) == 0) {
                Toast.makeText(getApplicationContext(), "Amount should not Exceed 500 or be 0", Toast.LENGTH_SHORT).show();

            } else if (!SelectedDevice.isDeviceSelected() && isBluetooth) {
                Toast.makeText(getApplicationContext(), "Select Printer", Toast.LENGTH_SHORT).show();
            } else {
                if (amountSelector.length() > 0) {
                    if (isNumeric(amountSelector.getText().toString())) {
                        bulkPinList.clear();
                        Toast.makeText(getApplicationContext(), "Printing " + amountSelector.getText().toString() + " Vouchers", Toast.LENGTH_SHORT).show();
                        HashMap<String, Integer> bulkMap = new HashMap<>();
                        System.out.println("**************************");
                        System.out.println(Integer.parseInt(amountSelector.getText().toString()));
                        System.out.println(bulkMap.size());
                        System.out.println("Insideeeeeeeee");
                        bulkMap.put(selectedCardFaceValue, Integer.parseInt(amountSelector.getText().toString()));
                        if (Integer.parseInt(amountSelector.getText().toString()) > 501) {
                            final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(this)
                                    .setBackgroundColor(R.color.colorPrimary)
                                    .setimageResource(R.drawable.ic_question_mark)
                                    .setTextTitle("Confirmation")
                                    .setTitleColor(R.color.colorLightWhite)
                                    .setTextSubTitle("Do you really want to print " + amountSelector.getText().toString() + " Vouchers?")
                                    .setSubtitleColor(R.color.colorLightWhite)
                                    .setBodyColor(R.color.red)
                                    .setPositiveButtonText("Yes")
                                    .setNegativeButtonText("No")
                                    .setOnNegativeClicked((view12, dialog) -> dialog.dismiss())
                                    .setPositiveColor(R.color.colorLightWhite)
                                    .setOnPositiveClicked((view1, dialog) -> {
                                        if (isNetworkConnected()) {

                                            if (isBluetooth) {


                                                if (!SelectedDevice.isDeviceSelected()) {
                                                    SelectedDevice.setSelectedDevice(BluetoothPrintersConnections.selectFirstPaired());
                                                    SelectedDevice.setIsDeviceSelected(true);
                                                }
                                                if (SelectedDevice.getSelectedDevice() != null) {
                                                    voucherPrint.setEnabled(false);
                                                    voucherPrint.setBackgroundResource(R.drawable.btn_rounded_grayed);
                                                    try {
                                                        EscPosPrinter printer = new EscPosPrinter(SelectedDevice.getSelectedDevice(), 203, isSmallPrinter ? 48f : 72f, isSmallPrinter ? 32 : 48);
                                                        System.out.println("printer info is" + printer.getPrinterWidthPx());
                                                        if (SelectedDevice.getSelectedDevice().isConnected()) {
                                                            purchaseVoucher(bulkMap, getApplicationContext());
                                                        } else {
                                                            System.out.println("no printer found");

                                                            Toast.makeText(getApplicationContext(), "Please Check You Bluetooth Connection", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } catch (EscPosConnectionException e) {
                                                        e.printStackTrace();

                                                        new AlertDialog.Builder(Voucher.this, R.style.DialogTheme)
                                                                .setTitle("Broken connection")


                                                                .setMessage(e.getMessage())
                                                                .show();
                                                    }


                                                } else {
                                                    new AlertDialog.Builder(Voucher.this, R.style.DialogTheme)
                                                            .setTitle("Broken connection")
                                                            .setMessage("Please Connect Your Device")
                                                            .show();
                                                }
                                            } else {
//                                                purchaseVoucherSunmiPrinter(bulkMap, getApplicationContext());
                                            }

                                        } else {
//                                printOfflineVouchers(selectedCardFaceValue, Integer.parseInt(amountSelector.getText().toString()));
                                        }
                                    })
                                    .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
                                    .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                                    .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
                                    .build();
                            alert.show();
                        } else {
                            System.out.println("inside 1");
                            if (isNetworkConnected()) {
                                System.out.println("inside 2");

//                                if (isBluetooth) {
                                if (!SelectedDevice.isDeviceSelected()) {
                                    SelectedDevice.setSelectedDevice(BluetoothPrintersConnections.selectFirstPaired());
                                    SelectedDevice.setIsDeviceSelected(true);
                                }
                                if (SelectedDevice.getSelectedDevice() != null) {
                                    voucherPrint.setEnabled(false);
                                    voucherPrint.setBackgroundResource(R.drawable.btn_rounded_grayed);

                                    try {
                                        EscPosPrinter printer = new EscPosPrinter(SelectedDevice.getSelectedDevice(), 203, isSmallPrinter ? 48f : 72f, isSmallPrinter ? 32 : 48);
                                        System.out.println("printer info is" + printer.getPrinterWidthPx());
                                        if (SelectedDevice.getSelectedDevice().isConnected()) {
                                            purchaseVoucher(bulkMap, getApplicationContext());
                                        } else {
                                            System.out.println("no printer found");

                                            Toast.makeText(getApplicationContext(), "Please Check You Bluetooth Connection", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (EscPosConnectionException e) {
                                        e.printStackTrace();

                                        new AlertDialog.Builder(Voucher.this, R.style.DialogTheme)
                                                .setTitle("Broken connection")
                                                .setMessage(e.getMessage())
                                                .show();
                                    }


                                } else {
                                    new AlertDialog.Builder(Voucher.this, R.style.DialogTheme)
                                            .setTitle("Broken connection")


                                            .setMessage("Please Connect Your Device")
                                            .show();
                                }

//                                } else {
////                                    purchaseVoucherSunmiPrinter(bulkMap, getApplicationContext());
//                                }
                            } else {
//                    printOfflineVouchers(selectedCardFaceValue, Integer.parseInt(amountSelector.getText().toString()));
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Please enter a valid number.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter a valid number.", Toast.LENGTH_LONG).show();
                }
            }
        });

        buyAirtime.setOnClickListener(view -> {
            if (Integer.parseInt(amountSelector.getText().toString()) == 1) {
                if (ContextCompat.checkSelfPermission(Voucher.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Voucher.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                } else {
                    MaterialDialog airtimePhoneInput = new MaterialDialog.Builder(this)
                            .customView(R.layout.voucher_airtime_dialog, false)
                            .positiveText("Send Airtime")
                            .negativeText("Cancel")
                            .cancelable(false)
                            .autoDismiss(false)
                            .onPositive((dialog, which) -> {
                                EditText phone = (EditText) dialog.findViewById(R.id.voucher_airtime_phone);
                                HashMap<String, Integer> bulkMap = new HashMap<>();
                                bulkMap.put(selectedCardFaceValue, Integer.parseInt(amountSelector.getText().toString()));
                                if (!buyingAirtime) {
                                    purchaseAirtime(bulkMap, getApplicationContext(), phone.getText().toString());
                                } else {
                                    Toast.makeText(getApplicationContext(), "Already In Progress", Toast.LENGTH_LONG).show();
                                }
                            }).onNegative((dialog, which) -> {
                                dialog.dismiss();
                            })
                            .build();
                    airtimePhoneInput.show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Amount should  be 1 For Airtime", Toast.LENGTH_SHORT).show();

            }
        });


        voucherDownload.setOnClickListener(view -> {
            HashMap<String, Integer> bulkMap = new HashMap<>();
            bulkMap.put(selectedCardFaceValue, Integer.parseInt(amountSelector.getText().toString()));
            downloadVoucher(bulkMap);
        });

        voucherBluetoothSelector.setOnClickListener(view -> {
            if (SelectedDevice.isDeviceSelected()) {
                Toast.makeText(getApplicationContext(), "You have already selected printer", Toast.LENGTH_LONG).show();
            } else {
                browseBluetoothDevice();
            }
        });

        voucherConnectBluetooth.setOnClickListener(view -> {
            if (SelectedDevice.isDeviceSelected()) {
                Toast.makeText(getApplicationContext(), "You have already selected printer", Toast.LENGTH_LONG).show();
            } else {
                browseBluetoothDevice();
            }
        });

        bluetoothPrinterDisconnector.setOnClickListener(view -> {
            if(SelectedDevice.isDeviceSelected()) {
                SelectedDevice.getSelectedDevice().disconnect();
            }
            SelectedDevice.setSelectedDevice(null);
            SelectedDevice.setIsDeviceSelected(false);
            binding.vouchersPrinterStatus.setText("PRINTER DISCONNECTED");
            binding.vouchersPrinterStatus.setTextColor(Color.RED);
            voucherBluetoothSelector.setText("PRINTER DISCONNECTED");
            binding.vouchersBluetoothButton.setColorFilter(Color.parseColor("#FFFFFF"));
            voucherBluetoothSelector.setBackgroundColor(Color.parseColor("#ECECEC"));
            voucherBluetoothSelector.setTextColor(Color.parseColor("#214A6A"));
            voucherBluetoothSelector.setEnabled(true);
            bluetoothPrinterDisconnector.setVisibility(View.GONE);
            voucherPrint.setEnabled(false);
            voucherPrint.setBackgroundResource(R.drawable.btn_rounded_grayed);

        });
        // endregion

        binding.vouchersBottomNavigation.addBubbleListener(id -> {
            switch (id) {
                case R.id.home_bottom_navigation_cards:
                    Toast.makeText(getApplicationContext(), "You are already on the Cards Page", Toast.LENGTH_SHORT).show();
                    return;
                case R.id.home_bottom_navigation_agnets:
                    startActivity(new Intent(getApplicationContext(), MyAgents.class));
                    return;
                case R.id.home_bottom_navigation_transfers:
                    startActivity(new Intent(getApplicationContext(), FundTransaction.class));
                    return;
                case R.id.home_bottom_navigation_report:
                    Toast.makeText(getApplicationContext(), "Comming Soon!", Toast.LENGTH_SHORT).show();
                    return;
                default:
                    Toast.makeText(getApplicationContext(), "Comming Soon!", Toast.LENGTH_SHORT).show();
                    return;

            }
        });

        binding.vouchersSinglePrintSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            singlePrintEnabled = b;
        });

        binding.vouchersBackButton.setOnClickListener(v -> {
//            SharedPreferences sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
////            Toast.makeText(getApplicationContext(), "HomePageMode: " + sharedPreferences.getString("HomePageMode", ""), Toast.LENGTH_LONG).show();
//            if (sharedPreferences.getString("HomePageMode", "1").equals("1")) {
////                Intent intent = new Intent(getApplicationContext(), Home.class);
//                Intent intent = new Intent(getApplicationContext(), Voucher.class);
//                startActivity(intent);
//            } else {
//                onBackPressed();
//            }
            drawer.openMenu();
        });

        binding.vouchersBluetoothButton.setOnClickListener(view -> {
//            menuBottomSheetDialog.show();
//            startActivity(new Intent(getApplicationContext(), NewHome.class));
            // todo bluetooth connect
            if (SelectedDevice.isDeviceSelected()) {
                Toast.makeText(getApplicationContext(), "You have already selected printer", Toast.LENGTH_LONG).show();
            } else {
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {

                    final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(this)
                            .setBackgroundColor(R.color.colorPrimaryWarning)
                            .setimageResource(R.drawable.ic_dialog_warning)
                            .setTextTitle("Warning")
                            .setTitleColor(R.color.colorLightWhite)
                            .setTextSubTitle("Your device doesn't support bluetooth.")
                            .setSubtitleColor(R.color.colorLightWhite)
                            .setBodyColor(R.color.red)
                            .setPositiveButtonText("Okay")
                            .setPositiveColor(R.color.colorLightWhite)
                            .setOnPositiveClicked((view1, dialog) -> {
                                dialog.dismiss();
                            })
                            .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
                            .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                            .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
                            .build();
                    alert.show();



                    // Device does not support Bluetooth
                    voucherBluetoothSelector.setText("DOES NOT SUPPORT BLUETOOTH PRINTER");
//            binding.vouchersBluetoothButton.tint
                    binding.vouchersBluetoothButton.setColorFilter(Color.parseColor("#D11616"));
                    voucherBluetoothSelector.setBackgroundColor(Color.parseColor("#D11616"));
                    voucherBluetoothSelector.setTextColor(Color.parseColor("#FFFFFF"));
                    voucherBluetoothSelector.setEnabled(false);
                    voucherPrint.setEnabled(false);
                    voucherPrint.setBackgroundResource(R.drawable.btn_rounded_grayed);
                } else if (!mBluetoothAdapter.isEnabled()) {
//                    final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(this)
//                            .setBackgroundColor(R.color.colorPrimary)
//                            .setimageResource(R.drawable.ic_question_mark)
//                            .setTextTitle("Error")
//                            .setTitleColor(R.color.colorLightWhite)
//                            .setTextSubTitle("Please turn in bluetooth first.")
//                            .setSubtitleColor(R.color.colorLightWhite)
//                            .setBodyColor(R.color.red)
//                            .setPositiveButtonText("Yes")
//                            .setNegativeButtonText("No")
//                            .setOnNegativeClicked((view12, dialog) -> dialog.dismiss())
//                            .setPositiveColor(R.color.colorLightWhite)
//                            .setOnPositiveClicked((view1, dialog) -> {
//                                dialog.dismiss();
//                            })
//                            .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
//                            .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
//                            .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
//                            .build();
//                    alert.show();

                    final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(this)
                            .setBackgroundColor(R.color.colorPrimaryWarning)
                            .setimageResource(R.drawable.ic_dialog_warning)
                            .setTextTitle("Warning")
                            .setTitleColor(R.color.colorLightWhite)
                            .setTextSubTitle("Please turn in bluetooth first.")
                            .setSubtitleColor(R.color.colorLightWhite)
                            .setBodyColor(R.color.red)
                            .setPositiveButtonText("Okay")
                            .setPositiveColor(R.color.colorLightWhite)
                            .setOnPositiveClicked((view1, dialog) -> {
                                dialog.dismiss();
                            })
                            .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
                            .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                            .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
                            .build();
                    alert.show();



                    // Bluetooth is not enabled :)
                    voucherBluetoothSelector.setText("TURN ON BLUETOOTH");
                    binding.vouchersBluetoothButton.setColorFilter(Color.parseColor("#D11616"));
                    binding.vouchersPrinterStatus.setText("BLUETOOTH OFF");
                    binding.vouchersPrinterStatus.setTextColor(Color.RED);
                    voucherBluetoothSelector.setBackgroundColor(Color.parseColor("#D11616"));
                    voucherBluetoothSelector.setTextColor(Color.parseColor("#FFFFFF"));
                    voucherBluetoothSelector.setEnabled(false);
                    voucherPrint.setEnabled(false);
                    voucherPrint.setBackgroundResource(R.drawable.btn_rounded_grayed);
                } else {
                    browseBluetoothDevice();
                }
            }
        });

        Tovuti.from(this).monitor((connectionType, isConnected, isFast) -> {
            if (isConnected) {
                this.isConnected = true;
//                if (!OfflineVouchersManager.isSyncing()) {
//                    new OfflineVouchersManager(getApplicationContext()).sync();
//                } else {
////                    Toast.makeText(getApplicationContext(),"Sync Already In Progress  Vouchers",Toast.LENGTH_LONG).show();
//                }

                binding.vouchersOnlineStatus.setText("CONNECTED");
                binding.vouchersOnlineStatus.setTextColor(Color.GREEN);
                setCardDescription(true);
            } else {
                this.isConnected = false;
                binding.vouchersOnlineStatus.setText("OFFLINE");
                binding.vouchersOnlineStatus.setTextColor(Color.MAGENTA);
                setCardDescription(false);
            }
        });
    }
    // endregion

    // region Setup Broadcast Handlers
    private void setupBroadcastListeners() {
        IntentFilter filter3 = new IntentFilter();
        filter3.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter3.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(mBroadcastReceiver3, filter3);

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver1, filter);
    }
    // endregion

    // region Single Print
    private void singlePrint(String selectedCardFaceValue) {
        HashMap<String, Integer> bulkMap = new HashMap<>();
        bulkMap.put(selectedCardFaceValue, 1);

        if (isNetworkConnected()) {
            Intent intent = new Intent(getApplicationContext(), BluetoothPrinter.class);
            intent.putExtra("offline", "false");
            intent.putExtra("isReprint", "false");
            intent.putExtra("isBulk", "false");
            intent.putExtra("bulk", bulkMap);
            startActivity(intent);

        } else {
            printOfflineVouchers(selectedCardFaceValue, 1);
        }
    }
    // endregion

    // region Set Vouchers Titles
    private void setCardDescription(boolean setDefault) {
        if (setDefault) {
            binding.vouchers5birrCardDescription.setText("5 Birr");
            binding.vouchers10birrCardDescription.setText("10 Birr");
            binding.vouchers15birrCardDescription.setText("15 Birr");
            binding.vouchers25birrCardDescription.setText("25 Birr");
            binding.vouchers50birrCardDescription.setText("50 Birr");
            binding.vouchers100birrCardDescription.setText("100 Birr");
            binding.vouchers250birrCardDescription.setText("250 Birr");
            binding.vouchers500birrCardDescription.setText("500 Birr");
            binding.vouchers1000birrCardDescription.setText("1000 Birr");
        } else {
            binding.vouchers5birrCardDescription.setText("5 Birr (" + getOfflineAvailableQuantity("5.00") + ")");
            binding.vouchers10birrCardDescription.setText("10 Birr (" + getOfflineAvailableQuantity("10.00") + ")");
            binding.vouchers15birrCardDescription.setText("15 Birr (" + getOfflineAvailableQuantity("15.00") + ")");
            binding.vouchers25birrCardDescription.setText("25 Birr (" + getOfflineAvailableQuantity("25.00") + ")");
            binding.vouchers50birrCardDescription.setText("50 Birr (" + getOfflineAvailableQuantity("50.00") + ")");
            binding.vouchers100birrCardDescription.setText("100 Birr (" + getOfflineAvailableQuantity("100.00") + ")");
            binding.vouchers250birrCardDescription.setText("250 Birr (" + getOfflineAvailableQuantity("250.00") + ")");
            binding.vouchers500birrCardDescription.setText("500 Birr (" + getOfflineAvailableQuantity("500.00") + ")");
            binding.vouchers1000birrCardDescription.setText("1000 Birr (" + getOfflineAvailableQuantity("1000.00") + ")");
        }
    }
    // endregion

    // region Initialize Other Elements
    private void init() throws NoSuchPaddingException, NoSuchAlgorithmException {
        getUserBalance(getApplicationContext());
        VoucherBulkPrint.setBulkPrint(null);
        VoucherBulkPrint.setIsVouchersLoaded(false);
        offlineDBManager = new OfflineDBManager(this);
        cryptLib = new CryptLib();

        SharedPreferences sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        if (sharedPreferences.getString("BluetoothPrinterMode", "").equals("1")) {
            isBluetooth = true;
        }
        if (sharedPreferences.getString("LargePrinterMode", "").equals("1")) {
            isSmallPrinter = false;
        }

        if (SelectedDevice.isDeviceSelected()) {
            voucherBluetoothSelector.setText("Connected to printer " + SelectedDevice.getSelectedDevice().getDevice().getName());
            binding.vouchersPrinterStatus.setText("PRINTER CONNECTED: " + SelectedDevice.getSelectedDevice().getDevice().getName());
            binding.vouchersPrinterStatus.setTextColor(Color.GREEN);
            bluetoothPrinterDisconnector.setVisibility(View.VISIBLE);
            voucherBluetoothSelector.setBackgroundColor(Color.parseColor("#4CAF50"));
            binding.vouchersBluetoothButton.setColorFilter(Color.parseColor("#4CAF50"));
            voucherBluetoothSelector.setTextColor(Color.parseColor("#ffffff"));
            voucherPrint.setEnabled(true);
            voucherPrint.setBackgroundResource(R.drawable.btn_rounded);
        } else {
            Toast.makeText(getApplicationContext(), "No Device Connected", Toast.LENGTH_LONG).show();
            binding.vouchersPrinterStatus.setText("PRINTER DISCONNECTED");
            binding.vouchersBluetoothButton.setColorFilter(Color.parseColor("#D11616"));
            binding.vouchersPrinterStatus.setTextColor(Color.RED);
            voucherPrint.setEnabled(false);
            voucherPrint.setBackgroundResource(R.drawable.btn_rounded_grayed);
        }
    }
    // endregion

    // region Init Drawer
    private void initDrawer(Bundle savedInstanceState) {
        drawer = new SlidingRootNavBuilder(this)
//                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.drawer_side_menus)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        // Glide.with(this).load(R.drawable.bg_drawer_img).into(new SimpleTarget<Drawable>() {
        //     @Override
        //     public void onResourceReady(@NonNull Drawable resource, Transition<? super Drawable> transition) {
        //         findViewById(R.id.drawer_background_image).setBackground(resource);
        //     }
        // });

        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(DRAWER_MENU_ITEM_DASHBOARD).setChecked(true),
                createItemFor(DRAWER_MENU_ITEM_TRANSACTIONS),
                createItemFor(DRAWER_MENU_ITEM_MY_VOUCHERS_HISTORY),
//                createItemFor(DRAWER_MENU_ITEM_Bank),
                new SpaceItem(24),
                createItemFor(DRAWER_MENU_ITEM_MY_ACCOUNT),
                createItemFor(DRAWER_MENU_ITEM_SETTINGS),
                createItemFor(DRAWER_MENU_ITEM_LOGOUT)
        ));
        adapter.setListener(this);
        TextView nameText = findViewById(R.id.drawer_user_name);
        nameText.setText(User.getUserSessionUsername());
        RecyclerView list = findViewById(R.id.drawer_side_menus_container);
        list.setNestedScrollingEnabled(false);
        list.setHasFixedSize(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        adapter.setSelected(DRAWER_MENU_ITEM_DASHBOARD);
    }
    // endregion
    // endregion

    // region Get User Balance
    public void getUserBalance(final Context passedContext) {
        binding.vouchersBalanceTitle.setText("UPDATING BALANCE");
        String token = User.getUserSessionToken();

        APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
        myInterface.retailerAccountInformation(token, APP_VERSION)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<AccountResponse>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(@NonNull Response<AccountResponse> balanceEndPointResponseData) {
                System.out.println("****************************************************************");
                System.out.println("****************************************************************");

                System.out.println(balanceEndPointResponseData.body().getBalance());
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
                    binding.balanceVocuher.setText(String.format("%.0f", balance) + " Birr");
                    binding.vouchersBalanceTitle.setText("BALANCE UPDATED");
                } else {
                    Toast.makeText(passedContext, getResources().getString(R.string.someting_went_worong), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("has" + e);
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                binding.vouchersBalanceTitle.setText("CURRENT BALANCE");
            }
        });
    }
    // endregion

    // region Print offline Vouchers
    public void printOfflineVouchers(String amount, int quantity) {
        List<DownloadedVouchers> downloadedVouchers;

        downloadedVouchers = offlineDBManager.printVouchers(amount, quantity);
        if (downloadedVouchers.size() < quantity) {
            final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(this)
                    .setBackgroundColor(R.color.colorPrimaryWarning)
                    .setimageResource(R.drawable.ic_dialog_warning)
                    .setTextTitle("Insufficient Vouchers")
                    .setTitleColor(R.color.colorLightWhite)
                    .setTextSubTitle("There are only " + downloadedVouchers.size() + " vouchers available in your offline stock.")
                    .setSubtitleColor(R.color.colorLightWhite)
                    .setBodyColor(R.color.red)
                    .setPositiveButtonText("Okay")
                    .setPositiveColor(R.color.colorLightWhite)
                    .setOnPositiveClicked((view1, dialog) -> {
                        dialog.dismiss();
                    })
                    .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
                    .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                    .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
                    .build();
            alert.show();
        } else {
            offlineDBManager.updateVoucher(downloadedVouchers);
            VoucherBulkPrint.setOfflineBulkPrint(downloadedVouchers);
            Intent intent = new Intent();
            intent = new Intent(Voucher.this, BluetoothPrinter.class);
            intent.putExtra("offline", "true");
            intent.putExtra("isReprint", "false");
            intent.putExtra("isBulk", "false");
            startActivity(intent);

        }
    }
    //endregion

    // region Get Offline Available Vouchers Quantity
    private String getOfflineAvailableQuantity(String faceValue) {
        List<DownloadedVouchers> unprintedDownloadedVouchers = offlineDBManager.getAllUnPrintedVoucher();
        for (int i = 0; i < unprintedDownloadedVouchers.size(); i++) {
            if (faceValue.equals(unprintedDownloadedVouchers.get(i).getVoucherAmount())) {
                return unprintedDownloadedVouchers.get(i).getVoucherCount();
            }
        }
        return "0";
    }
    // endregion

    // region Download Vouchers For Offline Usage
    public void downloadVoucher(HashMap<String, Integer> voucher) {
        if (this.isConnected) {
            MaterialDialog downloadProgress = new MaterialDialog.Builder(this)
                    .customView(R.layout.voucher_download_dialog, false)
                    .cancelable(false)
                    .autoDismiss(false)
                    .build();
            downloadProgress.show();
            Context contextForCompleteDialog = this;

            APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
            myInterface.retailerBuyVouchers(User.getUserSessionToken(), APP_VERSION, voucher, "downloaded",null)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<ArrayList<VoucherPurchaseResponse>>>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(@NonNull Response<ArrayList<VoucherPurchaseResponse>> voucherPurchaseResponse) {
                    if (voucherPurchaseResponse.code() == 201) {
                        List<VoucherPurchaseResponse> lists = voucherPurchaseResponse.body();
                        User user = new User();
                        user.userBalance(getApplicationContext(), User.getUserSessionToken());

                        for (int i = 0; i < lists.size(); i++) {
                            bulkPinList.addAll(lists.get(i).getVoucher());
                        }

                        VoucherBulkPrint.setBulkPrint(bulkPinList);
                        VoucherBulkPrint.setIsVouchersLoaded(true);
                        for (int i = 0; i < VoucherBulkPrint.getBulkPrint().size(); i++) {
                            offlineDBManager.insertVouchers(VoucherBulkPrint.getBulkPrint().get(i).getId(), VoucherBulkPrint.getBulkPrint().get(i).getVoucherAmount(), VoucherBulkPrint.getBulkPrint().get(i).getVoucherNumber(), VoucherBulkPrint.getBulkPrint().get(i).getVoucherSerialNumber(), VoucherBulkPrint.getBulkPrint().get(i).getVoucherExpiryDate(), lists.get(0).getBulk_id(), 0);
                        }

                        MaterialDialog downloadProgress = new MaterialDialog.Builder(contextForCompleteDialog)
                                .customView(R.layout.voucher_download_complete_dialog, false)
                                .cancelable(true)
                                .autoDismiss(true)
                                .build();
                        downloadProgress.show();
                    } else {
                        switch (voucherPurchaseResponse.code()) {
                            case 400:
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
                    downloadProgress.dismiss();
                    new MaterialDialog.Builder(Voucher.this)
                            .customView(R.layout.voucher_download_error_dialog, false)
                            .cancelable(true)
                            .autoDismiss(true)
                            .build()
                            .show();
                }

                @Override
                public void onComplete() {
                    downloadProgress.dismiss();
                    getUserBalance(getApplicationContext());
                }
            });
        } else {
            new MaterialDialog.Builder(Voucher.this)
                    .customView(R.layout.connection_error_dialog, false)
                    .cancelable(true)
                    .autoDismiss(true)
                    .build()
                    .show();
        }
    }
    //endregion

    // region Drawer Implementations
    @Override
    public void onItemSelected(int position) {
        if (position == DRAWER_MENU_ITEM_MY_VOUCHERS_HISTORY) {
            getApplicationContext().startActivity(new Intent(getApplicationContext(), FundTransactionHistory.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else if (position == DRAWER_MENU_ITEM_TRANSACTIONS) {
            getApplicationContext().startActivity(new Intent(getApplicationContext(), VoucherTransactionHistory.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else if (position == DRAWER_MENU_ITEM_MY_ACCOUNT) {
            getApplicationContext().startActivity(new Intent(getApplicationContext(), Profile.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else if (position == DRAWER_MENU_ITEM_SETTINGS) {
            getApplicationContext().startActivity(new Intent(getApplicationContext(), UserSetting.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
//        else if (position == DRAWER_MENU_ITEM_Bank) {
//            getApplicationContext().startActivity(new Intent(getApplicationContext(), DownloadedVoucher.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//        }

        else if (position == DRAWER_MENU_ITEM_LOGOUT) {
            User user = new User();
            user.logout(getApplicationContext());
        }
        drawer.closeMenu();
    }
    // endregion

    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(color(R.color.textColorSecondary))
                .withTextTint(color(R.color.textColorPrimary))
                .withSelectedIconTint(color(R.color.colorAccent))
                .withSelectedTextTint(color(R.color.colorAccent));
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.ld_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }


    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }

    // region Purchase Voucher
    public void purchaseVoucher(HashMap<String, Integer> voucher, Context context) throws EscPosConnectionException {
        APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
        myInterface.retailerBuyVouchers(User.getUserSessionToken(), APP_VERSION, voucher, null,null)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<ArrayList<VoucherPurchaseResponse>>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Response<ArrayList<VoucherPurchaseResponse>> voucherPurchaseResponse) {
                if (voucherPurchaseResponse.code() == 201) {
                    List<VoucherPurchaseResponse> lists = voucherPurchaseResponse.body();
//                    System.out.println("Lists Size is " + lists.size());
                    User user = new User();
                    user.userBalance(getApplicationContext(), User.getUserSessionToken());

                    int pinCounter = 0;
                    for (int i = 0; i < lists.size(); i++) {
                        System.out.println("Lists size inner" + lists.size());
                        for (int x = 0; x < lists.get(i).getVoucher().size(); x++) {
                            bulkPinList.add(lists.get(i).getVoucher().get(x));
                            System.out.println("i is " + i + "x is " + x);
//                            VoucherBulkPrint.setBulkPrint(bulkPinList);
//                            VoucherBulkPrint.setIsVouchersLoaded(true);
                            System.out.println("++++++++++" + lists.get(i).getVoucher().get(x).getVoucherNumber());

                        }


                    }


                    System.out.println("Old size is" + bulkPinList.size());
//                    for (int i = 0; i < voucherList.size(); i++) {
//                        System.out.println("voucher batch number" + voucherList.get(i).getVoucherBatchNumber());
//                    }
                    Map<String, com.example.fibath.classes.model.Voucher> map = new LinkedHashMap<>();
                    for (com.example.fibath.classes.model.Voucher voucherrr : bulkPinList) {
                        map.put(voucherrr.getVoucherNumber(), voucherrr);
                    }
                    bulkPinList.clear();
                    bulkPinList.addAll(map.values());
//
                    System.out.println("new size is" + bulkPinList.size());
//                    for (int i = 0; i < voucherList.size(); i++) {
//                        System.out.println("voucher batch number" + voucherList.get(i).getVoucherBatchNumber());
//                    }

//                    bulkPinList.add()
                    for (int x = 0; x < bulkPinList.size(); x++) {
                        try {
                            printBill(bulkPinList.size() - x, bulkPinList.get(x).getVoucherAmount(), bulkPinList.get(x).getVoucherNumber(), bulkPinList.get(x).getVoucherSerialNumber(), bulkPinList.get(x).getVoucherExpiryDate(), 0, lists.get(0).getBulk_id());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    getUserBalance(getApplicationContext());
                } else {

                    switch (voucherPurchaseResponse.code()) {
                        case 400:
                            Gson gson = new GsonBuilder().create();
                            ErrorResponse mError = new ErrorResponse();
                            try {
                                mError = gson.fromJson(voucherPurchaseResponse.errorBody().string(), ErrorResponse.class);
                                Toast.makeText(context, mError.getErrors().get(0).getMsg(), Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                // handle failure to read error
                            }
                            break;
                        case 500:
                            Toast.makeText(context, "Server Error", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(context, "unknown error", Toast.LENGTH_SHORT).show();
                            break;
                    }

                }
            }

            @Override
            public void onError(Throwable e) {
                getUserBalance(getApplicationContext());
                System.out.println("has" + e);
                voucherPrint.setEnabled(true);
                voucherPrint.setBackgroundResource(R.drawable.btn_rounded);
            }

            @Override
            public void onComplete() {
                getUserBalance(getApplicationContext());
                voucherPrint.setEnabled(true);
                voucherPrint.setBackgroundResource(R.drawable.btn_rounded);
            }
        });
    }
    // endregion

    // region Print Bill
    protected void printBill(int pageNumber, String amount, String pinNo, String serial, String expiryDate, int reDownload, String bulkId) throws Exception {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, BluetoothPrinter.PERMISSION_BLUETOOTH);
            System.out.println("outside print");

        } else {
            try {
                String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

                pinNo = cryptLib.decryptCipherTextWithRandomIV(pinNo, "123456789");
                String originalPin = pinNo;

                String pinOne = pinNo.substring(0, 5);
                String pinTwo = pinNo.substring(5, 8);
                String pinThree = pinNo.substring(8);
                pinNo = pinOne + "-" + pinTwo + "-" + pinThree;
                System.out.println("inside print");
//                selectedDevice = BluetoothPrintersConnections.selectFirstPaired();

                EscPosPrinter printer = new EscPosPrinter(SelectedDevice.getSelectedDevice(), 203, isSmallPrinter ? 48f : 72f, isSmallPrinter ? 32 : 48);
                System.out.println("selected redownload is" + reDownload);
                printer.printFormattedText(
                        "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, this.getApplicationContext().getResources().getDrawableForDensity(R.drawable.horizon_ethiotelecom_logo, DisplayMetrics.DENSITY_MEDIUM)) + "</img>\n" +
                                "[C]<font size='wide'>" + amount.replace(".00", "") + " Birr</font>\n" +
                                "[C]--------------------------------\n" +
                                "[C]<font size='big'>" + pinNo + "</font>\n" +
                                "[C]--------------------------------\n" +

                                "[C]To Recharge *705*" + originalPin + "#\n" +
                                "[C] SN: " + serial + "\n" +
                                "[C]<b>Agent: " + User.getUserSessionUsername() + "</b>\n" +
                                "[C]  Powered By HorizonTech\n" +
                                "[C]PD:" + date + " ED:" + expiryDate + "\n" +
//                                (pageNumber != 0 ? "[C]" + bulkId + "-(" + pageNumber + ")" : "")
                                (pageNumber != 0 ? "[C]" + pageNumber +  "":"")

//                                +"\n"+
//                                "[C]Make Sure That The Card Is Recharged\n" +
//                                "[C]Before Leaving the Retailer"

                        , 5f);
            } catch (EscPosConnectionException e) {
                SelectedDevice.setIsDeviceSelected(false);
                e.printStackTrace();
                new AlertDialog.Builder(this, R.style.DialogTheme)
                        .setTitle("Broken connection")
                        .setMessage(e.getMessage())
                        .show();
            } catch (EscPosParserException e) {
                SelectedDevice.setIsDeviceSelected(false);
                e.printStackTrace();
                new AlertDialog.Builder(this)
                        .setTitle("Invalid formatted text")
                        .setMessage(e.getMessage())
                        .show();
            } catch (EscPosEncodingException e) {
                SelectedDevice.setIsDeviceSelected(false);
                e.printStackTrace();
                new AlertDialog.Builder(this)
                        .setTitle("Bad selected encoding")
                        .setMessage(e.getMessage())
                        .show();
            } catch (EscPosBarcodeException e) {
                SelectedDevice.setIsDeviceSelected(false);
                e.printStackTrace();
                new AlertDialog.Builder(this)
                        .setTitle("Invalid barcode")
                        .setMessage(e.getMessage())
                        .show();
            }
        }
    }
    // endregion

    // region Purchase Airtime
    public void purchaseAirtime(HashMap<String, Integer> voucher, Context context, String phone) {
        buyingAirtime = true;
        APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
        myInterface.retailerBuyVouchers(User.getUserSessionToken(), APP_VERSION, voucher, null,"true")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<ArrayList<VoucherPurchaseResponse>>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(@NonNull Response<ArrayList<VoucherPurchaseResponse>> voucherPurchaseResponse) {
                if (voucherPurchaseResponse.code() == 201) {
                    List<VoucherPurchaseResponse> lists = voucherPurchaseResponse.body();
                    User user = new User();
                    user.userBalance(getApplicationContext(), User.getUserSessionToken());
                    String pinNo = "";
                    try {
                        pinNo = cryptLib.decryptCipherTextWithRandomIV(lists.get(0).getVoucher().get(0).getVoucherNumber(), "123456789");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    String ussdCode = "*705*" + pinNo + "*" + phone + Uri.encode("#");
                    startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + ussdCode)));
                } else {
                    Toast.makeText(getApplicationContext(), "No Vouchers", Toast.LENGTH_SHORT).show();
                    switch (voucherPurchaseResponse.code()) {
                        case 400:
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
                buyingAirtime = false;
                getUserBalance(getApplicationContext());
            }
        });
    }
    // endregion

    // region Browse Printers
    public void browseBluetoothDevice() {
        final BluetoothConnection[] bluetoothDevicesList = (new BluetoothPrintersConnections()).getList();

        if (bluetoothDevicesList != null) {
            final String[] items = new String[bluetoothDevicesList.length + 1];
            items[0] = "Default printer";
            int i = 0;
            for (BluetoothConnection device : bluetoothDevicesList) {
                items[++i] = device.getDevice().getName();
            }

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(Voucher.this, R.style.DialogTheme);
            alertDialog.setTitle("Select Bluetooth Device");

            alertDialog.setItems(items, (dialogInterface, i1) -> {
                int index = i1 - 1;
                if (index == -1) {
                    SelectedDevice.setSelectedDevice(BluetoothPrintersConnections.selectFirstPaired());
                    SelectedDevice.setIsDeviceSelected(true);
                } else {
                    SelectedDevice.setSelectedDevice(bluetoothDevicesList[index]);
                    SelectedDevice.setIsDeviceSelected(true);
                }
                connectToPrinter(items, i1);
            });

            alert = alertDialog.create();
            alert.setCanceledOnTouchOutside(false);
            alert.show();
        }
    }
    // endregion

    // region Connect to printer
    private void connectToPrinter(String[] items, int i1) {
        try {
            SelectedDevice.getSelectedDevice().connect();
            voucherBluetoothSelector.setText("Connected to printer " + items[i1]);
            binding.vouchersPrinterStatus.setText("PRINTER CONNECTED: " + items[i1]);
            binding.vouchersPrinterStatus.setTextColor(Color.GREEN);
            voucherBluetoothSelector.setBackgroundColor(Color.parseColor("#4CAF50"));
            binding.vouchersBluetoothButton.setColorFilter(Color.parseColor("#4CAF50"));
            voucherBluetoothSelector.setTextColor(Color.parseColor("#ffffff"));
            bluetoothPrinterDisconnector.setVisibility(View.VISIBLE);
            voucherPrint.setEnabled(true);
            voucherPrint.setBackgroundResource(R.drawable.btn_rounded);
            startService(new Intent(getApplicationContext(), BluetoothBroadcastReceiver.class));
        } catch (EscPosConnectionException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Unable to connect to printer.", Toast.LENGTH_LONG).show();
            SelectedDevice.setIsDeviceSelected(false);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Unable to connect to printer.", Toast.LENGTH_LONG).show();
            SelectedDevice.setSelectedDevice(null);
            SelectedDevice.setIsDeviceSelected(false);
        }
    }
    // endregion

    @Override
    protected void onStop() {
        Tovuti.from(this).stop();
        super.onStop();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private final BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch (action) {
                case BluetoothDevice.ACTION_ACL_CONNECTED:

                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    binding.vouchersPrinterStatus.setText("PRINTER DISCONNECTED");
                    binding.vouchersPrinterStatus.setTextColor(Color.RED);
                    voucherBluetoothSelector.setText("PRINTER DISCONNECTED");
                    voucherBluetoothSelector.setBackgroundColor(Color.parseColor("#ECECEC"));
                    binding.vouchersBluetoothButton.setColorFilter(Color.parseColor("#D11616"));
                    voucherBluetoothSelector.setTextColor(Color.parseColor("#214A6A"));
                    voucherBluetoothSelector.setEnabled(true);
                    bluetoothPrinterDisconnector.setVisibility(View.GONE);
                    voucherPrint.setEnabled(false);
                    voucherPrint.setBackgroundResource(R.drawable.btn_rounded_grayed);

                    break;
            }
        }
    };

    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        binding.vouchersPrinterStatus.setText("BLUETOOTH OFF");
                        binding.vouchersPrinterStatus.setTextColor(Color.RED);
                        voucherBluetoothSelector.setText("TURN ON BLUETOOTH");
                        voucherBluetoothSelector.setBackgroundColor(Color.parseColor("#D11616"));
                        binding.vouchersBluetoothButton.setColorFilter(Color.parseColor("#D11616"));
                        voucherBluetoothSelector.setTextColor(Color.parseColor("#FFFFFF"));
                        voucherBluetoothSelector.setEnabled(false);
                        bluetoothPrinterDisconnector.setVisibility(View.GONE);
                        voucherPrint.setEnabled(false);
                        voucherPrint.setBackgroundResource(R.drawable.btn_rounded_grayed);
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:

                        break;
                    case BluetoothAdapter.STATE_ON:
                        binding.vouchersPrinterStatus.setText("PRINTER DISCONNECTED");
                        binding.vouchersPrinterStatus.setTextColor(Color.RED);
                        voucherBluetoothSelector.setText("PRINTER DISCONNECTED");
                        voucherBluetoothSelector.setBackgroundColor(Color.parseColor("#ECECEC"));
                        binding.vouchersBluetoothButton.setColorFilter(Color.parseColor("#D11616"));
                        voucherBluetoothSelector.setTextColor(Color.parseColor("#214A6A"));
                        voucherBluetoothSelector.setEnabled(true);
                        bluetoothPrinterDisconnector.setVisibility(View.GONE);
                        voucherPrint.setEnabled(false);
                        voucherPrint.setBackgroundResource(R.drawable.btn_rounded_grayed);
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:

                        break;
                }

            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getUserBalance(getApplicationContext());
        binding.vouchersBottomNavigation.setSelected(0, false);
    }

}