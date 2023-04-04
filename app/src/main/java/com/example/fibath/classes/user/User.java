package com.example.fibath.classes.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.example.fibath.R;import com.example.fibath.classes.api.APIInterface;
import com.example.fibath.classes.model.AccountResponse;
import com.example.fibath.classes.model.LoginResponse;
import com.example.fibath.classes.model.LogoutResponse;

import com.example.fibath.classes.model.PasswordResponse;
import com.example.fibath.classes.networking.communication.ServerCommunicator;
import com.example.fibath.classes.state.StateManager;
import com.example.fibath.ui.home.NewHome;
import com.example.fibath.ui.login.Login;
import com.victor.loading.rotate.RotateLoading;

import java.util.HashMap;


import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.example.fibath.classes.api.APIInterface.APP_VERSION;
import static com.example.fibath.classes.api.APIInterface.BASE_URL;

public class User {
    // region User Login Variables
    private static String userSessionId;
    private static String userSessionEmail;
    private static String userSessionUsername;
    private static String userSessionPassword;
    private static String userSessionToken;
    private static String userSessionUserType;
    private static boolean isFirstTime = true;
    String applicationVersion;

    public static String getUserSessionUserType() {
        return userSessionUserType;
    }

    public static void setUserSessionUserType(String userSessionUserType) {
        User.userSessionUserType = userSessionUserType;
    }

    public static boolean isIsFirstTime() {
        return isFirstTime;
    }

    public static void setIsFirstTime(boolean isFirstTime) {
        User.isFirstTime = isFirstTime;
    }

    public Boolean user_available = true;
    public static int getBackToLogin = 0;
    // endregion

    // region Setter and getter for User variables
    public static String getUserSessionId() {
        return userSessionId;
    }

    public static void setUserSessionId(String userSessionId) {
        User.userSessionId = userSessionId;
    }

    public static String getUserSessionEmail() {
        return userSessionEmail;
    }

    public static void setUserSessionEmail(String userSessionEmail) {
        User.userSessionEmail = userSessionEmail;
    }

    public static String getUserSessionPassword() {
        return userSessionPassword;
    }

    public static void setUserSessionPassword(String userSessionPassword) {
        User.userSessionPassword = userSessionPassword;
    }

    public static String getUserSessionUsername() {
        return userSessionUsername;
    }

    public static void setUserSessionUsername(String userSessionUsername) {
        User.userSessionUsername = userSessionUsername;
    }

    public static String getUserSessionToken() {
        return userSessionToken;
    }

    public static void setUserSessionToken(String userSessionToken) {
        User.userSessionToken = userSessionToken;
    }
    //endregion

    // region Load user session data from shared preferences
    public void loadUserSessionData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("cookies", MODE_PRIVATE);
        if (sharedPreferences != null) {
            if (sharedPreferences.contains("user_id") && sharedPreferences.contains("user_email") && sharedPreferences.contains("user_name") && sharedPreferences.contains("user_token") && sharedPreferences.contains("first_time") && sharedPreferences.contains("userType")) {
                setUserSessionId(userSessionId = sharedPreferences.getString("user_id", ""));
                setUserSessionEmail(userSessionEmail = sharedPreferences.getString("user_email", ""));
                setUserSessionUsername(userSessionUsername = sharedPreferences.getString("user_name", ""));
                setUserSessionToken(userSessionToken = sharedPreferences.getString("user_token", ""));
                setUserSessionUserType(userSessionUserType = sharedPreferences.getString("userType", ""));
                setIsFirstTime(isFirstTime = sharedPreferences.getBoolean("first_time", true));
            } else {
                userSessionId = null;
                userSessionEmail = null;
                userSessionUsername = null;
                userSessionToken = null;
                userSessionUserType = null;
                isFirstTime = true;
            }
        } else {
            userSessionId = null;
            userSessionEmail = null;
            userSessionPassword = null;
            userSessionUsername = null;
            userSessionToken = null;
            userSessionUserType = null;
            isFirstTime = true;

            System.out.println("Data is Also Null");
        }
    }
    //endregion

    // region Remove user session data from shared preferences
    public void removeUserSessionData(Context context) {
        userSessionId = null;
        userSessionEmail = null;
        userSessionPassword = null;
        userSessionUsername = null;
        userSessionUserType = null;

        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences("cookies", MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.remove("user_id");
        sharedPreferencesEditor.remove("user_email");
        sharedPreferencesEditor.remove("user_name");
        sharedPreferencesEditor.remove("user_token");
        sharedPreferencesEditor.remove("userType");
        sharedPreferencesEditor.apply();
        sharedPreferencesEditor.clear();
    }
    //endregion

    // region Initialize network elements and send user session data to server to validate user
    public void setUserSessionData(Context passedContext, String UserId, String UserLoginInfo, String UserName, String token, boolean firsttime, String userType) {
        SharedPreferences sharedPreferences = passedContext.getSharedPreferences("cookies", MODE_PRIVATE);
        removeUserSessionData(passedContext);
        assert sharedPreferences != null;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_id", UserId);
        editor.putString("user_email", UserLoginInfo);
        editor.putString("user_name", UserName);
        editor.putString("user_token", token);
        editor.putBoolean("first_time", firsttime);
        editor.putString("userType", userType);
        editor.apply();
        loadUserSessionData(passedContext);
    }

    public void login(final Context passedContext, HashMap<String, String> requestBodys, RotateLoading rotateLoading) {
        startProgress(rotateLoading);
        APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
        myInterface.retailerLogin(APP_VERSION, requestBodys)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<LoginResponse>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(@NonNull Response<LoginResponse> loginEndPointResponseData) {
                System.out.println("DDDDL: " + loginEndPointResponseData.code());
                if (loginEndPointResponseData.code() == 200) {
                    stopProgress(rotateLoading);
                    StateManager.setAppFirstStart(true);
                    System.out.println("Succccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc");
                    System.out.println(loginEndPointResponseData.body().getUser().getUserType());
                    setUserSessionData(passedContext, String.valueOf(loginEndPointResponseData.body().getUser().getId()), loginEndPointResponseData.body().getUser().getPhone(), loginEndPointResponseData.body().getUser().getName(), loginEndPointResponseData.body().getToken(), false, loginEndPointResponseData.body().getUser().getUserType());
                    System.out.println("Afffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
                    userBalance(passedContext, loginEndPointResponseData.body().getToken());

                    SharedPreferences sharedPreferences = passedContext.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                    if (sharedPreferences.getString("HomePageMode", "1").equals("1")) {
//                        Intent intent = new Intent(passedContext, Voucher.class);
                        Intent intent = new Intent(passedContext, NewHome.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        passedContext.startActivity(intent);
                    } else {
//                        Intent intent = new Intent(passedContext, Home.class);
//                        Intent intent = new Intent(passedContext, Voucher.class);
                        Intent intent = new Intent(passedContext, NewHome.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        passedContext.startActivity(intent);
                    }


                } else {
                    stopProgress(rotateLoading);
                    Toast.makeText(passedContext, "Invalid Credential", Toast.LENGTH_SHORT).show();
                    System.out.println("Login Error is" + loginEndPointResponseData.message());
                }
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT: ");
                System.out.println(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }


    public void changePassword(final Context passedContext, HashMap<String, String> requestBody, Button button) {
        System.out.println("The BAse Url is" + BASE_URL);
        button.setEnabled(false);
        button.setText("Changing Password");
        button.setBackgroundResource(R.drawable.btn_rounded_grayed);
        APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
        myInterface.changePassword(User.getUserSessionToken(), APP_VERSION, requestBody)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<PasswordResponse>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Response<PasswordResponse> loginEndPointResponseData) {
                if (loginEndPointResponseData.code() == 201) {
                    Toast.makeText(passedContext, "Password Successfully Changed, Next time you login use your new password", Toast.LENGTH_SHORT).show();
//                    removeUserSessionData(passedContext);
//                    Intent intent = new Intent(passedContext, Home.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    passedContext.startActivity(intent);
                } else if (loginEndPointResponseData.code() == 400) {
                    Toast.makeText(passedContext, "Invalid Credential", Toast.LENGTH_SHORT).show();
                } else if (loginEndPointResponseData.code() == 500) {
                    System.out.println("Error is Server" + loginEndPointResponseData.message());
                }
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("has" + e);
                button.setEnabled(true);
                button.setBackgroundResource(R.drawable.btn_rounded);
                button.setText("Change");
            }

            @Override
            public void onComplete() {
                button.setEnabled(true);
                button.setBackgroundResource(R.drawable.btn_rounded);
                button.setText("Change");
            }
        });
    }

    public void logout(final Context passedContext) {
        System.out.println("The BAse Url is" + BASE_URL);
        HashMap<String, String> requestBody = new HashMap<>();

        APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
        myInterface.retailerLogout(User.getUserSessionToken(), APP_VERSION)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<LogoutResponse>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Response<LogoutResponse> logoutResponseResponse) {

                System.out.println("Error code is" + logoutResponseResponse.code());
                if (logoutResponseResponse.code() == 200) {

                    removeUserSessionData(passedContext);
                    Intent intent = new Intent(passedContext, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    passedContext.startActivity(intent);
                } else {
                    Toast.makeText(passedContext, "Unable To Logout", Toast.LENGTH_SHORT).show();
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


    private void startProgress(RotateLoading rotateLoading) {
        rotateLoading.start();
    }

    private void stopProgress(RotateLoading rotateLoading) {
        rotateLoading.stop();
    }


    public void userBalance(final Context passedContext, String token) {
        HashMap<String, String> requestBody = new HashMap<>();

        APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
        myInterface.retailerAccountInformation(token, APP_VERSION)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<AccountResponse>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Response<AccountResponse> balanceEndPointResponseData) {

                System.out.println("Token is" + getUserSessionToken());

                if (balanceEndPointResponseData.code() == 200) {


                    StateManager.setBalance(String.valueOf(balanceEndPointResponseData.body().getBalance()));

                } else {
                    Toast.makeText(passedContext, passedContext.getResources().getString(R.string.someting_went_worong), Toast.LENGTH_SHORT).show();

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
    //endregion
}
