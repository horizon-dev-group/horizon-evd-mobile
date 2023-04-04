package com.example.fibath.ui.Printer.sunmi.v2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.Toast;

import com.example.fibath.R;import com.example.fibath.classes.encryption.CryptLib;
import com.example.fibath.classes.user.User;
import com.example.fibath.classes.voucher.VoucherBulkPrint;
import com.example.fibath.classes.voucher.VoucherBulkPrintStatic;
import com.example.fibath.ui.Printer.sunmi.v2.utils.BitmapUtil;
import com.example.fibath.ui.Printer.sunmi.v2.utils.BluetoothUtil;
import com.example.fibath.ui.Printer.sunmi.v2.utils.SunmiPrintHelper;
import com.example.fibath.ui.home.NewHome;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.NoSuchPaddingException;

public class SunmiV2Printer extends BaseActivity {
    private int print_num = 0;
    private int print_size = 8;
    private int error_level = 3;
    private String isReprint = "";
    private String isBulk = "";
    String offline = "";

    // region Electronic Voucher Variables
    private int cardFive, cardTen, cardFifteen;
    // endregion

    private String picturePath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private String amount, serialNumber, pinNumber, expiryDate;
    Button back_button_printer;
    CryptLib cryptLib = new CryptLib();

    public SunmiV2Printer() throws NoSuchPaddingException, NoSuchAlgorithmException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.printer_layout);
        back_button_printer = findViewById(R.id.back_button_printer);
        offline = getIntent().getStringExtra("offline");
        isReprint = getIntent().getStringExtra("isReprint");
        isBulk = getIntent().getStringExtra("isBulk");
        getSelectedVouchers();
        loadPrintLogo();
        try {
            printVoucher();
        } catch (Exception e) {
            e.printStackTrace();
        }
        back_button_printer.setOnClickListener(v -> {
//            Intent intent = new Intent(getApplicationContext(), Voucher.class);
            Intent intent = new Intent(getApplicationContext(), NewHome.class);
            startActivity(intent);
        });
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

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Intent homeIntent = new Intent(getApplicationContext(), Voucher.class);
        Intent homeIntent = new Intent(getApplicationContext(), NewHome.class);
        startActivity(homeIntent);
    }

    private void loadPrintLogo() {
        File fileVocher = new File(picturePath + "/horizon_ethiotelecom_logo.bmp");
        File fileVocher2 = new File(picturePath + "/my_bab_ad.bmp");
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
        if (!fileVocher2.exists()) {
            InputStream inputStream = null;
            FileOutputStream fos = null;
            byte[] tmp = new byte[1024];
            try {
                inputStream = getApplicationContext().getAssets().open("my_bab_ad.png");
                fos = new FileOutputStream(fileVocher2);
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
        File file2 = new File(picturePath);
        if (!file2.exists()) {
            InputStream inputStream = null;
            FileOutputStream fos = null;
            byte[] tmp = new byte[1024];
            try {
                inputStream = getApplicationContext().getAssets().open("my_bab_ad.png");
                fos = new FileOutputStream(file2);
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

    // region Sunmi Printer Methods
    // region Print Voucher
    private void printVoucher() throws Exception {
        if (offline.equals("true") && isReprint.equals("false")) {
            for (int i = 0; i < VoucherBulkPrint.getOfflineBulkPrint().size(); i++) {
                try {
                    printEthiotelVoucher(VoucherBulkPrint.getOfflineBulkPrint().size() - i, VoucherBulkPrint.getOfflineBulkPrint().get(i).getVoucherAmount(), VoucherBulkPrint.getOfflineBulkPrint().get(i).getVoucherNumber(), VoucherBulkPrint.getOfflineBulkPrint().get(i).getVoucherSerialNumber(), VoucherBulkPrint.getOfflineBulkPrint().get(i).getVoucherExpiryDate());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            VoucherBulkPrint.getOfflineBulkPrint().clear();
        } else if (offline.equals("false") && isReprint.equals("false")) {
            System.out.println("********************");
            System.out.println(VoucherBulkPrint.getBulkPrint().size());
            for (int ii = 0; ii < VoucherBulkPrint.getBulkPrint().size(); ii++) {
                printEthiotelVoucher(VoucherBulkPrint.getBulkPrint().size() - ii, VoucherBulkPrint.getBulkPrint().get(ii).getVoucherAmount(), VoucherBulkPrint.getBulkPrint().get(ii).getVoucherNumber(), VoucherBulkPrint.getBulkPrint().get(ii).getVoucherSerialNumber(), VoucherBulkPrint.getBulkPrint().get(ii).getVoucherExpiryDate());
            }
            VoucherBulkPrint.getBulkPrint().clear();
        } else if (offline.equals("true") && isReprint.equals("true")) {
            if (isBulk.equals("true")) { // isbulk true
                System.out.println("offline");
                for (int i = 0; i < VoucherBulkPrintStatic.getOfflineBulkPrint().size(); i++) {
                    try {
                        printEthiotelVoucher(0, VoucherBulkPrintStatic.getOfflineBulkPrint().get(i).getVoucherAmount(), VoucherBulkPrintStatic.getOfflineBulkPrint().get(i).getVoucherNumber(), VoucherBulkPrintStatic.getOfflineBulkPrint().get(i).getVoucherSerialNumber(), VoucherBulkPrintStatic.getOfflineBulkPrint().get(i).getVoucherExpiryDate());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                VoucherBulkPrintStatic.getOfflineBulkPrint().clear();
            } else {
                printEthiotelVoucher(0, amount, pinNumber, serialNumber, expiryDate);
            }
        } else if (offline.equals("false") && isReprint.equals("true")) {
            if (isBulk.equals("true")) { // isbulk true
                for (int ii = 0; ii < VoucherBulkPrintStatic.getBulkPrint().size(); ii++) {
                    printEthiotelVoucher(0, VoucherBulkPrintStatic.getBulkPrint().get(ii).getVoucherAmount(), VoucherBulkPrintStatic.getBulkPrint().get(ii).getVoucherNumber(), VoucherBulkPrintStatic.getBulkPrint().get(ii).getVoucherSerialNumber(), VoucherBulkPrintStatic.getBulkPrint().get(ii).getVoucherExpiryDate());
                }
                VoucherBulkPrintStatic.getBulkPrint().clear();
            } else {
                printEthiotelVoucher(0, amount, pinNumber, serialNumber, expiryDate);
            }
        }
    }
    // endregion

    // region Print Ethiotelecom Voucher
    public void printEthiotelVoucher(int pageNumber, String amount, String pinNo, String serial, String expiryDate) throws Exception {
        // String date = new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(new Date());
        String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

        String originalPin = pinNo;
        pinNo = cryptLib.decryptCipherTextWithRandomIV(pinNo, "123456789");

        String pinOne = pinNo.substring(0, 4);
        String pinTwo = pinNo.substring(4, 7);
        String pinThree = pinNo.substring(7, 10);
        String pinFour = pinNo.substring(10);
        pinNo = pinOne + "-" + pinTwo + "-" + pinThree + "-" + pinFour;
        System.out.println("i am in Ethiotelecom voucher pattern is" + pinOne);
        System.out.println("i am in Ethiotelecom voucher pattern is" + pinTwo);
        System.out.println("i am in Ethiotelecom voucher pattern is" + pinThree);
        System.out.println("i am in Ethiotelecom voucher pattern is" + pinFour);
        if (isReprint.equals("true")) {
            printText("*COPY*", "20", true, false, 1, false);
        }

        // region Print Logo
        File file = new File(picturePath + "/horizon_ethiotelecom_logo.bmp");
        // bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.test1, options);
        // bitmap1 = scaleImage(bitmap1);
        if (file.exists()) {
            printLogo(BitmapFactory.decodeFile(picturePath + "/horizon_ethiotelecom_logo.bmp"), 1);
        } else {
            Toast.makeText(SunmiV2Printer.this, "Logo not found!", Toast.LENGTH_LONG).show();
        }
        // endregion
        printText(amount.replace(".00", "") + " Birr", "50", true, false, 1, false);
        printText("-------e-voucher Pin-------", "26", true, false, 1, false);
        // print main title
        printText(pinNo, "40", true, false, 1, false);
        printText("---------------------------", "26", false, false, 1, false);

        // print amount title
        printText("To Recharge *705*e-voucher pin#", "24", false, false, 1, false);
//        printText("To Check Your Balance *804#", "24", false, false, 1, false);
        // print pin title
        printText("SN: " + serial, "26", true, false, 1, false);
        printText("Agent-" + User.getUserSessionUsername(), "22", true, false, 1, false);
        printText("Powered By:" + "\u23FB" + "Horizontech™", "22", false, false, 1, false);
//        printText("=== Thank you for buying ===", "22", false, false, 1, false);
        printText("PD:" + date + " ED:" + expiryDate, "26", true, false, 1, false);
//        printText("Expiry Date: " + expiryDate, "26", true, false, 1, false);
        if (pageNumber != 0) {
            printText("-------------" + pageNumber + "--------------", "26", false, false, 1, true);
        } else {
            printText("---------------------------", "26", false, false, 1, true);
        }
        System.out.println("}}}}}}}}}]] " + date);


        // print qr code
        // region Print Logo
        File file2 = new File(picturePath + "/my_bab_ad.bmp");
//         bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.test1, options);
//         bitmap1 = scaleImage(bitmap1);
        if (file2.exists()) {
//            printLogo(BitmapFactory.decodeFile(picturePath + "/my_bab_ad.bmp"), 1);
        } else {
            Toast.makeText(SunmiV2Printer.this, "Logo not found!", Toast.LENGTH_LONG).show();
        }


    }
    // endregion

    // region Print Text
    private void printText(String content, String fontSize, boolean isBold, boolean isUnderLine,
                           int align, boolean addSpace) {
        // boolean isBold = false;
        // boolean isUnderLine = false;

        float size = Integer.parseInt(fontSize);
        if (!BluetoothUtil.isBlueToothPrinter) {
            SunmiPrintHelper.getInstance().setAlign(align);
            SunmiPrintHelper.getInstance().printText(content, size, isBold, isUnderLine);
            SunmiPrintHelper.getInstance().printText("\n", size, isBold, isUnderLine);
            if (addSpace) {
                SunmiPrintHelper.getInstance().feedPaper();
            }
        } else {
            // printByBluTooth(content);
        }
    }
    // endregion


    // region Print Text
    private void printText(String content, String fontSize, boolean isBold, boolean isUnderLine,
                           int align, boolean addSpace, String s) {
        // boolean isBold = false;
        // boolean isUnderLine = false;

        float size = Integer.parseInt(fontSize);
        if (!BluetoothUtil.isBlueToothPrinter) {
            SunmiPrintHelper.getInstance().setAlign(align);
            SunmiPrintHelper.getInstance().printText(content, size, isBold, isUnderLine);
            SunmiPrintHelper.getInstance().printText(s, size, isBold, isUnderLine);
            if (addSpace) {
                SunmiPrintHelper.getInstance().feedPaper();
            }
        } else {
            // printByBluTooth(content);
        }
    }
    // endregion

    // region Print QR Code
    private void printQRCode(String content) {
        Bitmap bitmap = BitmapUtil.generateBitmap(content, 9, 700, 700);
        // if (bitmap != null) {
        //     mImageView.setImageDrawable(new BitmapDrawable(bitmap));
        // }

        if (!BluetoothUtil.isBlueToothPrinter) {
//            SunmiPrintHelper.getInstance().printQr(content, print_size, error_level);
            SunmiPrintHelper.getInstance().printBarCode(content, 8, 25, 2, 0);
//            SunmiPrintHelper.getInstance().feedPaper();
        } else {
            // if (print_num == 0) {
            //     BluetoothUtil.sendData(ESCUtil.getPrintQRCode(mTextView1.getText().toString(), print_size, error_level));
            //     BluetoothUtil.sendData(ESCUtil.nextLine(3));
            // } else {
            //     BluetoothUtil.sendData(ESCUtil.getPrintDoubleQRCode(mTextView1.getText().toString(), mTextView1.getText().toString(), print_size, error_level));
            //     BluetoothUtil.sendData(ESCUtil.nextLine(3));
            // }
        }
    }
    // endregion

    // region Print Logo

    /**
     * Scaled image width is an integer multiple of 8 and can be ignored
     */
    private Bitmap scaleImage(Bitmap bitmap1) {
        int width = bitmap1.getWidth();
        int height = bitmap1.getHeight();
        // 设置想要的大小
        int newWidth = (width / 8 + 1) * 8;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, 1);
        // 得到新的图片
        return Bitmap.createBitmap(bitmap1, 0, 0, width, height, matrix, true);
    }

    public void printLogo(Bitmap bitmap, int orientation) {
        if (!BluetoothUtil.isBlueToothPrinter) {
            SunmiPrintHelper.getInstance().printBitmap(bitmap, orientation);
            SunmiPrintHelper.getInstance().setAlign(1);
            SunmiPrintHelper.getInstance().printText("\n", 26, false, false);
            // SunmiPrintHelper.getInstance().feedPaper();
        } else {
            // if(mytype == 0){
            //     if(mCheckBox1.isChecked() && mCheckBox2.isChecked()){
            //         BluetoothUtil.sendData(ESCUtil.printBitmap(bitmap1, 3));
            //     }else if(mCheckBox1.isChecked()){
            //         BluetoothUtil.sendData(ESCUtil.printBitmap(bitmap1, 1));
            //     }else if(mCheckBox2.isChecked()){
            //         BluetoothUtil.sendData(ESCUtil.printBitmap(bitmap1, 2));
            //     }else{
            //         BluetoothUtil.sendData(ESCUtil.printBitmap(bitmap1, 0));
            //     }
            // }else if(mytype == 1){
            //     BluetoothUtil.sendData(ESCUtil.selectBitmap(bitmap1, 0));
            // }else if(mytype == 2){
            //     BluetoothUtil.sendData(ESCUtil.selectBitmap(bitmap1, 1));
            // }else if(mytype == 3){
            //     BluetoothUtil.sendData(ESCUtil.selectBitmap(bitmap1, 32));
            // }else if(mytype == 4){
            //     BluetoothUtil.sendData(ESCUtil.selectBitmap(bitmap1, 33));
            // }
            // BluetoothUtil.sendData(ESCUtil.nextLine(3));
        }
    }
    // endregion
    // endregion
    // endregion
}
