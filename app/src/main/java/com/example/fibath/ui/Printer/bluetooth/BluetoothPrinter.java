package com.example.fibath.ui.Printer.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;
import com.dantsu.escposprinter.exceptions.EscPosEncodingException;
import com.dantsu.escposprinter.exceptions.EscPosParserException;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;
import com.example.fibath.R;import com.example.fibath.classes.api.APIInterface;
import com.example.fibath.classes.encryption.CryptLib;
import com.example.fibath.classes.model.ErrorResponse;
import com.example.fibath.classes.model.VoucherPurchaseResponse;
import com.example.fibath.classes.networking.communication.ServerCommunicator;
import com.example.fibath.classes.user.User;
import com.example.fibath.classes.voucher.VoucherBulkPrint;
import com.example.fibath.classes.voucher.VoucherBulkPrintStatic;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import static com.example.fibath.classes.api.APIInterface.APP_VERSION;
import static com.example.fibath.classes.api.APIInterface.BASE_URL;

public class BluetoothPrinter extends Activity {
    EditText message;
    Button btnBill, connectBtn;
    LottieAnimationView printer_layout_lottie_animation2;
    byte FONT_TYPE;
    private static BluetoothSocket btsocket;
    private static OutputStream outputStream;
    // region Electronic Voucher Variables
    private int cardFive, cardTen, cardFifteen;
    // endregion

    //region activity  permission
    public static final int PERMISSION_BLUETOOTH = 1;
    boolean isSmallPrinter = true;
//    boolean isDeviceSelected = false;


    // endregion

    private String isReprint = "";
    private String isBulk = "";
    String offline = "";
    CryptLib cryptLib = new CryptLib();
    String FaceValue;

    public List<com.example.fibath.classes.model.Voucher> bulkPinList = new ArrayList<>();
    private String amount, serialNumber, pinNumber, expiryDate;
    int reDownLoad;

    public BluetoothPrinter() throws NoSuchPaddingException, NoSuchAlgorithmException {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        btnBill = findViewById(R.id.btnBill);
        connectBtn = findViewById(R.id.connect_btn);
        printer_layout_lottie_animation2 = findViewById(R.id.printer_layout_lottie_animation2);

        FaceValue = getIntent().getStringExtra("mrp");
        offline = getIntent().getStringExtra("offline");
        isReprint = getIntent().getStringExtra("isReprint");
        isBulk = getIntent().getStringExtra("isBulk");
        getSelectedVouchers();
        Intent intent = getIntent();
        HashMap<String, Integer> bulkMap = (HashMap<String, Integer>) intent.getSerializableExtra("bulk");
        SharedPreferences sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        if (sharedPreferences.getString("LargePrinterMode", "").equals("1")) {
            isSmallPrinter = false;
        }


        System.out.println("@@@@@@bulk map in bluetooth" + bulkMap);
        System.out.println("offline" + offline);
        System.out.println("isReprint" + isReprint);
        System.out.println("isBulk" + isBulk);

        // region Initialize Bluetooth State
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {

        } else if (!mBluetoothAdapter.isEnabled()) {
            // Bluetooth is not enabled :)
            connectBtn.setText("TURN ON BLUETOOTH");
            connectBtn.setEnabled(false);
            connectBtn.setBackgroundResource(R.drawable.btn_rounded_grayed);
        } else {
            // Bluetooth is enabled
        }
        // endregion

        if (SelectedDevice.isDeviceSelected()) {
            connectBtn.setText("Connected to printer " + SelectedDevice.getSelectedDevice().getDevice().getName());
            connectBtn.setEnabled(false);
            connectBtn.setBackgroundResource(R.drawable.btn_rounded_grayed);
        } else {
            connectBtn.setOnClickListener(e -> {
                browseBluetoothDevice();

            });
        }

        btnBill.setOnClickListener(v -> {
//                selectedDevice=BluetoothPrintersConnections.selectFirstPaired();
            if (!SelectedDevice.isDeviceSelected()) {
                SelectedDevice.setSelectedDevice(BluetoothPrintersConnections.selectFirstPaired());
                SelectedDevice.setIsDeviceSelected(true);
            }
//                System.out.println("selected device is" + selectedDevice.isConnected());
            if (SelectedDevice.getSelectedDevice() != null) {
                System.out.println("%%%%%%%%");
                btnBill.setEnabled(false);
                if (offline.equals("true") && isReprint.equals("false")) {
                    for (int i = 0; i < VoucherBulkPrint.getOfflineBulkPrint().size(); i++) {
                        try {
                            printBill(VoucherBulkPrint.getOfflineBulkPrint().size() - i, VoucherBulkPrint.getOfflineBulkPrint().get(i).getVoucherAmount(), VoucherBulkPrint.getOfflineBulkPrint().get(i).getVoucherNumber(), VoucherBulkPrint.getOfflineBulkPrint().get(i).getVoucherSerialNumber(), VoucherBulkPrint.getOfflineBulkPrint().get(i).getVoucherExpiryDate(), VoucherBulkPrint.getOfflineBulkPrint().get(i).getVoucherReDownload(), "-------------");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    VoucherBulkPrint.getOfflineBulkPrint().clear();

                } else if (offline.equals("false") && isReprint.equals("false")) {

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

                        new AlertDialog.Builder(BluetoothPrinter.this, R.style.DialogTheme)
                                .setTitle("Broken connection")


                                .setMessage(e.getMessage())
                                .show();
                    }

                } else if (offline.equals("true") && isReprint.equals("true")) {

                    if (isBulk.equals("true")) { // isbulk true
                        System.out.println("offline");
                        for (int i = 0; i < VoucherBulkPrintStatic.getOfflineBulkPrint().size(); i++) {

                            try {
                                printBill(0, VoucherBulkPrintStatic.getOfflineBulkPrint().get(i).getVoucherAmount(), VoucherBulkPrintStatic.getOfflineBulkPrint().get(i).getVoucherNumber(), VoucherBulkPrintStatic.getOfflineBulkPrint().get(i).getVoucherSerialNumber(), VoucherBulkPrintStatic.getOfflineBulkPrint().get(i).getVoucherExpiryDate(), VoucherBulkPrintStatic.getOfflineBulkPrint().get(i).getVoucherReDownload(), "-------------");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        VoucherBulkPrintStatic.getOfflineBulkPrint().clear();
                    } else {
                        try {
                            printBill(0, amount, pinNumber, serialNumber, expiryDate, reDownLoad, "-------------");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else if (offline.equals("false") && isReprint.equals("true")) {
                    if (isBulk.equals("true")) { // is bulk true
                        System.out.println("her is me");
                        for (int ii = 0; ii < VoucherBulkPrintStatic.getBulkPrint().size(); ii++) {
                            try {
                                System.out.println("Id of Element is" + VoucherBulkPrintStatic.getBulkPrint().get(ii).getVoucherRowNumber());
                                printBill(VoucherBulkPrintStatic.getBulkPrint().get(ii).getVoucherRowNumber(), VoucherBulkPrintStatic.getBulkPrint().get(ii).getVoucherAmount(), VoucherBulkPrintStatic.getBulkPrint().get(ii).getVoucherNumber(), VoucherBulkPrintStatic.getBulkPrint().get(ii).getVoucherSerialNumber(), VoucherBulkPrintStatic.getBulkPrint().get(ii).getVoucherExpiryDate(), 0, VoucherBulkPrintStatic.getBulkPrint().get(ii).getBulkId());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        VoucherBulkPrintStatic.getBulkPrint().clear();
                    } else {
                        try {
                            printBill(0, amount, pinNumber, serialNumber, expiryDate, 0, "----------");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            } else {
                new AlertDialog.Builder(BluetoothPrinter.this, R.style.DialogTheme)
                        .setTitle("Broken connection")


                        .setMessage("Please Connect Your Device")
                        .show();
            }

        });


    }

    private void getSelectedVouchers() {
        Intent intent = getIntent();
        amount = intent.getStringExtra("AMOUNT");
        serialNumber = intent.getStringExtra("SERIAL_NUMBER");
        pinNumber = intent.getStringExtra("PIN_NUMBER");
        expiryDate = intent.getStringExtra("EXPIRY_DATE");
        reDownLoad = intent.getIntExtra("TRANSACTION_REDOWNLOAD", 0);
        System.out.println("amount is" + amount);
        System.out.println("pinNumber is" + pinNumber);
        System.out.println("serialNumber is" + serialNumber);
        System.out.println("expiryDate is" + expiryDate);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Intent homeIntent = new Intent(getApplicationContext(), Home.class);
//        startActivity(homeIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void printBill(int pageNumber, String amount, String pinNo, String serial, String expiryDate, int reDownload, String bulkId) throws Exception {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, BluetoothPrinter.PERMISSION_BLUETOOTH);
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
                pinNo = pinOne + "-" + pinTwo + "-" + pinThree+ "-" + pinFour;
                System.out.println("inside print");
//                selectedDevice = BluetoothPrintersConnections.selectFirstPaired();

                EscPosPrinter printer = new EscPosPrinter(SelectedDevice.getSelectedDevice(), 203, isSmallPrinter ? 48f : 72f, isSmallPrinter ? 32 : 48);
                System.out.println("selected redownload is" + reDownload);
                printer.printFormattedText(
                        "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, this.getApplicationContext().getResources().getDrawableForDensity(R.drawable.horizon_ethiotelecom_logo, DisplayMetrics.DENSITY_MEDIUM)) + "</img>\n" +
                                "[C]<font size='wide'>" + amount.replace(".00", "") + " Birr</font>\n" +
                                "[C]--------------------------------\n" +
                                "[C]<b><font size='tall'>" + pinNo + "</font></b>\n" +
                                "[C]--------------------------------\n" +

                                (reDownload > 0 ? "[C]RE DOWNLOADED\n" : "") +

                                "[C]ToRecharge*705*" + originalPin + "#\n" +
                                "[C] SN: " + serial + (isReprint.equals("true") ? "*CP*" : "") + "\n" +
                                "[C]<b>Agent: " + User.getUserSessionUsername() + "</b>\n" +
//                                "[C]  Powered By HorizonTech\n" +
                                "[C]PD:" + date + " ED:" + expiryDate + "\n" +
//                                (pageNumber != 0 ? "[C]" + bulkId + "-(" + pageNumber + ")" : "")

//                                "[C]--------------------------------\n"+
                                "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, this.getApplicationContext().getResources().getDrawableForDensity(R.drawable.warning_message_two, DisplayMetrics.DENSITY_MEDIUM)) + "</img>\n" +
//                                "[C]--------------------------------\n"+
//                                "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, this.requireActivity().getResources().getDrawableForDensity(R.drawable.powered_by_horizontech, DisplayMetrics.DENSITY_MEDIUM)) + "</img>\n" +

                                (pageNumber != 0 ? "[C] (" + pageNumber + ")" : "")


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


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


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
                            printBill(bulkPinList.size() - x, bulkPinList.get(x).getVoucherAmount(), bulkPinList.get(x).getVoucherNumber(), bulkPinList.get(x).getVoucherSerialNumber(), bulkPinList.get(x).getVoucherExpiryDate(), 0, bulkPinList.get(x).getBulkId());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }


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
                System.out.println("has" + e);
            }

            @Override
            public void onComplete() {

            }
        });
    }



    public void browseBluetoothDevice() {
        final BluetoothConnection[] bluetoothDevicesList = (new BluetoothPrintersConnections()).getList();

        if (bluetoothDevicesList != null) {
            final String[] items = new String[bluetoothDevicesList.length + 1];
            items[0] = "Default printer";
            int i = 0;
            for (BluetoothConnection device : bluetoothDevicesList) {
                items[++i] = device.getDevice().getName();
            }

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(BluetoothPrinter.this, R.style.DialogTheme);
            alertDialog.setTitle("Select Bluetooth Device");

            alertDialog.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    int index = i - 1;
                    if (index == -1) {
                        SelectedDevice.setSelectedDevice(BluetoothPrintersConnections.selectFirstPaired());
                        SelectedDevice.setIsDeviceSelected(true);
                    } else {
                        SelectedDevice.setSelectedDevice(bluetoothDevicesList[index]);
                        SelectedDevice.setIsDeviceSelected(true);
                    }
                    try {
                        SelectedDevice.getSelectedDevice().connect();
                    } catch (EscPosConnectionException e) {
                        e.printStackTrace();
                        SelectedDevice.setIsDeviceSelected(false);
                    }
//                    Button button = (Button) findViewById(R.id.button_bluetooth_browse);
                    connectBtn.setText(items[i]);
//                    isDeviceSelected = true;
                }
            });

            AlertDialog alert = alertDialog.create();
            alert.setCanceledOnTouchOutside(false);
            alert.show();

        }
    }

}