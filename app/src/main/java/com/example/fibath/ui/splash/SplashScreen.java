package com.example.fibath.ui.splash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.example.fibath.classes.localization.LanguageSelector;
import com.example.fibath.classes.state.StateManager;
import com.example.fibath.classes.user.User;
import com.example.fibath.ui.helpers.UIHelpers;
import com.example.fibath.ui.intro.Intro;
import com.example.fibath.ui.login.Login;

public class SplashScreen extends LocalizationActivity {
    User user = new User();

    // region On Create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        <?xml version='1.0' encoding='utf-8' standalone='yes' ?>
//<map>
//    <string name="user_email">0944345689</string>
//    <boolean name="first_time" value="false" />
//    <string name="user_name">lemmma Abebe</string>
//    <string name="user_id">5ed0f455db1a6f21c872c11a</string>
//    <string name="user_token">eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI1ZWQwZjQ1NWRiMWE2ZjIxYzg3MmMxMWEiLCJpYXQiOjE2MzMyNDI1ODR9.Gl3t0KjOsbRSyb8OPCE9Mi0ps4A_XgPtifKaAPsk-PQ</string>
//    <string name="userType">5eb1b4e50b65b13950b7bcca</string>
//</map>


//        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("cookies", MODE_PRIVATE);
////        removeUserSessionData(getApplicationContext());
//        assert sharedPreferences != null;
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("user_id", "611bb16654a71504b96d43a6");
//        editor.putString("user_email", "test@gmail.com");
//        editor.putString("user_name", "Lemma");
//        editor.putString("user_token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2MTFiYjE2NjU0YTcxNTA0Yjk2ZDQzYTYiLCJpYXQiOjE2MjkyMDQ4OTR9.gi5u3Dl2AtdhUZZs6otvEb3tiQoT5BfomXiDV0eCt");
//        editor.putString("userType", "5eb1b13d0b65b13950b7bcc4");
//        editor.apply();

        user.loadUserSessionData(this);
        loadApplicationPreferences();
        initUI();
        startApp();
    }
    // endregion

    // region Initialize App
    private void initUI() {
        UIHelpers.makeWindowTransparent(this);
    }

    private void loadApplicationPreferences() {
        loadAppLanguage();
        loadAppLunchedTimeState();
    }

    private void loadAppLanguage() {
        String savedLanguage = LanguageSelector.loadSavedLanguage(getApplicationContext());
        StateManager.setAppLanguage(savedLanguage);
    }

    private void loadAppLunchedTimeState() {
        SharedPreferences sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean("isAppFirstStart", false)) {
            StateManager.setAppFirstStart(true);
        }
    }
    // endregion

    // region Start App
    private void startApp() {
        setLanguage(StateManager.getAppLanguage());
        // Toast.makeText(getApplicationContext(), "Saved Language: " + StateManager.getAppLanguage(), Toast.LENGTH_SHORT).show();

//        if (isNetworkConnected() && User.getUserSessionToken()!=null) {
//            if(!OfflineVouchersManager.isSyncing()){
//                new OfflineVouchersManager(getApplicationContext(),true).sync();
//            }else{
////                Toast.makeText(getApplicationContext(),"Sync Already In Progress  Splash Screen",Toast.LENGTH_LONG).show();
//            }
//
//        } else {
        if (!StateManager.isAppFirstStart()) {
            startActivity(new Intent(getApplicationContext(), Intro.class)); // Intro.class
        } else {
            startActivity(new Intent(getApplicationContext(), Login.class));
        }
//        }
    }
    // endregion

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
