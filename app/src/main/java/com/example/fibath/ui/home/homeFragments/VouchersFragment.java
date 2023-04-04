package com.example.fibath.ui.home.homeFragments;

import static com.example.fibath.classes.api.APIInterface.APP_VERSION;
import static com.example.fibath.classes.api.APIInterface.BASE_URL;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
import com.example.fibath.databinding.VoucherFragmentBinding;
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
import com.example.fibath.ui.Printer.sunmi.v2.SunmiV2Printer;
import com.example.fibath.ui.helpers.UIHelpers;
import com.example.fibath.ui.home.NewHome;
import com.example.fibath.ui.transaction.VoucherTransactionHistory;
import com.geniusforapp.fancydialog.FancyAlertDialog;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.crypto.NoSuchPaddingException;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class VouchersFragment extends Fragment {
    private VoucherFragmentBinding binding;
    MyReceiver myReceiver;

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
    boolean isBluetooth = true;
    String selectedCardFaceValue = "";
    // endregion

    public List<com.example.fibath.classes.model.Voucher> bulkPinList = new ArrayList<>();
    private OfflineDBManager offlineDBManager;
    CryptLib cryptLib;
    AlertDialog alert;
    View view;

    public VouchersFragment() {
        // Required empty public constructor
    }

    // region On Create
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = VoucherFragmentBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        if (isAdded()) {
            try {
                initUI();
            } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
                e.printStackTrace();
            }
        } else {

        }
        return view;
    }


    // endregion

    // region Init UI
    private void initUI() throws NoSuchAlgorithmException, NoSuchPaddingException {
        UIHelpers.makeWindowTransparent(requireActivity());
        bottomSheetDialog = new BottomSheetDialog(requireActivity());
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
            voucherBluetoothSelector.setText(requireContext().getResources().getString(R.string.bluetooth_prinet_not_supported));
            // binding.vouchersBluetoothButton.tint
            binding.vouchersBluetoothButton.setColorFilter(Color.parseColor("#D11616"));
            voucherBluetoothSelector.setBackgroundColor(Color.parseColor("#D11616"));
            voucherBluetoothSelector.setTextColor(Color.parseColor("#FFFFFF"));
            voucherBluetoothSelector.setEnabled(false);
            voucherPrint.setEnabled(false);
            voucherPrint.setBackgroundResource(R.drawable.btn_rounded_grayed);
        } else if (!mBluetoothAdapter.isEnabled()) {
            // Bluetooth is not enabled :)
            voucherBluetoothSelector.setText(requireContext().getResources().getString(R.string.turn_on_bluetooth));
            binding.vouchersBluetoothButton.setColorFilter(Color.parseColor("#D11616"));
            binding.vouchersPrinterStatus.setText(requireContext().getResources().getString(R.string.bluetooth_off));
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

        binding.vouchersTransactionsButton.setVisibility(View.VISIBLE);

        binding.vouchersTransactionsButton.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), VoucherTransactionHistory.class);
            startActivity(intent);
        });

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

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(context, "Vouchers Page Updating...", Toast.LENGTH_SHORT).show();
            getUserBalance(context);
        }
    }

    // region Setup Event Handlers
    private void setupEventHandlers() {
        // region Vouchers On Click
        binding.vouchers5birrCard.setOnClickListener(view -> {
            selectedCardFaceValue = "5.00";
            if (!singlePrintEnabled) {
                amountSelector.setText("1");
                selectedAmount.setText("1 " + requireContext().getResources().getString(R.string.voucher_selected));
                currentlySelectedCard.setText(requireContext().getResources().getString(R.string.five_birr_voucher));
                bottomSheetDialog.show();
            } else {
                singlePrint(selectedCardFaceValue);
            }
        });

        binding.vouchers10birrCard.setOnClickListener(view -> {
            selectedCardFaceValue = "10.00";
            if (!singlePrintEnabled) {
                amountSelector.setText("1");
                selectedAmount.setText("1 " + requireContext().getResources().getString(R.string.voucher_selected));
                currentlySelectedCard.setText(requireContext().getResources().getString(R.string.ten_birr_voucher));
                bottomSheetDialog.show();
            } else {
                singlePrint(selectedCardFaceValue);
            }
        });

        binding.vouchers15birrCard.setOnClickListener(view -> {
            selectedCardFaceValue = "15.00";
            if (!singlePrintEnabled) {
                amountSelector.setText("1");
                selectedAmount.setText("1 " + requireContext().getResources().getString(R.string.voucher_selected));
                currentlySelectedCard.setText(requireContext().getResources().getString(R.string.fifteen_birr_voucher));
                bottomSheetDialog.show();
            } else {
                singlePrint(selectedCardFaceValue);
            }
        });

        binding.vouchers25birrCard.setOnClickListener(view -> {
            selectedCardFaceValue = "25.00";
            if (!singlePrintEnabled) {
                amountSelector.setText("1");
                selectedAmount.setText("1 " + requireContext().getResources().getString(R.string.voucher_selected));
                currentlySelectedCard.setText(requireContext().getResources().getString(R.string.twentyfive_birr_voucher));
                bottomSheetDialog.show();
            } else {
                singlePrint(selectedCardFaceValue);
            }
        });

        binding.vouchers50birrCard.setOnClickListener(view -> {
            selectedCardFaceValue = "50.00";
            if (!singlePrintEnabled) {
                amountSelector.setText("1");
                selectedAmount.setText("1 " + requireContext().getResources().getString(R.string.voucher_selected));
                currentlySelectedCard.setText(requireContext().getResources().getString(R.string.fifty_birr_voucher));
                bottomSheetDialog.show();
            } else {
                singlePrint(selectedCardFaceValue);
            }
        });

        binding.vouchers100birrCard.setOnClickListener(view -> {
            selectedCardFaceValue = "100.00";
            if (!singlePrintEnabled) {
                amountSelector.setText("1");
                selectedAmount.setText("1 " + requireContext().getResources().getString(R.string.voucher_selected));
                currentlySelectedCard.setText(requireContext().getResources().getString(R.string.hunderd_birr_voucher));
                bottomSheetDialog.show();
            } else {
                singlePrint(selectedCardFaceValue);
            }
        });

        binding.vouchers250birrCard.setOnClickListener(view -> {
            selectedCardFaceValue = "200.00";
            boolean isTwoHundredFiftyBirrVoucher = binding.vouchers250birrCardDescription.getText().toString().startsWith("250");
            if (isTwoHundredFiftyBirrVoucher) {
                selectedCardFaceValue = "250.00";
            }
            if (!singlePrintEnabled) {
                amountSelector.setText("1");
                selectedAmount.setText("1 " + requireContext().getResources().getString(R.string.voucher_selected));
                currentlySelectedCard.setText("200 Birr");
                if (isTwoHundredFiftyBirrVoucher) {
                    currentlySelectedCard.setText(requireContext().getResources().getString(R.string.twohundredfifty_birr_voucher));
                } else {
                    currentlySelectedCard.setText("200");
                }
                bottomSheetDialog.show();
            } else {
                final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(requireActivity())
                        .setBackgroundColor(R.color.colorPrimary)
                        .setimageResource(R.drawable.ic_question_mark)
                        .setTextTitle(requireContext().getResources().getString(R.string.dialog_confirmation))
                        .setTitleColor(R.color.colorLightWhite)
                        .setTextSubTitle("Do you really want to print 200 Birr Card?")
                        .setSubtitleColor(R.color.colorLightWhite)
                        .setBodyColor(R.color.red)
                        .setPositiveButtonText(requireContext().getResources().getString(R.string.dialog_yes))
                        .setNegativeButtonText(requireContext().getResources().getString(R.string.dialog_no))
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
            boolean isThousandBirrVoucher = binding.vouchers500birrCardDescription.getText().toString().startsWith("1000");
            if (isThousandBirrVoucher) {
                selectedCardFaceValue = "1000.00";
            }

            if (!singlePrintEnabled) {
                amountSelector.setText("1");
                selectedAmount.setText("1 " + requireContext().getResources().getString(R.string.voucher_selected));

                if (isThousandBirrVoucher) {
                    currentlySelectedCard.setText(requireContext().getResources().getString(R.string.thousand_birr_voucher));
                } else {
                    currentlySelectedCard.setText(requireContext().getResources().getString(R.string.fivehundred_birr_voucher));
                }

                bottomSheetDialog.show();
            } else {
                final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(requireActivity())
                        .setBackgroundColor(R.color.colorPrimary)
                        .setimageResource(R.drawable.ic_question_mark)
                        .setTextTitle(requireContext().getResources().getString(R.string.dialog_confirmation))
                        .setTitleColor(R.color.colorLightWhite)
                        .setTextSubTitle(requireContext().getResources().getString(R.string.vouchers_overprint_warning_500))
                        .setSubtitleColor(R.color.colorLightWhite)
                        .setBodyColor(R.color.red)
                        .setPositiveButtonText(requireContext().getResources().getString(R.string.dialog_yes))
                        .setNegativeButtonText(requireContext().getResources().getString(R.string.dialog_no))
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

        binding.vouchers500birrCardDescription.setOnClickListener(view -> {
            if (binding.vouchers500birrCardDescription.getText().toString().startsWith("500")) {
                binding.vouchers500birrCardDescription.setText("1000 Birr");
            } else {
                binding.vouchers500birrCardDescription.setText("500 Birr");
            }
        });

        binding.vouchers250birrCardDescription.setOnClickListener(view -> {
            if (binding.vouchers250birrCardDescription.getText().toString().startsWith("200")) {
                binding.vouchers250birrCardDescription.setText("250 Birr");
            } else {
                binding.vouchers250birrCardDescription.setText("200 Birr");
            }
        });

        binding.vouchers1000birrCard.setOnClickListener(view -> {
            selectedCardFaceValue = "20.00";
            if (!singlePrintEnabled) {
                amountSelector.setText("1");
                selectedAmount.setText("1 " + requireContext().getResources().getString(R.string.voucher_selected));
                currentlySelectedCard.setText("20 Birr");
                bottomSheetDialog.show();
            } else {
//                final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(requireActivity())
//                        .setBackgroundColor(R.color.colorPrimary)
//                        .setimageResource(R.drawable.ic_question_mark)
//                        .setTextTitle("Confirmation")
//                        .setTitleColor(R.color.colorLightWhite)
//                        .setTextSubTitle("Do you really want to print 20 Birr Card?")
//                        .setSubtitleColor(R.color.colorLightWhite)
//                        .setBodyColor(R.color.red)
//                        .setPositiveButtonText("Yes")
//                        .setNegativeButtonText("No")
//                        .setOnNegativeClicked((view12, dialog) -> dialog.dismiss())
//                        .setPositiveColor(R.color.colorLightWhite)
//                        .setOnPositiveClicked((view1, dialog) -> {
                singlePrint(selectedCardFaceValue);
//                        })
//                        .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
//                        .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
//                        .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
//                        .build();
//                alert.show();
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
////                Toast.makeText(requireActivity(),"event called",Toast.LENGTH_SHORT).show();
//                if (amountSelector.length() > 0) {
//                    Toast.makeText(requireActivity(),"event called",Toast.LENGTH_SHORT).show();
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
                selectedAmount.setText(requireContext().getResources().getString(R.string.voucher_selected) + " " + amountSelector.getText().toString());
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
//                        Toast.makeText(requireActivity(),"Amount is not number",Toast.LENGTH_SHORT).show();
////                        selectedAmount.setText("");
//                    }
//                } else {
//                    Toast.makeText(requireActivity(),"Amount is not number1",Toast.LENGTH_SHORT).show();
//
////                    voucherPrint.setEnabled(false);
////                    voucherPrint.setBackgroundResource(R.drawable.btn_rounded_grayed);
//                }
            }
        });

        voucherPrint.setOnClickListener(view -> {
            if (amountSelector.getText().toString().trim().length() == 0) {
                Toast.makeText(requireActivity(), requireContext().getResources().getString(R.string.amount_should_not_be_empty), Toast.LENGTH_SHORT).show();

            } else if (Integer.parseInt(amountSelector.getText().toString()) > 500 || Integer.parseInt(amountSelector.getText().toString()) == 0) {
                Toast.makeText(requireActivity(), requireContext().getResources().getString(R.string.amount_should_not_exceed), Toast.LENGTH_SHORT).show();

            } else if (!SelectedDevice.isDeviceSelected() && isBluetooth) {
                Toast.makeText(requireActivity(), requireContext().getResources().getString(R.string.select_printer), Toast.LENGTH_SHORT).show();
            } else {
                if (amountSelector.length() > 0) {
                    if (isNumeric(amountSelector.getText().toString())) {
                        bulkPinList.clear();
                        Toast.makeText(requireActivity(), amountSelector.getText().toString() + String.format(requireContext().getResources().getString(R.string.printing_vouchers), amountSelector.getText().toString()), Toast.LENGTH_SHORT).show();
                        HashMap<String, Integer> bulkMap = new HashMap<>();
                        System.out.println("**************************");
                        System.out.println(Integer.parseInt(amountSelector.getText().toString()));
                        System.out.println(bulkMap.size());
                        bulkMap.put(selectedCardFaceValue, Integer.parseInt(amountSelector.getText().toString()));
                        if (Integer.parseInt(amountSelector.getText().toString()) >= 200) {
                            final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(requireActivity())
                                    .setBackgroundColor(R.color.colorPrimary)
                                    .setimageResource(R.drawable.ic_question_mark)
                                    .setTextTitle(requireContext().getResources().getString(R.string.dialog_confirmation))
                                    .setTitleColor(R.color.colorLightWhite)
                                    .setTextSubTitle(String.format(requireContext().getResources().getString(R.string.print_warning_1), amountSelector.getText().toString()))
                                    .setSubtitleColor(R.color.colorLightWhite)
                                    .setBodyColor(R.color.red)
                                    .setPositiveButtonText(requireContext().getResources().getString(R.string.dialog_yes))
                                    .setNegativeButtonText(requireContext().getResources().getString(R.string.dialog_no))
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
                                                        if (SelectedDevice.getSelectedDevice().isConnected()) {
                                                            purchaseVoucherBluetoothPrinter(bulkMap, requireActivity());
                                                        } else {
                                                            Toast.makeText(requireActivity(), requireContext().getResources().getString(R.string.check_bluetooth_connection), Toast.LENGTH_SHORT).show();
                                                        }
                                                    } catch (EscPosConnectionException e) {
                                                        e.printStackTrace();
                                                        new AlertDialog.Builder(requireActivity(), R.style.DialogTheme)
                                                                .setTitle("Broken connection")
                                                                .setMessage(e.getMessage())
                                                                .show();
                                                    }
                                                } else {
                                                    new AlertDialog.Builder(requireActivity(), R.style.DialogTheme)
                                                            .setTitle("Broken connection")
                                                            .setMessage("Please Connect Your Device")
                                                            .show();
                                                }
                                            } else {
                                                purchaseVoucherSunmiPrinter(bulkMap, requireActivity());
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
                                            if (SelectedDevice.getSelectedDevice().isConnected()) {
                                                purchaseVoucherBluetoothPrinter(bulkMap, requireActivity());
                                            } else {
                                                Toast.makeText(requireActivity(), requireContext().getResources().getString(R.string.check_bluetooth_connection), Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (EscPosConnectionException e) {
                                            e.printStackTrace();

                                            new AlertDialog.Builder(requireActivity(), R.style.DialogTheme)
                                                    .setTitle("Broken connection")
                                                    .setMessage(e.getMessage())
                                                    .show();
                                        }


                                    } else {
                                        new AlertDialog.Builder(requireActivity(), R.style.DialogTheme)
                                                .setTitle("Broken connection")
                                                .setMessage("Please Connect Your Device")
                                                .show();
                                    }

                                } else {
                                    purchaseVoucherSunmiPrinter(bulkMap, requireActivity());
                                }
                            } else {
//                    printOfflineVouchers(selectedCardFaceValue, Integer.parseInt(amountSelector.getText().toString()));
                            }
                        }
                    } else {
                        Toast.makeText(requireActivity(), requireContext().getResources().getString(R.string.enter_valid_number), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(requireActivity(), requireContext().getResources().getString(R.string.enter_valid_number), Toast.LENGTH_LONG).show();
                }
            }
        });

        buyAirtime.setOnClickListener(view -> {
            if (Integer.parseInt(amountSelector.getText().toString()) == 1) {
                if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) requireActivity(), new String[]{Manifest.permission.CALL_PHONE}, 1);
                } else {
                    MaterialDialog airtimePhoneInput = new MaterialDialog.Builder(requireActivity())
                            .customView(R.layout.voucher_airtime_dialog, false)
                            .positiveText(requireContext().getResources().getString(R.string.send_airtime))
                            .negativeText(requireContext().getResources().getString(R.string.dialog_cancel))
                            .cancelable(false)
                            .autoDismiss(false)
                            .onPositive((dialog, which) -> {
                                EditText phone = (EditText) dialog.findViewById(R.id.voucher_airtime_phone);
                                HashMap<String, Integer> bulkMap = new HashMap<>();
                                bulkMap.put(selectedCardFaceValue, Integer.parseInt(amountSelector.getText().toString()));
                                if (!buyingAirtime) {
                                    purchaseAirtime(bulkMap, requireActivity(), phone.getText().toString());
                                } else {
                                    Toast.makeText(requireActivity(), "Already In Progress", Toast.LENGTH_LONG).show();
                                }
                            }).onNegative((dialog, which) -> {
                                dialog.dismiss();
                            })
                            .build();
                    airtimePhoneInput.show();
                }
            } else {
                Toast.makeText(requireActivity(), requireContext().getResources().getString(R.string.airtime_limit_is_1), Toast.LENGTH_SHORT).show();
            }
        });


        voucherDownload.setOnClickListener(view -> {
            HashMap<String, Integer> bulkMap = new HashMap<>();
            bulkMap.put(selectedCardFaceValue, Integer.parseInt(amountSelector.getText().toString()));
            downloadVoucher(bulkMap);
        });

        voucherBluetoothSelector.setOnClickListener(view -> {
            if (SelectedDevice.isDeviceSelected()) {
                Toast.makeText(requireActivity(), requireContext().getResources().getString(R.string.already_selected_printer), Toast.LENGTH_LONG).show();
            } else {
                browseBluetoothDevice();
            }
        });

        voucherConnectBluetooth.setOnClickListener(view -> {
            if (SelectedDevice.isDeviceSelected()) {
                Toast.makeText(requireActivity(), requireContext().getResources().getString(R.string.already_selected_printer), Toast.LENGTH_LONG).show();
            } else {
                browseBluetoothDevice();
            }
        });

        bluetoothPrinterDisconnector.setOnClickListener(view -> {
            if (SelectedDevice.isDeviceSelected()) {
                SelectedDevice.getSelectedDevice().disconnect();
            }
            SelectedDevice.setSelectedDevice(null);
            SelectedDevice.setIsDeviceSelected(false);
            binding.vouchersPrinterStatus.setText(requireContext().getResources().getString(R.string.bluetooth_printer_disconnected));
            binding.vouchersPrinterStatus.setTextColor(Color.RED);
            voucherBluetoothSelector.setText(requireContext().getResources().getString(R.string.bluetooth_printer_disconnected));
            binding.vouchersBluetoothButton.setColorFilter(Color.parseColor("#FFFFFF"));
            voucherBluetoothSelector.setBackgroundColor(Color.parseColor("#ECECEC"));
            voucherBluetoothSelector.setTextColor(Color.parseColor("#214A6A"));
            voucherBluetoothSelector.setEnabled(true);
            bluetoothPrinterDisconnector.setVisibility(View.GONE);
            voucherPrint.setEnabled(false);
            voucherPrint.setBackgroundResource(R.drawable.btn_rounded_grayed);

        });
        // endregion

//        binding.vouchersBottomNavigation.addBubbleListener(id -> {
//            switch (id) {
//                case R.id.home_bottom_navigation_cards:
//                    Toast.makeText(requireActivity(), "You are already on the Cards Page", Toast.LENGTH_SHORT).show();
//                    return;
//                case R.id.home_bottom_navigation_agnets:
//                    startActivity(new Intent(requireActivity(), MyAgents.class));
//                    return;
//                case R.id.home_bottom_navigation_transfers:
//                    startActivity(new Intent(requireActivity(), FundTransaction.class));
//                    return;
//                case R.id.home_bottom_navigation_report:
//                    Toast.makeText(requireActivity(), "Comming Soon!", Toast.LENGTH_SHORT).show();
//                    return;
//                default:
//                    Toast.makeText(requireActivity(), "Comming Soon!", Toast.LENGTH_SHORT).show();
//                    return;
//
//            }
//        });

        binding.vouchersSinglePrintSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            singlePrintEnabled = b;
        });

        binding.vouchersBackButton.setOnClickListener(v -> {
//            SharedPreferences sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
////            Toast.makeText(requireActivity(), "HomePageMode: " + sharedPreferences.getString("HomePageMode", ""), Toast.LENGTH_LONG).show();
//            if (sharedPreferences.getString("HomePageMode", "1").equals("1")) {
////                Intent intent = new Intent(requireActivity(), Home.class);
//                Intent intent = new Intent(requireActivity(), Voucher.class);
//                startActivity(intent);
//            } else {
//                onBackPressed();
//            }
            NewHome.drawer.openMenu();
        });

        binding.vouchersBluetoothButton.setOnClickListener(view -> {
//            menuBottomSheetDialog.show();
//            startActivity(new Intent(requireActivity(), NewHome.class));
            // todo bluetooth connect
            // region Connect To Bluetooth Device
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Dexter.withActivity((Activity) getContext())
                        .withPermissions(
                                Manifest.permission.BLUETOOTH_CONNECT)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                // region check if all permissions are granted
                                if (report.areAllPermissionsGranted()) {
                                    if (SelectedDevice.isDeviceSelected()) {
                                        MaterialDialog dialog = new MaterialDialog.Builder(requireActivity())
                                                .customView(R.layout.dialog_lottie_message, false)
                                                .cancelable(true)
                                                .autoDismiss(true)
                                                .build();

                                        assert dialog.getCustomView() != null;

                                        TextView dialogLottieTitle = dialog.getCustomView().findViewById(R.id.dialogLottieTitle);
                                        TextView dialogLottieDescription = dialog.getCustomView().findViewById(R.id.dialogLottieDescription);

                                        dialogLottieTitle.setText(String.format(requireContext().getResources().getString(R.string.connected_to_printer_title), SelectedDevice.getSelectedDevice().getDevice().getName()));
                                        dialogLottieDescription.setText(String.format(requireContext().getResources().getString(R.string.connected_to_printer_desc), SelectedDevice.getSelectedDevice().getDevice().getName()));

                                        if (!dialog.isShowing()) dialog.show();
                                    } else {
                                        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                                        if (mBluetoothAdapter == null) {
                                            final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(requireActivity())
                                                    .setBackgroundColor(R.color.colorPrimaryWarning)
                                                    .setimageResource(R.drawable.ic_dialog_warning)
                                                    .setTextTitle(requireContext().getResources().getString(R.string.dialog_warning))
                                                    .setTitleColor(R.color.colorLightWhite)
                                                    .setTextSubTitle(requireContext().getResources().getString(R.string.does_not_support_bt))
                                                    .setSubtitleColor(R.color.colorLightWhite)
                                                    .setBodyColor(R.color.red)
                                                    .setPositiveButtonText(requireContext().getResources().getString(R.string.dialog_okay))
                                                    .setPositiveColor(R.color.colorLightWhite)
                                                    .setOnPositiveClicked((view1, dialog) -> dialog.dismiss())
                                                    .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
                                                    .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                                                    .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
                                                    .build();
                                            alert.show();

                                            // Device does not support Bluetooth
                                            voucherBluetoothSelector.setText(requireContext().getResources().getString(R.string.bluetooth_prinet_not_supported));
                                            binding.vouchersBluetoothButton.setColorFilter(Color.parseColor("#D11616"));
                                            voucherBluetoothSelector.setBackgroundColor(Color.parseColor("#D11616"));
                                            voucherBluetoothSelector.setTextColor(Color.parseColor("#FFFFFF"));
                                            voucherBluetoothSelector.setEnabled(false);
                                            voucherPrint.setEnabled(false);
                                            voucherPrint.setBackgroundResource(R.drawable.btn_rounded_grayed);
                                        } else if (!mBluetoothAdapter.isEnabled()) {
                                            final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(requireActivity())
                                                    .setBackgroundColor(R.color.colorPrimaryWarning)
                                                    .setimageResource(R.drawable.ic_dialog_warning)
                                                    .setTextTitle(requireContext().getResources().getString(R.string.dialog_warning))
                                                    .setTitleColor(R.color.colorLightWhite)
                                                    .setTextSubTitle(requireContext().getResources().getString(R.string.turn_on_bluetooth_first))
                                                    .setSubtitleColor(R.color.colorLightWhite)
                                                    .setBodyColor(R.color.red)
                                                    .setPositiveButtonText(requireContext().getResources().getString(R.string.dialog_okay))
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
                                            voucherBluetoothSelector.setText(requireContext().getResources().getString(R.string.turn_on_bluetooth));
                                            binding.vouchersBluetoothButton.setColorFilter(Color.parseColor("#D11616"));
                                            binding.vouchersPrinterStatus.setText(requireContext().getResources().getString(R.string.bluetooth_off));
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
                                    // endregion
                                }
                                // endregion

                                // check for permanent denial of any permission
                                if (report.isAnyPermissionPermanentlyDenied()) {
                                    // show alert dialog navigating to Settings
                                    final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(requireActivity())
                                            .setBackgroundColor(R.color.colorPrimaryWarning)
                                            .setimageResource(R.drawable.ic_dialog_warning)
                                            .setTextTitle(requireContext().getResources().getString(R.string.dialog_warning))
                                            .setTitleColor(R.color.colorLightWhite)
                                            .setTextSubTitle(requireContext().getResources().getString(R.string.does_not_support_bt))
                                            .setSubtitleColor(R.color.colorLightWhite)
                                            .setBodyColor(R.color.red)
                                            .setPositiveButtonText(requireContext().getResources().getString(R.string.dialog_okay))
                                            .setPositiveColor(R.color.colorLightWhite)
                                            .setOnPositiveClicked((view1, dialog) -> dialog.dismiss())
                                            .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
                                            .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                                            .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
                                            .build();
                                    alert.show();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).
                        withErrorListener(error -> Toast.makeText(getContext(), "Some Bluetooth Related Error occurred!", Toast.LENGTH_SHORT).show())
                        .onSameThread()
                        .check();
            } else {
                if (SelectedDevice.isDeviceSelected()) {
                    MaterialDialog dialog = new MaterialDialog.Builder(requireActivity())
                            .customView(R.layout.dialog_lottie_message, false)
                            .cancelable(true)
                            .autoDismiss(true)
                            .build();

                    assert dialog.getCustomView() != null;

                    TextView dialogLottieTitle = dialog.getCustomView().findViewById(R.id.dialogLottieTitle);
                    TextView dialogLottieDescription = dialog.getCustomView().findViewById(R.id.dialogLottieDescription);

                    dialogLottieTitle.setText(String.format(requireContext().getResources().getString(R.string.connected_to_printer_title), SelectedDevice.getSelectedDevice().getDevice().getName()));
                    dialogLottieDescription.setText(String.format(requireContext().getResources().getString(R.string.connected_to_printer_desc), SelectedDevice.getSelectedDevice().getDevice().getName()));

                    if (!dialog.isShowing()) dialog.show();
                } else {
                    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (mBluetoothAdapter == null) {
                        final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(requireActivity())
                                .setBackgroundColor(R.color.colorPrimaryWarning)
                                .setimageResource(R.drawable.ic_dialog_warning)
                                .setTextTitle(requireContext().getResources().getString(R.string.dialog_warning))
                                .setTitleColor(R.color.colorLightWhite)
                                .setTextSubTitle(requireContext().getResources().getString(R.string.does_not_support_bt))
                                .setSubtitleColor(R.color.colorLightWhite)
                                .setBodyColor(R.color.red)
                                .setPositiveButtonText(requireContext().getResources().getString(R.string.dialog_okay))
                                .setPositiveColor(R.color.colorLightWhite)
                                .setOnPositiveClicked((view1, dialog) -> dialog.dismiss())
                                .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
                                .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                                .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
                                .build();
                        alert.show();

                        // Device does not support Bluetooth
                        voucherBluetoothSelector.setText(requireContext().getResources().getString(R.string.bluetooth_prinet_not_supported));
                        binding.vouchersBluetoothButton.setColorFilter(Color.parseColor("#D11616"));
                        voucherBluetoothSelector.setBackgroundColor(Color.parseColor("#D11616"));
                        voucherBluetoothSelector.setTextColor(Color.parseColor("#FFFFFF"));
                        voucherBluetoothSelector.setEnabled(false);
                        voucherPrint.setEnabled(false);
                        voucherPrint.setBackgroundResource(R.drawable.btn_rounded_grayed);
                    } else if (!mBluetoothAdapter.isEnabled()) {
                        final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(requireActivity())
                                .setBackgroundColor(R.color.colorPrimaryWarning)
                                .setimageResource(R.drawable.ic_dialog_warning)
                                .setTextTitle(requireContext().getResources().getString(R.string.dialog_warning))
                                .setTitleColor(R.color.colorLightWhite)
                                .setTextSubTitle(requireContext().getResources().getString(R.string.turn_on_bluetooth_first))
                                .setSubtitleColor(R.color.colorLightWhite)
                                .setBodyColor(R.color.red)
                                .setPositiveButtonText(requireContext().getResources().getString(R.string.dialog_okay))
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
                        voucherBluetoothSelector.setText(requireContext().getResources().getString(R.string.turn_on_bluetooth));
                        binding.vouchersBluetoothButton.setColorFilter(Color.parseColor("#D11616"));
                        binding.vouchersPrinterStatus.setText(requireContext().getResources().getString(R.string.bluetooth_off));
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
                // endregion
            }
            // endregion
        });

        try {
            Tovuti.from(requireActivity()).monitor((connectionType, isConnected, isFast) -> {
                if (isConnected) {
                    this.isConnected = true;
//                if (!OfflineVouchersManager.isSyncing()) {
//                    new OfflineVouchersManager(requireActivity()).sync();
//                } else {
////                    Toast.makeText(requireActivity(),"Sync Already In Progress  Vouchers",Toast.LENGTH_LONG).show();
//                }
                    binding.vouchersOnlineStatus.setText(requireContext().getResources().getString(R.string.network_connected));
                    binding.vouchersOnlineStatus.setTextColor(Color.GREEN);
                    setCardDescription(true);
                } else {
                    this.isConnected = false;
                    binding.vouchersOnlineStatus.setText(requireContext().getResources().getString(R.string.network_offline));
                    binding.vouchersOnlineStatus.setTextColor(Color.MAGENTA);
                    setCardDescription(false);
                }
            });
        } catch (Exception e) {

        }
    }
    // endregion

    // region Setup Broadcast Handlers
    private void setupBroadcastListeners() {
        IntentFilter filter3 = new IntentFilter();
        filter3.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter3.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        requireActivity().registerReceiver(mBroadcastReceiver3, filter3);

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        requireActivity().registerReceiver(mBroadcastReceiver1, filter);
    }
    // endregion

    // region Single Print
    private void singlePrint(String selectedCardFaceValue) {
        HashMap<String, Integer> bulkMap = new HashMap<>();
        bulkMap.put(selectedCardFaceValue, 1);

        if (isNetworkConnected()) {
            Intent intent = new Intent(requireActivity(), BluetoothPrinter.class);
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
            binding.vouchers5birrCardDescription.setText(requireContext().getResources().getString(R.string.five_birr_voucher));
            binding.vouchers10birrCardDescription.setText(requireContext().getResources().getString(R.string.ten_birr_voucher));
            binding.vouchers15birrCardDescription.setText(requireContext().getResources().getString(R.string.fifteen_birr_voucher));
            binding.vouchers25birrCardDescription.setText(requireContext().getResources().getString(R.string.twentyfive_birr_voucher));
            binding.vouchers50birrCardDescription.setText(requireContext().getResources().getString(R.string.fifty_birr_voucher));
            binding.vouchers100birrCardDescription.setText(requireContext().getResources().getString(R.string.hunderd_birr_voucher));
            binding.vouchers250birrCardDescription.setText("200 Birr");
            binding.vouchers500birrCardDescription.setText(requireContext().getResources().getString(R.string.fivehundred_birr_voucher));
            binding.vouchers1000birrCardDescription.setText("20 Birr");
        } else {
            binding.vouchers5birrCardDescription.setText(requireContext().getResources().getString(R.string.five_birr_voucher) + " (" + getOfflineAvailableQuantity("5.00") + ")");
            binding.vouchers10birrCardDescription.setText(requireContext().getResources().getString(R.string.ten_birr_voucher) + " (" + getOfflineAvailableQuantity("10.00") + ")");
            binding.vouchers15birrCardDescription.setText(requireContext().getResources().getString(R.string.fifteen_birr_voucher) + " (" + getOfflineAvailableQuantity("15.00") + ")");
            binding.vouchers25birrCardDescription.setText(requireContext().getResources().getString(R.string.twentyfive_birr_voucher) + " (" + getOfflineAvailableQuantity("25.00") + ")");
            binding.vouchers50birrCardDescription.setText(requireContext().getResources().getString(R.string.fifty_birr_voucher) + " (" + getOfflineAvailableQuantity("50.00") + ")");
            binding.vouchers100birrCardDescription.setText(requireContext().getResources().getString(R.string.hunderd_birr_voucher) + " (" + getOfflineAvailableQuantity("100.00") + ")");
            binding.vouchers250birrCardDescription.setText(requireContext().getResources().getString(R.string.twohundredfifty_birr_voucher) + " (" + getOfflineAvailableQuantity("200.00") + ")");
            binding.vouchers500birrCardDescription.setText(requireContext().getResources().getString(R.string.fivehundred_birr_voucher) + " (" + getOfflineAvailableQuantity("500.00") + ")");
            binding.vouchers1000birrCardDescription.setText(requireContext().getResources().getString(R.string.thousand_birr_voucher) + " (" + getOfflineAvailableQuantity("20.00") + ")");
        }
    }
    // endregion

    // region Initialize Other Elements
    private void init() throws NoSuchPaddingException, NoSuchAlgorithmException {
        getUserBalance(requireActivity());
        VoucherBulkPrint.setBulkPrint(null);
        VoucherBulkPrint.setIsVouchersLoaded(false);
        offlineDBManager = new OfflineDBManager(requireActivity());
        cryptLib = new CryptLib();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        if (sharedPreferences.getString("BluetoothPrinterMode", "1").equals("1")) {
            isBluetooth = true;
        }
        if (sharedPreferences.getString("LargePrinterMode", "").equals("1")) {
            isSmallPrinter = false;
        }
        if (isBluetooth) {
            if (SelectedDevice.isDeviceSelected()) {
                voucherBluetoothSelector.setText(String.format(requireContext().getResources().getString(R.string.connected_to_printer), SelectedDevice.getSelectedDevice().getDevice().getName()));
                binding.vouchersPrinterStatus.setText(String.format(requireContext().getResources().getString(R.string.connected_to_printer), SelectedDevice.getSelectedDevice().getDevice().getName()));
                binding.vouchersPrinterStatus.setText(requireContext().getResources().getString(R.string.bluetooth_printer_connected));
                binding.vouchersPrinterStatus.setTextColor(Color.GREEN);
                bluetoothPrinterDisconnector.setVisibility(View.VISIBLE);
                voucherBluetoothSelector.setBackgroundColor(Color.parseColor("#4CAF50"));
                binding.vouchersBluetoothButton.setColorFilter(Color.parseColor("#4CAF50"));
                voucherBluetoothSelector.setTextColor(Color.parseColor("#ffffff"));
                voucherPrint.setEnabled(true);
                voucherPrint.setBackgroundResource(R.drawable.btn_rounded);
            } else {
                Toast.makeText(requireActivity(), requireContext().getResources().getString(R.string.no_device_connected), Toast.LENGTH_LONG).show();
                binding.vouchersPrinterStatus.setText(requireContext().getResources().getString(R.string.bluetooth_printer_disconnected));
                binding.vouchersBluetoothButton.setColorFilter(Color.parseColor("#D11616"));
                binding.vouchersPrinterStatus.setTextColor(Color.RED);
                voucherPrint.setEnabled(false);
                voucherPrint.setBackgroundResource(R.drawable.btn_rounded_grayed);
            }
        }
    }
    // endregion


    // endregion

    // region Get User Balance
    public void getUserBalance(final Context passedContext) {
        try {
            binding.vouchersBalanceTitle.setText(requireActivity().getResources().getString(R.string.updating_balance));
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
                            if (balanceEndPointResponseData.code() == 200) {
                                StateManager.setBalance(String.valueOf(balanceEndPointResponseData.body().getBalance()));
                                DecimalFormat df = new DecimalFormat();
                                df.setMaximumFractionDigits(2);
                                Double balance = 0d;
                                try {
                                    balance = Double.parseDouble(String.valueOf(balanceEndPointResponseData.body().getBalance()));
                                } catch (Exception e) {
                                    Toast.makeText(requireActivity(), requireContext().getResources().getString(R.string.unable_to_get_balance), Toast.LENGTH_SHORT).show();
                                }
                                binding.balanceVocuher.setText(String.format("%.2f", balance) + " " + requireContext().getResources().getString(R.string.default_currency_short));
                                binding.vouchersBalanceTitle.setText(requireContext().getResources().getString(R.string.balance_updated));
                            } else {
                                Toast.makeText(passedContext, requireContext().getResources().getString(R.string.someting_went_worong), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            System.out.println("has" + e);
                        }

                        @Override
                        public void onComplete() {
                            binding.vouchersBalanceTitle.setText(requireContext().getResources().getString(R.string.current_balance));
                        }
                    });
            System.out.println("Bal Update");
        } catch (Exception e) {
            System.out.println("Unable to update balance");
            //            getUserBalance(requireContext() );
        }
    }
    // endregion

    // region Print offline Vouchers
    public void printOfflineVouchers(String amount, int quantity) {
        List<DownloadedVouchers> downloadedVouchers;

        downloadedVouchers = offlineDBManager.printVouchers(amount, quantity);
        if (downloadedVouchers.size() < quantity) {
            final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(requireActivity())
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
            intent = new Intent(requireActivity(), BluetoothPrinter.class);
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
            MaterialDialog downloadProgress = new MaterialDialog.Builder(requireActivity())
                    .customView(R.layout.voucher_download_dialog, false)
                    .cancelable(false)
                    .autoDismiss(false)
                    .build();
            downloadProgress.show();
            Context contextForCompleteDialog = requireActivity();

            APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
            myInterface.retailerBuyVouchers(User.getUserSessionToken(), APP_VERSION, voucher, "downloaded", null)
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
                                user.userBalance(requireActivity(), User.getUserSessionToken());

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
                                            FancyToast.makeText(requireActivity(), mError.getErrors().get(0).getMsg(), FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                                        } catch (IOException e) {
                                            // handle failure to read error
                                        }
                                        break;
                                    case 500:
                                        FancyToast.makeText(requireActivity(), "Server Error! Please contact the system administrators.", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                                        break;
                                    default:
                                        FancyToast.makeText(requireActivity(), "Unknown Error! Something went wrong.", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                                        break;
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            downloadProgress.dismiss();
                            new MaterialDialog.Builder(requireActivity())
                                    .customView(R.layout.voucher_download_error_dialog, false)
                                    .cancelable(true)
                                    .autoDismiss(true)
                                    .build()
                                    .show();
                        }

                        @Override
                        public void onComplete() {
                            downloadProgress.dismiss();
                            getUserBalance(requireActivity());
                        }
                    });
        } else {
            new MaterialDialog.Builder(requireActivity())
                    .customView(R.layout.connection_error_dialog, false)
                    .cancelable(true)
                    .autoDismiss(true)
                    .build()
                    .show();
        }
    }
    //endregion

    // region Purchase Voucher (Bluetooth)
    public void purchaseVoucherBluetoothPrinter(HashMap<String, Integer> voucher, Context context) throws EscPosConnectionException {
        APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
        myInterface.retailerBuyVouchers(User.getUserSessionToken(), APP_VERSION, voucher, null, null)
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
                            user.userBalance(requireActivity(), User.getUserSessionToken());

                            int pinCounter = 0;
                            for (int i = 0; i < lists.size(); i++) {
                                for (int x = 0; x < lists.get(i).getVoucher().size(); x++) {
                                    bulkPinList.add(lists.get(i).getVoucher().get(x));

                                }


                            }

                            Map<String, com.example.fibath.classes.model.Voucher> map = new LinkedHashMap<>();
                            for (com.example.fibath.classes.model.Voucher voucherrr : bulkPinList) {
                                map.put(voucherrr.getVoucherNumber(), voucherrr);
                            }
                            bulkPinList.clear();
                            bulkPinList.addAll(map.values());
//
                            for (int x = 0; x < bulkPinList.size(); x++) {
                                try {
                                    printBill(bulkPinList.size() - x, bulkPinList.get(x).getVoucherAmount(), bulkPinList.get(x).getVoucherNumber(), bulkPinList.get(x).getVoucherSerialNumber(), bulkPinList.get(x).getVoucherExpiryDate(), 0, lists.get(0).getBulk_id());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                            getUserBalance(requireActivity());
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
                                    Toast.makeText(context, requireContext().getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(context, "unknown error", Toast.LENGTH_SHORT).show();
                                    break;
                            }

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        getUserBalance(requireActivity());
                        System.out.println("has" + e);
                        voucherPrint.setEnabled(true);
                        voucherPrint.setBackgroundResource(R.drawable.btn_rounded);
                    }

                    @Override
                    public void onComplete() {
                        getUserBalance(requireActivity());
                        voucherPrint.setEnabled(true);
                        voucherPrint.setBackgroundResource(R.drawable.btn_rounded);
                    }
                });
    }
    // endregion

    //region Purchase Voucher (Sunmi)
    public void purchaseVoucherSunmiPrinter(HashMap<String, Integer> voucher, Context context) {
        APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
        myInterface.retailerBuyVouchers(User.getUserSessionToken(), APP_VERSION, voucher, null, null)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<ArrayList<VoucherPurchaseResponse>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Response<ArrayList<VoucherPurchaseResponse>> voucherPurchaseResponse) {
                        if (voucherPurchaseResponse.code() == 201) {
                            List<VoucherPurchaseResponse> lists = voucherPurchaseResponse.body();
//                    System.out.println(list);
                            User user = new User();
                            user.userBalance(requireActivity(), User.getUserSessionToken());

                            if (lists.get(0).getVoucher().size() == 1) {
                                try {
                                    SunmiV2Printer sunmiV2Printer = new SunmiV2Printer();
//                            sunmiV2Printer.printEthiotelVoucher(1, lists.get(0).getVoucher().get(0).getVoucherAmount(), lists.get(0).getVoucher().get(0).getVoucherNumber(), lists.get(0).getVoucher().get(0).getVoucherSerialNumber(), lists.get(0).getVoucher().get(0).getVoucherExpiryDate(), lists.get(0).getBulk_id());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                for (int i = 0; i < lists.size(); i++) {
                                    lists.get(0).getVoucher().get(i).setBulkId(lists.get(0).getBulk_id());

                                    bulkPinList.addAll(lists.get(i).getVoucher());
                                }

                                VoucherBulkPrint.setBulkPrint(bulkPinList);

                                VoucherBulkPrint.setIsVouchersLoaded(true);
                                Intent printerIntent;
                                printerIntent = new Intent(requireActivity(), SunmiV2Printer.class);
                                printerIntent.putExtra("offline", "false");
                                printerIntent.putExtra("isReprint", "false");
                                printerIntent.putExtra("isBulk", "false");
                                System.out.println("(((((((((((((((((((((((((");
                                System.out.println("L: " + bulkPinList.size());
                                startActivity(printerIntent);
                            }
                        } else {
                            switch (voucherPurchaseResponse.code()) {
                                case 400:
                                    Gson gson = new GsonBuilder().create();
                                    ErrorResponse mError;
                                    try {
                                        mError = gson.fromJson(voucherPurchaseResponse.errorBody().string(), ErrorResponse.class);
                                        FancyToast.makeText(requireActivity(), mError.getErrors().get(0).getMsg(), FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                                    } catch (IOException e) {
                                        // handle failure to read error
                                    }
                                    break;
                                case 500:
                                    FancyToast.makeText(requireActivity(), requireContext().getResources().getString(R.string.server_error_long), FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                                    break;
                                default:
                                    FancyToast.makeText(requireActivity(), requireContext().getResources().getString(R.string.server_error_long), FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        getUserBalance(requireActivity());
                        System.out.println("has" + e);
                        voucherPrint.setEnabled(true);
                        voucherPrint.setBackgroundResource(R.drawable.btn_rounded);
                    }

                    @Override
                    public void onComplete() {
                        getUserBalance(requireActivity());
                        voucherPrint.setEnabled(true);
                        voucherPrint.setBackgroundResource(R.drawable.btn_rounded);
                    }
                });
//
    }


    //endregion

    // region Print Bill
    protected void printBill(int pageNumber, String amount, String pinNo, String serial, String expiryDate, int reDownload, String bulkId) throws Exception {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) requireActivity(), new String[]{Manifest.permission.BLUETOOTH}, BluetoothPrinter.PERMISSION_BLUETOOTH);
            System.out.println("outside print");

        } else {
            try {
                String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

                pinNo = cryptLib.decryptCipherTextWithRandomIV(pinNo, "123456789");
                String originalPin = pinNo;

                String pinOne = pinNo.substring(0, 4);
                String pinTwo = pinNo.substring(4, 8);
                String pinThree = pinNo.substring(8, 12);
                String pinFour = pinNo.substring(12);
                pinNo = pinOne + "-" + pinTwo + "-" + pinThree + "-" + pinFour;
                System.out.println("inside print");
//                selectedDevice = BluetoothPrintersConnections.selectFirstPaired();

                EscPosPrinter printer = new EscPosPrinter(SelectedDevice.getSelectedDevice(), 203, isSmallPrinter ? 48f : 72f, isSmallPrinter ? 32 : 48);
                System.out.println("selected redownload is" + reDownload);
                printer.printFormattedText(
                        "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, this.requireActivity().getResources().getDrawableForDensity(R.drawable.horizon_ethiotelecom_logo, DisplayMetrics.DENSITY_MEDIUM)) + "</img>\n" +
                                "[C]<font size='wide'>" + amount.replace(".00", "") + " Birr</font>\n" +
                                "[C]------e-voucher Pin------\n" +
//                                "[C]<font size='big'>" + pinNo + "</font>\n" +
                                "[C]<b><font size='tall'>" + pinNo + "</font></b>\n" +

                                "[C]ToRecharge*705*" + originalPin + "#\n" +
                                "[C] SN: " + serial + "\n" +
                                "[C]<b>Agent: " + User.getUserSessionUsername() + "</b>\n" +
                                "[C]PD:" + date + " ED:" + expiryDate + "\n" +
//                                "[C]--------------------------------\n"+
                                "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, this.requireActivity().getResources().getDrawableForDensity(R.drawable.warning_message_two, DisplayMetrics.DENSITY_MEDIUM)) + "</img>\n" +
//                                "[C]--------------------------------\n"+
//                                "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, this.requireActivity().getResources().getDrawableForDensity(R.drawable.powered_by_horizontech, DisplayMetrics.DENSITY_MEDIUM)) + "</img>\n" +

                                (pageNumber != 0 ? "[C] (" + pageNumber + ")" : "")

                        , 5f);
            } catch (EscPosConnectionException e) {
                SelectedDevice.setIsDeviceSelected(false);
                e.printStackTrace();
                new AlertDialog.Builder(requireActivity(), R.style.DialogTheme)
                        .setTitle("Broken connection")
                        .setMessage(e.getMessage())
                        .show();
            } catch (EscPosParserException e) {
                SelectedDevice.setIsDeviceSelected(false);
                e.printStackTrace();
                new AlertDialog.Builder(requireActivity())
                        .setTitle("Invalid formatted text")
                        .setMessage(e.getMessage())
                        .show();
            } catch (EscPosEncodingException e) {
                SelectedDevice.setIsDeviceSelected(false);
                e.printStackTrace();
                new AlertDialog.Builder(requireActivity())
                        .setTitle("Bad selected encoding")
                        .setMessage(e.getMessage())
                        .show();
            } catch (EscPosBarcodeException e) {
                SelectedDevice.setIsDeviceSelected(false);
                e.printStackTrace();
                new AlertDialog.Builder(requireActivity())
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
        myInterface.retailerBuyVouchers(User.getUserSessionToken(), APP_VERSION, voucher, null, "true")
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
                            user.userBalance(requireActivity(), User.getUserSessionToken());
                            String pinNo = "";
                            try {
                                pinNo = cryptLib.decryptCipherTextWithRandomIV(lists.get(0).getVoucher().get(0).getVoucherNumber(), "123456789");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            String ussdCode = "*705*" + pinNo + "*" + phone + Uri.encode("#");
                            startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + ussdCode)));
                        } else {
                            Toast.makeText(requireActivity(), requireContext().getResources().getString(R.string.no_vouchers), Toast.LENGTH_SHORT).show();
                            switch (voucherPurchaseResponse.code()) {
                                case 400:
                                    Gson gson = new GsonBuilder().create();
                                    ErrorResponse mError;
                                    try {
                                        mError = gson.fromJson(voucherPurchaseResponse.errorBody().string(), ErrorResponse.class);
                                        FancyToast.makeText(requireActivity(), mError.getErrors().get(0).getMsg(), FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                                    } catch (IOException e) {
                                        // handle failure to read error
                                    }
                                    break;
                                case 500:
                                    FancyToast.makeText(requireActivity(), requireContext().getResources().getString(R.string.server_error_long), FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                                    break;
                                default:
                                    FancyToast.makeText(requireActivity(), requireContext().getResources().getString(R.string.server_error_long), FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
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
                        getUserBalance(requireActivity());
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

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireActivity(), R.style.DialogTheme);
            alertDialog.setTitle(requireContext().getResources().getString(R.string.select_bluetooth_device));

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
            voucherBluetoothSelector.setText(String.format(requireContext().getResources().getString(R.string.connected_to_printer), items[i1]));
            voucherBluetoothSelector.setText(String.format(requireContext().getResources().getString(R.string.connected_to_printer_capital), items[i1]));
            binding.vouchersPrinterStatus.setText(String.format(requireContext().getResources().getString(R.string.connected_to_printer_capital), items[i1]));
            binding.vouchersPrinterStatus.setTextColor(Color.GREEN);
            voucherBluetoothSelector.setBackgroundColor(Color.parseColor("#4CAF50"));
            binding.vouchersBluetoothButton.setColorFilter(Color.parseColor("#4CAF50"));
            voucherBluetoothSelector.setTextColor(Color.parseColor("#ffffff"));
            bluetoothPrinterDisconnector.setVisibility(View.VISIBLE);
            voucherPrint.setEnabled(true);
            voucherPrint.setBackgroundResource(R.drawable.btn_rounded);
            requireActivity().startService(new Intent(requireActivity(), BluetoothBroadcastReceiver.class));
        } catch (EscPosConnectionException e) {
            e.printStackTrace();
            Toast.makeText(requireActivity(), requireContext().getResources().getString(R.string.unable_to_connect_to_printer), Toast.LENGTH_LONG).show();
            SelectedDevice.setIsDeviceSelected(false);
        } catch (Exception e) {
            Toast.makeText(requireActivity(), requireContext().getResources().getString(R.string.unable_to_connect_to_printer), Toast.LENGTH_LONG).show();
            SelectedDevice.setSelectedDevice(null);
            SelectedDevice.setIsDeviceSelected(false);
        }
    }
    // endregion

    //    @Override
    //    protected void onStop() {
    //        Tovuti.from(requireActivity()).stop();
    //        super.onStop();
    //    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Tovuti.from(requireActivity()).stop();
//        binding = null;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//    }

    private final BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch (action) {
                case BluetoothDevice.ACTION_ACL_CONNECTED:

                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    if (isBluetooth) {

                        binding.vouchersPrinterStatus.setText(requireContext().getResources().getString(R.string.bluetooth_printer_disconnected));
                        binding.vouchersPrinterStatus.setTextColor(Color.RED);
                        voucherBluetoothSelector.setText(requireContext().getResources().getString(R.string.select_printer_upper));
                        voucherBluetoothSelector.setBackgroundColor(Color.parseColor("#ECECEC"));
                        binding.vouchersBluetoothButton.setColorFilter(Color.parseColor("#D11616"));
                        voucherBluetoothSelector.setTextColor(Color.parseColor("#214A6A"));
                        voucherBluetoothSelector.setEnabled(true);
                        bluetoothPrinterDisconnector.setVisibility(View.GONE);
                        voucherPrint.setEnabled(false);
                        voucherPrint.setBackgroundResource(R.drawable.btn_rounded_grayed);
                    }
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
                        if (isBluetooth) {
                            binding.vouchersPrinterStatus.setText(requireContext().getResources().getString(R.string.bluetooth_off));
                            binding.vouchersPrinterStatus.setTextColor(Color.RED);
                            voucherBluetoothSelector.setText(requireContext().getResources().getString(R.string.turn_on_bluetooth));
                            voucherBluetoothSelector.setBackgroundColor(Color.parseColor("#D11616"));
                            binding.vouchersBluetoothButton.setColorFilter(Color.parseColor("#D11616"));
                            voucherBluetoothSelector.setTextColor(Color.parseColor("#FFFFFF"));
                            voucherBluetoothSelector.setEnabled(false);
                            bluetoothPrinterDisconnector.setVisibility(View.GONE);
                            voucherPrint.setEnabled(false);
                            voucherPrint.setBackgroundResource(R.drawable.btn_rounded_grayed);
                        }
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:

                        break;
                    case BluetoothAdapter.STATE_ON:
                        if (isBluetooth) {

                            binding.vouchersPrinterStatus.setText(requireContext().getResources().getString(R.string.bluetooth_printer_disconnected));
                            binding.vouchersPrinterStatus.setTextColor(Color.RED);
                            voucherBluetoothSelector.setText(requireContext().getResources().getString(R.string.select_printer_upper));
                            voucherBluetoothSelector.setBackgroundColor(Color.parseColor("#ECECEC"));
                            binding.vouchersBluetoothButton.setColorFilter(Color.parseColor("#D11616"));
                            voucherBluetoothSelector.setTextColor(Color.parseColor("#214A6A"));
                            voucherBluetoothSelector.setEnabled(true);
                            bluetoothPrinterDisconnector.setVisibility(View.GONE);
                            voucherPrint.setEnabled(false);
                            voucherPrint.setBackgroundResource(R.drawable.btn_rounded_grayed);
                        }
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
        getUserBalance(requireActivity());
//        Toast.makeText(requireActivity(),"Registered Broadcast Listener",Toast.LENGTH_SHORT).show();
        myReceiver = new MyReceiver();
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(myReceiver,
                new IntentFilter("REFRESH_CARDS_PAGE"));
//        binding.vouchersBottomNavigation.setSelected(0, false);
//        Toast.makeText(getContext(), "Voucher Fragment Resumbed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart() {
        super.onStart();
//        Toast.makeText(getContext(), "Voucher Fragment Started", Toast.LENGTH_LONG).show();
    }
    // 1.1:region Init UI
//    private void initUI(View view) {
//
//        userStatementRecyclerview = view.findViewById(R.id.user_statement_screen_recyclerview);
//        activityUserStatementNewtonCradleLoading = view.findViewById(R.id.activity_user_statement_newton_cradle_loading2);
//
//        initRecyclerView();
//        getTransactionHistory();
//    }

    //end region
    //1.2 region Init RecyclerView
//    private void initRecyclerView() {
//
//        statementAdapter = new StatementAdapter(requireActivity(), statementList);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity());
//        userStatementRecyclerview.setLayoutManager(linearLayoutManager);
//        userStatementRecyclerview.setHasFixedSize(false);
//        userStatementRecyclerview.setAdapter(statementAdapter);
//    }

    //endregion
    //region get Transaction History
//    private void getTransactionHistory() {
//        activityUserStatementNewtonCradleLoading.start();
//
//        APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
//        myInterface.retailerGetSentFundTransaction(User.getUserSessionToken(), APP_VERSION)
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<ArrayList<FundResponse>>>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//
//            @Override
//            public void onNext(Response<ArrayList<FundResponse>> responseStatements) {
//
//
//                if (responseStatements.code() == 200) {
//                    activityUserStatementNewtonCradleLoading.stop();
//                    System.out.println("********************************************************");
//                    statementAdapter.add(responseStatements.body());
//                    System.out.println("********************************************************");
//
//
//                } else if (responseStatements.code() == 400) {
//                    activityUserStatementNewtonCradleLoading.stop();
//
//                    Toast.makeText(requireActivity(), "You Don't Have Any Transaction", Toast.LENGTH_SHORT).show();
//                } else {
//                    activityUserStatementNewtonCradleLoading.stop();
//
//                    Toast.makeText(requireActivity(), requireContext().getResources().getString(R.string.someting_went_worong), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                System.out.println("has" + e);
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        });
//    }
    //endregion
}
