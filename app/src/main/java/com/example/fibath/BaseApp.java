package com.example.fibath;

import android.app.Application;
import android.content.Context;

import com.example.fibath.BuildConfig;
import com.example.fibath.ui.Printer.sunmi.v2.utils.SunmiPrintHelper;

import org.acra.ACRA;
import org.acra.config.CoreConfigurationBuilder;
import org.acra.config.HttpSenderConfigurationBuilder;
import org.acra.data.StringFormat;
import org.acra.sender.HttpSender;

public class BaseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ACRA.DEV_LOGGING = true;
        // Initialization of ACRA
        ACRA.init(this, new CoreConfigurationBuilder()
                        //core configuration:
                        .withBuildConfigClass(BuildConfig.class)
                        .withReportFormat(StringFormat.JSON)
                        .withReportSendSuccessToast("Success")
//                        .withReportSendFailureToast("soryyyyyyyyyyyyyyyyyy")

                        .withPluginConfigurations(
                                new HttpSenderConfigurationBuilder()
                                        .withUri("http://196.188.232.219:3000/api/users/report/crash")
                                        // defaults to POST
                                        .withHttpMethod(HttpSender.Method.POST)
                                        //defaults to 5000ms
                                        .withConnectionTimeout(5000)
                                        //defaults to 20000ms
                                        .withSocketTimeout(20000)
                                        // defaults to false
                                        .withDropReportsOnTimeout(false)
                                        .build()
                        )
        );


    }

    /**
     * Connect print service through interface library
     */
    private void init() {
        SunmiPrintHelper.getInstance().initSunmiPrinterService(this);
    }
}
