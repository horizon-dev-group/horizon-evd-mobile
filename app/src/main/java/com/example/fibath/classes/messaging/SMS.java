package com.example.fibath.classes.messaging;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.SmsManager;

import androidx.core.app.ActivityCompat;


public class SMS {
    private final int  MY_SMS_PERMISSION = 10;

    //1.1 send SMS
    public void sendSMS(String phone, String message, Activity appCompatActivity) {
        if (checkSMSPermission(appCompatActivity)) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone, null, message, null, null);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                appCompatActivity.requestPermissions(new String[]{
                        Manifest.permission.SEND_SMS
                }, MY_SMS_PERMISSION);
            }
        }
    }
    //1.2 check SMS PERMISSION
    public boolean checkSMSPermission(Activity appCompatActivity) {
        if (ActivityCompat.checkSelfPermission(appCompatActivity.getApplicationContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
}
