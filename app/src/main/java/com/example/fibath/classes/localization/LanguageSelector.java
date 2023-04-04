package com.example.fibath.classes.localization;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.example.fibath.classes.state.StateManager;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class LanguageSelector {

    // region Saves App Language To Shared Preferences
    public static void saveAppLanguage(Context passedContext, String locale) {
        SharedPreferences sharedPreferences = passedContext.getSharedPreferences("Preferences", MODE_PRIVATE);
        assert sharedPreferences != null;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("app_language", locale);
        editor.apply();
        StateManager.setAppLanguage(locale);
//        setLanguage(passedContext);
    }
    // endregion

    // region Set App Language Default
    public static void setLanguage(Context passedContext) {
        String language = loadSavedLanguage(passedContext);
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        passedContext.getResources().updateConfiguration(config, passedContext.getResources().getDisplayMetrics());
    }
    // endregion

    // region Set App Language Localization Activity
    public static void setLanguage(LocalizationActivity passedActivity, String language) {
        passedActivity.setLanguage(language);
    }
    // endregion

    // region Load Saved Language
    public static String loadSavedLanguage(Context passedContext) {
        SharedPreferences sharedPreferences = passedContext.getSharedPreferences("Preferences", MODE_PRIVATE);
        String language = "en";
        if (sharedPreferences != null) {
            if (sharedPreferences.contains("app_language")) {
                language = sharedPreferences.getString("app_language", "en");
            } else {
                language = "en";
            }
        }
        return language;
    }
    // endregion
}
