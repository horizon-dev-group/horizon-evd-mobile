package com.example.fibath.ui.Printer.telpo.v330;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.airbnb.lottie.LottieAnimationView;
import com.example.fibath.R;import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.telpo.tps550.api.printer.UsbThermalPrinter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

public class Telpo330PrinterStatic extends Activity {
    // region Main Handler
    MyHandler handler;
    // endregion

    // region UI Elements
    Button printVoucher;
    private ProgressDialog progressDialog;
    ProgressDialog dialog;
    private LottieAnimationView animationView;
    // endregion

    // region POS Machine SDK Variables
    UsbThermalPrinter mUsbThermalPrinter = new UsbThermalPrinter(Telpo330PrinterStatic.this);
    // endregion

    // region POS Machine Variables
    private static String printVersion;
    private final int NOPAPER = 3;
    private final int LOWBATTERY = 4;
    private final int PRINTVERSION = 5;
    private final int PRINTBARCODE = 6;
    private final int PRINTQRCODE = 7;
    private final int PRINTPAPERWALK = 8;
    private final int PRINTCONTENT = 9;
    private final int CANCELPROMPT = 10;
    private final int PRINTERR = 11;
    private final int OVERHEAT = 12;
    private final int MAKER = 13;
    private final int PRINTPICTURE = 14;
    private final int NOBLACKBLOCK = 15;
    private final static int MAX_LEFT_DISTANCE = 255;

    private Boolean nopaper = false;
    private boolean LowBattery = false;
    private String result;
    private String qrcodeStr;
    private String picturePath = Environment.getExternalStorageDirectory().getAbsolutePath();

    // endregion

    // region Electronic Voucher Variables
    private String amount, serialNumber, pinNumber, expiryDate;
    // endregion

    // region Printer Print Preferences
    int leftDistance = 0; // Left Distance
    int lineDistance = 0; // Line Distance
    int wordFont = 2; // Word Font
    int printGray = 1; // Print Gray
    int charSpace = 0; // Print Gray

    // endregion

    // region Content To Be Printed
    String logo = "";
    String printContent = "";
    // endregion

    // region Main Handler (Event Receiver)
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NOPAPER:
                    noPaperDlg();
                    break;
                case LOWBATTERY:
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(Telpo330PrinterStatic.this);
                    alertDialog.setTitle("Result");
                    alertDialog.setMessage("The battery is low, please connect the charger!");
                    alertDialog.setPositiveButton("Ok", (dialogInterface, i) -> {
                    });
                    alertDialog.show();
                    break;
                case NOBLACKBLOCK:
                    Toast.makeText(Telpo330PrinterStatic.this, "No black mark found", Toast.LENGTH_SHORT).show();
                    break;
                case PRINTVERSION:
                    dialog.dismiss();
                    if (msg.obj.equals("1")) {
                        // textPrintVersion.setText(printVersion);
                        System.out.println(printVersion);
                    } else {
                        Toast.makeText(Telpo330PrinterStatic.this, "Operation Failed!", Toast.LENGTH_LONG).show();
                    }
                    break;
                case PRINTBARCODE:
                    // new barcodePrintThread().start();
                    break;
                case PRINTQRCODE:
                    // new qrcodePrintThread().start();
                    break;
                case PRINTPAPERWALK:
                    // new paperWalkPrintThread().start();
                    break;
                case PRINTCONTENT:
                    new contentPrintThread().start();
                    break;
                case MAKER:
                    // new MakerThread().start();
                    break;
                case PRINTPICTURE:
                    // new printPicture().start();
                    break;
                case CANCELPROMPT:
                    if (progressDialog != null && !Telpo330PrinterStatic.this.isFinishing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                        runOnUiThread(() -> {
                            // Toast.makeText(Telpo330Printer.this, "Logo not found!", Toast.LENGTH_LONG).show();
                            animationView.setAnimation("task_complited_file.json");
                        });
                    }
                    break;
                case OVERHEAT:
                    AlertDialog.Builder overHeatDialog = new AlertDialog.Builder(Telpo330PrinterStatic.this);
                    overHeatDialog.setTitle("Result");
                    overHeatDialog.setMessage("Printer is overheating!");
                    overHeatDialog.setPositiveButton("Ok", (dialogInterface, i) -> {
                    });
                    overHeatDialog.show();
                    break;
                default:
                    Toast.makeText(Telpo330PrinterStatic.this, "Print Error!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }
    // endregion

    // region POS Machine Print Methods


    private class contentPrintThread extends Thread {
        private void printVouchers(String amount, String pinNo, String serial, String expDate) throws Exception {
            mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_LEFT);
            mUsbThermalPrinter.setLeftIndent(leftDistance);
            mUsbThermalPrinter.setLineSpace(lineDistance);
            mUsbThermalPrinter.setCharSpace(charSpace);
            if (wordFont == 4) {
                mUsbThermalPrinter.setFontSize(2);
                mUsbThermalPrinter.enlargeFontSize(2, 2);
            } else if (wordFont == 3) {
                mUsbThermalPrinter.setFontSize(1);
                mUsbThermalPrinter.enlargeFontSize(2, 2);
            } else if (wordFont == 2) {
                mUsbThermalPrinter.setFontSize(2);
            } else if (wordFont == 1) {
                mUsbThermalPrinter.setFontSize(1);
            }
            mUsbThermalPrinter.setGray(printGray);

            // region Print Logo
            mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE);

            File file = new File(picturePath + "/horizon_ethiotelecom_logo.bmp");
            if (file.exists()) {
                mUsbThermalPrinter.printLogo(BitmapFactory.decodeFile(picturePath + "/horizon_ethiotelecom_logo.bmp"), false);
                // mUsbThermalPrinter.walkPaper(10);

            } else {
                runOnUiThread(() -> Toast.makeText(Telpo330PrinterStatic.this, "Logo not found!", Toast.LENGTH_LONG).show());
            }
            // endregion

            // region Print Content
            mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE);
            mUsbThermalPrinter.addString("---------------------------");
            // print main title
            mUsbThermalPrinter.addString("Electronic Voucher");
            mUsbThermalPrinter.printString();
            // print amount title
            mUsbThermalPrinter.addString("AMOUNT: " + amount + " Birr");
            mUsbThermalPrinter.printString();
            // print pin title
            mUsbThermalPrinter.setFontSize(1);
            mUsbThermalPrinter.enlargeFontSize(1, 1);
            mUsbThermalPrinter.addString("PIN");
            mUsbThermalPrinter.printString();
            // print large pin
            mUsbThermalPrinter.setFontSize(1);
            mUsbThermalPrinter.enlargeFontSize(2, 2);
            mUsbThermalPrinter.addString(String.valueOf(pinNo));
            mUsbThermalPrinter.printString();
            // print qr code
            qrcodeStr = String.valueOf(pinNo);
            Bitmap bitmap = CreateCode(qrcodeStr, BarcodeFormat.QR_CODE, 256, 256);
            if (bitmap != null) {
                mUsbThermalPrinter.printLogo(bitmap, true);
            }
            // print separator
            mUsbThermalPrinter.setFontSize(2);
            mUsbThermalPrinter.enlargeFontSize(1, 1);
            mUsbThermalPrinter.addString("---------------------------");
            mUsbThermalPrinter.printString();

            mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_LEFT);
            // print date
            Date currentTime = Calendar.getInstance().getTime();
            mUsbThermalPrinter.setFontSize(1);
            mUsbThermalPrinter.enlargeFontSize(1, 1);
            mUsbThermalPrinter.addString("Serial: " + serial);
            mUsbThermalPrinter.addString("Prt Date: " + currentTime.toString());
            mUsbThermalPrinter.addString("Exp Date: " + expDate);

            mUsbThermalPrinter.printString();

            mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE);
            // print Powered By
            mUsbThermalPrinter.addString("Powered By HorizonTech™");
            mUsbThermalPrinter.printString();
            mUsbThermalPrinter.walkPaper(1);

            // print Thanks
            mUsbThermalPrinter.addString("*** Thank You For Buying ***");
            mUsbThermalPrinter.printString();
            mUsbThermalPrinter.walkPaper(10);
        }

        @Override
        public void run() {
            super.run();
            switch (getPrintTypes()) {
                case "busTicket":
                    try {
                        mUsbThermalPrinter.start(0);
                        mUsbThermalPrinter.reset();
                        mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_LEFT);
                        mUsbThermalPrinter.setLeftIndent(leftDistance);
                        mUsbThermalPrinter.setLineSpace(lineDistance);
                        mUsbThermalPrinter.setCharSpace(charSpace);
                        if (wordFont == 4) {
                            mUsbThermalPrinter.setFontSize(2);
                            mUsbThermalPrinter.enlargeFontSize(2, 2);
                        } else if (wordFont == 3) {
                            mUsbThermalPrinter.setFontSize(1);
                            mUsbThermalPrinter.enlargeFontSize(2, 2);
                        } else if (wordFont == 2) {
                            mUsbThermalPrinter.setFontSize(2);
                        } else if (wordFont == 1) {
                            mUsbThermalPrinter.setFontSize(1);
                        }
                        mUsbThermalPrinter.setGray(printGray);

                        // region Print Logo
                        mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE);

                        File file = new File(picturePath + "/horizon_anbessa_logo.bmp");
                        if (file.exists()) {
                            mUsbThermalPrinter.printLogo(BitmapFactory.decodeFile(picturePath + "/horizon_anbessa_logo.bmp"), false);
                            // mUsbThermalPrinter.walkPaper(10);

                        } else {
                            runOnUiThread(() -> Toast.makeText(Telpo330PrinterStatic.this, "Logo not found!", Toast.LENGTH_LONG).show());
                        }
                        // endregion

                        // region Print Content
                        mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE);
                        // mUsbThermalPrinter.addString(printContent);
                        mUsbThermalPrinter.addString("---------");
                        // print Transport Provider
                        mUsbThermalPrinter.addString("Selam Bus");
                        mUsbThermalPrinter.printString();
                        // mUsbThermalPrinter.walkPaper(1);
                        // print large Full Name
                        mUsbThermalPrinter.setFontSize(1);
                        mUsbThermalPrinter.enlargeFontSize(2, 2);
                        mUsbThermalPrinter.addString("Abebe Kebede");
                        mUsbThermalPrinter.printString();
                        mUsbThermalPrinter.walkPaper(1);

                        // print large From
                        mUsbThermalPrinter.setFontSize(1);
                        mUsbThermalPrinter.enlargeFontSize(2, 2);
                        mUsbThermalPrinter.addString("From---------Addis Ababa");
                        mUsbThermalPrinter.printString();
                        mUsbThermalPrinter.walkPaper(1);

                        // print large To
                        mUsbThermalPrinter.setFontSize(1);
                        mUsbThermalPrinter.enlargeFontSize(2, 2);
                        mUsbThermalPrinter.addString("To---------Mekele");
                        mUsbThermalPrinter.printString();
                        mUsbThermalPrinter.walkPaper(1);

                        // print large Date
                        mUsbThermalPrinter.setFontSize(1);
                        mUsbThermalPrinter.enlargeFontSize(2, 2);
                        mUsbThermalPrinter.addString("Date---------February 23");
                        mUsbThermalPrinter.printString();
                        mUsbThermalPrinter.walkPaper(1);
                        // print large Time
                        mUsbThermalPrinter.setFontSize(1);
                        mUsbThermalPrinter.enlargeFontSize(2, 2);
                        mUsbThermalPrinter.addString("Time---------11:30");
                        mUsbThermalPrinter.printString();
                        mUsbThermalPrinter.walkPaper(1);
                        // print large Bus Number
                        mUsbThermalPrinter.setFontSize(1);
                        mUsbThermalPrinter.enlargeFontSize(2, 2);
                        mUsbThermalPrinter.addString("Bus Number---------A1234");
                        mUsbThermalPrinter.printString();
                        mUsbThermalPrinter.walkPaper(1);
                        // print large Seat Number
                        mUsbThermalPrinter.setFontSize(1);
                        mUsbThermalPrinter.enlargeFontSize(2, 2);
                        mUsbThermalPrinter.addString("Seat Number---------32");
                        mUsbThermalPrinter.printString();
                        mUsbThermalPrinter.walkPaper(1);

                        // print large Price
                        mUsbThermalPrinter.setFontSize(1);
                        mUsbThermalPrinter.enlargeFontSize(2, 2);
                        mUsbThermalPrinter.addString("Price---------1000 Birr");
                        mUsbThermalPrinter.printString();
                        mUsbThermalPrinter.walkPaper(1);

                        mUsbThermalPrinter.walkPaper(10);

                    } catch (Exception e) {
                        e.printStackTrace();
                        result = e.toString();
                        if (result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
                            nopaper = true;
                        } else if (result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
                            handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
                        } else {
                            handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
                        }
                    } finally {
                        handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
                        if (nopaper) {
                            handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
                            nopaper = false;
                            return;
                        }
                        mUsbThermalPrinter.stop();
                    }
                    break;
                case "trainTicket":
                    try {
                        mUsbThermalPrinter.start(0);
                        mUsbThermalPrinter.reset();
                        mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_LEFT);
                        mUsbThermalPrinter.setLeftIndent(leftDistance);
                        mUsbThermalPrinter.setLineSpace(lineDistance);
                        mUsbThermalPrinter.setCharSpace(charSpace);
                        if (wordFont == 4) {
                            mUsbThermalPrinter.setFontSize(2);
                            mUsbThermalPrinter.enlargeFontSize(2, 2);
                        } else if (wordFont == 3) {
                            mUsbThermalPrinter.setFontSize(1);
                            mUsbThermalPrinter.enlargeFontSize(2, 2);
                        } else if (wordFont == 2) {
                            mUsbThermalPrinter.setFontSize(2);
                        } else if (wordFont == 1) {
                            mUsbThermalPrinter.setFontSize(1);
                        }
                        mUsbThermalPrinter.setGray(printGray);

                        // region Print Logo
                        mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE);

                        File file = new File(picturePath + "/horizon_ethiorailway_logo.bmp");
                        if (file.exists()) {
                            mUsbThermalPrinter.printLogo(BitmapFactory.decodeFile(picturePath + "/horizon_ethiorailway_logo.bmp"), false);
                            // mUsbThermalPrinter.walkPaper(10);

                        } else {
                            runOnUiThread(() -> Toast.makeText(Telpo330PrinterStatic.this, "Logo not found!", Toast.LENGTH_LONG).show());
                        }
                        // endregion

                        // region Print Content
                        mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE);
                        // mUsbThermalPrinter.addString(printContent);
                        mUsbThermalPrinter.addString("---------------------------");
                        // print Train Type
                        mUsbThermalPrinter.addString("Light Train");
                        mUsbThermalPrinter.printString();
                        // mUsbThermalPrinter.walkPaper(1);
                        // print large Full Name
                        mUsbThermalPrinter.setFontSize(1);
                        mUsbThermalPrinter.enlargeFontSize(2, 2);
                        mUsbThermalPrinter.addString("Abebe Kebede");
                        mUsbThermalPrinter.printString();
                        mUsbThermalPrinter.walkPaper(1);

                        // print large From
                        mUsbThermalPrinter.setFontSize(1);
                        mUsbThermalPrinter.enlargeFontSize(2, 2);
                        mUsbThermalPrinter.addString("From---------Addis Ababa");
                        mUsbThermalPrinter.printString();
                        mUsbThermalPrinter.walkPaper(1);

                        // print large To
                        mUsbThermalPrinter.setFontSize(1);
                        mUsbThermalPrinter.enlargeFontSize(2, 2);
                        mUsbThermalPrinter.addString("To---------Mekele");
                        mUsbThermalPrinter.printString();
                        mUsbThermalPrinter.walkPaper(1);

                        // print large Date
                        mUsbThermalPrinter.setFontSize(1);
                        mUsbThermalPrinter.enlargeFontSize(2, 2);
                        mUsbThermalPrinter.addString("Date---------February 23");
                        mUsbThermalPrinter.printString();
                        mUsbThermalPrinter.walkPaper(1);
                        // print large Time
                        mUsbThermalPrinter.setFontSize(1);
                        mUsbThermalPrinter.enlargeFontSize(2, 2);
                        mUsbThermalPrinter.addString("Time---------11:30");
                        mUsbThermalPrinter.printString();
                        mUsbThermalPrinter.walkPaper(1);
                        // print large Bus Number
                        mUsbThermalPrinter.setFontSize(1);
                        mUsbThermalPrinter.enlargeFontSize(2, 2);
                        mUsbThermalPrinter.addString("Train Number---------A1234");
                        mUsbThermalPrinter.printString();
                        mUsbThermalPrinter.walkPaper(1);
                        // print large Seat Number
                        mUsbThermalPrinter.setFontSize(1);
                        mUsbThermalPrinter.enlargeFontSize(2, 2);
                        mUsbThermalPrinter.addString("Seat Number---------32");
                        mUsbThermalPrinter.printString();
                        mUsbThermalPrinter.walkPaper(1);

                        // print large Price
                        mUsbThermalPrinter.setFontSize(1);
                        mUsbThermalPrinter.enlargeFontSize(2, 2);
                        mUsbThermalPrinter.addString("Price---------1000 Birr");
                        mUsbThermalPrinter.printString();
                        mUsbThermalPrinter.walkPaper(1);


                        mUsbThermalPrinter.walkPaper(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                        result = e.toString();
                        if (result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
                            nopaper = true;
                        } else if (result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
                            handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
                        } else {
                            handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
                        }
                    } finally {
                        handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
                        if (nopaper) {
                            handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
                            nopaper = false;
                            return;
                        }
                        mUsbThermalPrinter.stop();
                    }
                    break;
                case "voucher":
                    try {
                        mUsbThermalPrinter.start(0);
                        mUsbThermalPrinter.reset();

//                        String voucherInfo = getSinglePrintType();
//                        String[] singleVoucherInfos = voucherInfo.split("\\|");
//                        System.out.println("amount "+singleVoucherInfos[0]);
//                        System.out.println("pin "+singleVoucherInfos[1]);
//                        System.out.println("serial "+singleVoucherInfos[2]);
//                        System.out.println("expireddate "+singleVoucherInfos[3]);
//
//                        printVouchers(singleVoucherInfos[0], singleVoucherInfos[1], singleVoucherInfos[2], singleVoucherInfos[3]);

                        System.out.println("amount is" + amount);
                        System.out.println("pinNumber is" + pinNumber);
                        System.out.println("serialNumber is" + serialNumber);
                        System.out.println("expiryDate is" + expiryDate);
                        printVouchers(amount, pinNumber, serialNumber, expiryDate);


                        // endregion
                    } catch (Exception e) {
                        e.printStackTrace();
                        result = e.toString();
                        if (result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
                            nopaper = true;
                        } else if (result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
                            handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
                        } else {
                            handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
                        }
                    } finally {
                        handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
                        if (nopaper) {
                            handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
                            nopaper = false;
                            return;
                        }
                        mUsbThermalPrinter.stop();
                    }
                    break;
            }

        }
    }
    // endregion

    // region Create Barcode
    public Bitmap CreateCode(String str, BarcodeFormat type, int bmpWidth, int bmpHeight) throws WriterException {
        // Generate a two-dimensional matrix, specify the size when encoding, do not scale the image after it is generated, to prevent blurring from causing recognition failure
        BitMatrix matrix = new MultiFormatWriter().encode(str, type, bmpWidth, bmpHeight);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        // 2D matrix into 1D pixel array (always row)
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                } else {
                    pixels[y * width + x] = 0xffffffff;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // Generate a bitmap from a pixel array.
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }
    // endregion

    // region Dialog Shower
    private void noPaperDlg() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(Telpo330PrinterStatic.this);
        dlg.setTitle("No Paper!");
        dlg.setMessage("Print out of paper, please try again after loading paper");
        dlg.setCancelable(false);
        dlg.setPositiveButton("Ok", (dialogInterface, i) -> mUsbThermalPrinter.stop());
        dlg.show();
    }
    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.printer_layout);
        // printVoucher = findViewById(R.id.activity_main_print_voucher);
        // print content , logo, etc....
        getPrintTypes();
        getLogoTypes();
        getSelectedVouchers();

        System.out.println("*********************************************");
        System.out.println("Print type" + getPrintTypes());
        System.out.println("Logo Type: " + getLogoTypes());
        System.out.println("*********************************************");

        // region to be refactored
        animationView = findViewById(R.id.printer_layout_lottie_animation);
        // endregion

        initApp();
        loadPrintLogo();
        // initEventHandlers();
        printNewVoucher();

    }

    private void setPrintContent() {
        logo = "horizon_ethiotelecom_logo.png";
        printContent = "687686876876";
    }

    private void initApp() {
        handler = new MyHandler();
    }

    private void loadPrintLogo() {
        switch (getLogoTypes()) {
            case "horizon_anbessa_logo.png":
                File fileBus = new File(picturePath + "/horizon_anbessa_logo.bmp");
                if (!fileBus.exists()) {
                    InputStream inputStream = null;
                    FileOutputStream fos = null;
                    byte[] tmp = new byte[1024];
                    try {
                        inputStream = getApplicationContext().getAssets().open("horizon_anbessa_logo.png");
                        fos = new FileOutputStream(fileBus);
                        int length = 0;
                        while ((length = inputStream.read(tmp)) > 0) {
                            fos.write(tmp, 0, length);
                        }
                        fos.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            inputStream.close();
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case "horizon_ethiorailway_logo.png":
                File fileTrain = new File(picturePath + "/horizon_ethiorailway_logo.bmp");
                if (!fileTrain.exists()) {
                    InputStream inputStream = null;
                    FileOutputStream fos = null;
                    byte[] tmp = new byte[1024];
                    try {
                        inputStream = getApplicationContext().getAssets().open("horizon_ethiorailway_logo.png");
                        fos = new FileOutputStream(fileTrain);
                        int length = 0;
                        while ((length = inputStream.read(tmp)) > 0) {
                            fos.write(tmp, 0, length);
                        }
                        fos.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            inputStream.close();
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case "horizon_ethiotelecom_logo.png":
                File fileVocher = new File(picturePath + "/horizon_ethiotelecom_logo.bmp");
                if (!fileVocher.exists()) {
                    InputStream inputStream = null;
                    FileOutputStream fos = null;
                    byte[] tmp = new byte[1024];
                    try {
                        inputStream = getApplicationContext().getAssets().open("horizon_ethiotelecom_logo.png");
                        fos = new FileOutputStream(fileVocher);
                        int length = 0;
                        while ((length = inputStream.read(tmp)) > 0) {
                            fos.write(tmp, 0, length);
                        }
                        fos.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            inputStream.close();
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;

        }
        File file = new File(picturePath);
        if (!file.exists()) {
            InputStream inputStream = null;
            FileOutputStream fos = null;
            byte[] tmp = new byte[1024];
            try {
                inputStream = getApplicationContext().getAssets().open("horizon_ethiotelecom_logo.png");
                fos = new FileOutputStream(file);
                int length = 0;
                while ((length = inputStream.read(tmp)) > 0) {
                    fos.write(tmp, 0, length);
                }
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initEventHandlers() {
        printVoucher.setOnClickListener(view -> {

        });
    }

    private void printNewVoucher() {
        printContent = "Final Print Test!";
        leftDistance = 0; // Left Distance
        lineDistance = 0; // Line Distance
        wordFont = 2; // Word Font
        printGray = 1; // Print Gray


        if (leftDistance > MAX_LEFT_DISTANCE) {
            Toast.makeText(Telpo330PrinterStatic.this, "Out of left margin!", Toast.LENGTH_LONG).show();
            return;
        }
        if (lineDistance > 255) {
            Toast.makeText(Telpo330PrinterStatic.this, "Out of line!", Toast.LENGTH_LONG).show();
            return;
        }
        if (wordFont > 4 || wordFont < 1) {
            Toast.makeText(Telpo330PrinterStatic.this, "Out of font range!", Toast.LENGTH_LONG).show();
            return;
        }
        if (printGray < 0 || printGray > 7) {
            Toast.makeText(Telpo330PrinterStatic.this, "Out of concentration range!", Toast.LENGTH_LONG).show();
            return;
        }
        if (printContent == null || printContent.length() == 0) {
            Toast.makeText(Telpo330PrinterStatic.this, "Content is empty!", Toast.LENGTH_LONG).show();
            return;
        }
        int charSpace = 0;
        if ((charSpace < 0) || (charSpace > 255)) {
            Toast.makeText(Telpo330PrinterStatic.this, "Out of character spacing!", Toast.LENGTH_LONG).show();
            return;
        }
        if (LowBattery) {
            handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
        } else {
            if (!nopaper) {
                // progressDialog = ProgressDialog.show(Telpo330Printer.this, "Print", "Please wait while printing");
                handler.sendMessage(handler.obtainMessage(PRINTCONTENT, 1, 0, null));
            } else {
                Toast.makeText(Telpo330PrinterStatic.this, "Printer Initialization", Toast.LENGTH_LONG).show();
            }
        }
    }

    private String getSinglePrintType() {
        Intent intent = getIntent();
        String voucherText = intent.getStringExtra("VOUCHER_TEXT");


        return voucherText;
    }

    private String getPrintTypes() {
        Intent intent = getIntent();
        String type = intent.getStringExtra("printType");

        return type;
    }

    private String getLogoTypes() {
        Intent intent = getIntent();
        String type = intent.getStringExtra("logo");

        return type;
    }


    private void getSelectedVouchers() {
        Intent intent = getIntent();

        amount = intent.getStringExtra("AMOUNT");
        serialNumber = intent.getStringExtra("SERIAL_NUMBER");
        pinNumber = intent.getStringExtra("PIN_NUMBER");
        expiryDate = intent.getStringExtra("EXPIRY_DATE");
        System.out.println("amount is" + amount);
        System.out.println("pinNumber is" + pinNumber);
        System.out.println("serialNumber is" + serialNumber);
        System.out.println("expiryDate is" + expiryDate);
        System.out.println("picturePath " + picturePath);
    }
}



