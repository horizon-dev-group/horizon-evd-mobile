package com.example.fibath.ui.Printer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fibath.R;import com.example.fibath.ui.Printer.bluetooth.BluetoothPrinter;
import com.example.fibath.ui.Printer.sunmi.v2.SunmiV2Printer;

public class PrinterOption extends AppCompatActivity {

    Button device_printer, bluetooth_printer;
    // region Electronic Voucher Variables
    private int cardFive, cardTen, cardFifteen;
    // endregion
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.printer_option);
        device_printer = findViewById(R.id.device_printer);
        bluetooth_printer = findViewById(R.id.bluetooth_printer);
        device_printer.setOnClickListener(v -> {
            Intent  devicePrinter=new Intent(getApplicationContext(), SunmiV2Printer.class);
            devicePrinter.putExtra("printType", getPrintTypes());
            devicePrinter.putExtra("logo", getLogoTypes());
            startActivity(devicePrinter);
        });

        bluetooth_printer.setOnClickListener(v -> {
                    Intent  bluetoothPrinter=new Intent(getApplicationContext(), BluetoothPrinter.class);
                    startActivity(bluetoothPrinter);
        }
        );
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
    }
}
