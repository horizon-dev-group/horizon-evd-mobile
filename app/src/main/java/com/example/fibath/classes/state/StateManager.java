package com.example.fibath.classes.state;

public class StateManager {
    // region States
    private static boolean appFirstStart = false;
    private static String appLanguage = "en";
    private static boolean languageChanged = false;
    // Login States
    private static String password = "";


    private static String email="";
    //Balance State
    private static  String balance="0";

    //Sold Items
    private static double sold=0;
//Dashboard Mode
    private static int minimal=0;
    private  static int advanced=1;


    // endregion

    // region Getter And Setter For App First Start
    public static boolean isAppFirstStart() {
        return appFirstStart;
    }

    public static void setAppFirstStart(boolean appFirstStart) {
        StateManager.appFirstStart = appFirstStart;
    }
    // endregion

    // region Getter And Setter For App Language
    public static String getAppLanguage() {
        return appLanguage;
    }

    public static void setAppLanguage(String appLanguage) {
        StateManager.appLanguage = appLanguage;
    }
    // endregion

    public static void setAppSeting(int appSetting) {
        StateManager.minimal = appSetting;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        StateManager.password = password;
    }

    public static String getBalance() {
        return balance;
    }

    public static void setBalance(String balance) {
        StateManager.balance = balance;
    }
    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        StateManager.email = email;
    }

    public static boolean isLanguageChanged() {
        return languageChanged;
    }

    public static void setLanguageChanged(boolean languageChanged) {
        StateManager.languageChanged = languageChanged;
    }
}
