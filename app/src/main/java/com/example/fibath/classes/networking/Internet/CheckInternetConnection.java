package com.example.fibath.classes.networking.Internet;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.view.View;

import com.example.fibath.R;import com.geniusforapp.fancydialog.FancyAlertDialog;

public class CheckInternetConnection {
    Context ctx;

    public CheckInternetConnection(Context context){
        ctx=context;
    }

    public void checkConnection(){

        if(!isInternetConnected()) {

            final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(ctx)
                    .setBackgroundColor(R.color.colorPrimary)
                    .setimageResource(R.drawable.internetconnection)
                    .setTextTitle("No Internet")
                    .setTitleColor(R.color.colorLightWhite)
                    .setTextSubTitle("Cannot connect to a server")
                    .setSubtitleColor(R.color.colorLightWhite)
                    .setBody("No Connection")
                    .setBodyColor(R.color.red)
                    .setPositiveButtonText("Connect Now")
                    .setPositiveColor(R.color.colorLightWhite)
                    .setOnPositiveClicked(new FancyAlertDialog.OnPositiveClicked() {
                        @Override
                        public void OnClick(View view, Dialog dialog) {

                            if(isInternetConnected()){

                                dialog.dismiss();

                            }else {

                                Intent dialogIntent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                ctx.startActivity(dialogIntent);
                            }
                        }
                    })
                    .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
                    .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                    .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
                    .setCancelable(false)
                    .build();
            alert.show();
        }
    }

    private boolean isInternetConnected() {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isConnectedOrConnecting();

    }
}

