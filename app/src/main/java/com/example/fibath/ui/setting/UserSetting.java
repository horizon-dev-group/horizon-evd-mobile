package com.example.fibath.ui.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.example.fibath.R;import com.example.fibath.classes.localization.LanguageSelector;
import com.example.fibath.classes.state.StateManager;
import com.example.fibath.classes.user.User;
import com.jakewharton.processphoenix.ProcessPhoenix;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserSetting extends LocalizationActivity {
    @BindView(R.id.user_setting_screen_switch_dashboard_minimal_mode)
    Switch userSettingScreenSwitchDashboardMinimalMode;
    @BindView(R.id.user_setting_screen_switch_large_printer_mode)
    Switch user_setting_screen_switch_large_printer_mode;
    @BindView(R.id.user_setting_screen_switch_enable_bluetooth_printer)
    Switch user_setting_screen_switch_enable_bluetooth_printer;
    @BindView(R.id.userSettingEnableDeductedTransactions)
    Switch userSettingEnableDeductedTransactions;
    @BindView(R.id.activity_change_password_old_password)
    EditText old_password;
    @BindView(R.id.activity_change_password_new_password)
    EditText new_password;
    @BindView(R.id.activity_change_password_confirm_password)
    EditText confirm_password;
    @BindView(R.id.activity_change_password_btn_change_password)
    Button activity_change_password_btn_change_password;
    @BindView(R.id.user_setting_change_language)
    MaterialSpinner user_setting_change_language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_new_page);
        ButterKnife.bind(this);
        initUi();
    }


    private void initUi() {
        initSwitch();
        activity_change_password_btn_change_password.setOnClickListener(view -> {
            changePassword();
        });
//        Lingver.init((Application) getApplicationContext(), StateManager.getAppLanguage());
    }

    private void initSwitch() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Preferences", MODE_PRIVATE);
        assert sharedPreferences != null;

        if (sharedPreferences.getString("HomePageMode", "1").equals("1")) { // 0 = advanced 1 = minimal
            userSettingScreenSwitchDashboardMinimalMode.setChecked(true);
        } else {
            userSettingScreenSwitchDashboardMinimalMode.setChecked(false);
        }

        if (sharedPreferences.getString("LargePrinterMode", "").equals("1")) { // 0 = advanced 1 = minimal
            user_setting_screen_switch_large_printer_mode.setChecked(true);
        } else {
            user_setting_screen_switch_large_printer_mode.setChecked(false);
        }

        if (sharedPreferences.getString("BluetoothPrinterMode", "1").equals("1")) { // 0 = advanced 1 = minimal
            user_setting_screen_switch_enable_bluetooth_printer.setChecked(true);
        } else {
            user_setting_screen_switch_enable_bluetooth_printer.setChecked(false);
        }

        if (sharedPreferences.getString("ShowDeductedTransactions", "").equals("1")) { // 0 = advanced 1 = minimal
            userSettingEnableDeductedTransactions.setChecked(true);
        } else {
            userSettingEnableDeductedTransactions.setChecked(false);
        }

        initLanguagePicker();

        userSettingScreenSwitchDashboardMinimalMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                setAppSetting(getApplicationContext(), "HomePageMode", "1");
                getAppSetting(getApplicationContext(), "HomePageMode");
            } else {
                setAppSetting(getApplicationContext(), "HomePageMode", "0");
                getAppSetting(getApplicationContext(), "HomePageMode");
            }
        });

        // 1 = large, 0 = small
        user_setting_screen_switch_large_printer_mode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                setAppSetting(getApplicationContext(), "LargePrinterMode", "1");
                getAppSetting(getApplicationContext(), "LargePrinterMode");
            } else {
                setAppSetting(getApplicationContext(), "LargePrinterMode", "0");
                getAppSetting(getApplicationContext(), "LargePrinterMode");
            }
        });

        // 1 = bluetooth Printer, 0 = default sunmi
        user_setting_screen_switch_enable_bluetooth_printer.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                setAppSetting(getApplicationContext(), "BluetoothPrinterMode", "1");
                getAppSetting(getApplicationContext(), "BluetoothPrinterMode");
            } else {
                setAppSetting(getApplicationContext(), "BluetoothPrinterMode", "0");
                getAppSetting(getApplicationContext(), "BluetoothPrinterMode");
            }
        });

        // 1 = bluetooth Printer, 0 = default sunmi
        userSettingEnableDeductedTransactions.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                setAppSetting(getApplicationContext(), "ShowDeductedTransactions", "1");
                getAppSetting(getApplicationContext(), "ShowDeductedTransactions");
            } else {
                setAppSetting(getApplicationContext(), "ShowDeductedTransactions", "0");
                getAppSetting(getApplicationContext(), "ShowDeductedTransactions");
            }
        });
    }

    // region Password Change
    private boolean validateOldPassword() {
        if (old_password.getText().toString().equals("")) {
            old_password.setError("Enter Your Password");
            return false;
        }
        return true;
    }

    private boolean validateNewPassword() {
        if (new_password.getText().toString().equals("")) {
            new_password.setError(getResources().getString(R.string.setting_enter_new_password));
            return false;
        } else if (new_password.getText().length() < 4) {
            new_password.setError(getResources().getString(R.string.my_agnets_enter_valid_password_length));
            return false;
        }
        return true;
    }

    private boolean validateConfirmPassword() {
        if (confirm_password.getText().toString().equals("")) {
            confirm_password.setError(getResources().getString(R.string.my_agents_confirm_password));
            return false;
        } else if (!confirm_password.getText().toString().equals(new_password.getText().toString())) {
            confirm_password.setError(getResources().getString(R.string.my_agnets_password_should_match));
            return false;
        }
        return true;
    }

    private class ValidationTextWatcher implements TextWatcher {
        private View view;

        private ValidationTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.activity_change_password_old_password:
                    validateOldPassword();
                    break;
                case R.id.activity_change_password_new_password:
                    validateNewPassword();
                    break;
                case R.id.activity_change_password_confirm_password:
                    validateConfirmPassword();
                    break;
            }
        }
    }

    public void changePassword() {
        User user = new User();
        HashMap<String, String> map = new HashMap<>();
        map.put("old_password", old_password.getText().toString());
        map.put("new_password", new_password.getText().toString());
        user.changePassword(getApplicationContext(), map, activity_change_password_btn_change_password);
    }
    // endregion

    // region Saves App Setting To Shared Preferences
    public static void setAppSetting(Context passedContext, String key, String setting) {
        SharedPreferences sharedPreferences = passedContext.getSharedPreferences("Preferences", MODE_PRIVATE);
        assert sharedPreferences != null;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, setting);
        editor.apply();
    }
    // endregion

    public String getAppSetting(Context passedContext, String key) {
        SharedPreferences sharedPreferences = passedContext.getSharedPreferences("Preferences", MODE_PRIVATE);
        assert sharedPreferences != null;
        return sharedPreferences.getString(key, "");
    }

    // 1.2: Init UI Components
    private void initLanguagePicker() {
        user_setting_change_language.setItems("English", "አማርኛ", "Oromifa", "ትግርኛ", "Somali", "Afar");
        if (StateManager.getAppLanguage().equals("en")) {
            user_setting_change_language.setSelectedIndex(0);
        } else if (StateManager.getAppLanguage().equals("am")) {
            user_setting_change_language.setSelectedIndex(1);
        } else if (StateManager.getAppLanguage().equals("om")) {
            user_setting_change_language.setSelectedIndex(2);
        }
        languagePickerOnItemSelectedListener();
    }
    // endregion

    // 1.3: Init Event Listeners
    private void languagePickerOnItemSelectedListener() {
        user_setting_change_language.setOnItemSelectedListener((MaterialSpinner.OnItemSelectedListener<String>) (view1, position, id, item) -> {
            languagePickerOnItemSelectedHandler(item);
        });
    }

    // 1.4: Init Event Handlers
    private void languagePickerOnItemSelectedHandler(Object item) {
        if (item.equals("English")) {
            changeAppLanguage("en");
        } else if (item.equals("አማርኛ")) {
            changeAppLanguage("am");
        } else if (item.equals("Oromifa")) {
            changeAppLanguage("om");
        }
    }
    // endregion


    // endregion


    // region Change Language
    private void changeAppLanguage(String locale) {
        // region Save Selected Language
        LanguageSelector.saveAppLanguage(getApplicationContext(), locale);
        // endregion

        // region Change Language
        LanguageSelector.setLanguage(getApplicationContext());
        StateManager.setAppLanguage(locale);
        StateManager.setLanguageChanged(true);
        setLanguage(StateManager.getAppLanguage());
//        Toast.makeText(getApplicationContext(), "Current Lan: " + locale, Toast.LENGTH_LONG).show();
//        Lingver.getInstance().setLocale(getApplicationContext(), locale);
        // endregion
//        Toast.makeText(getApplicationContext(), getResources().getString(R.string.setting_restart_to_see_change), Toast.LENGTH_LONG).show();
        // region Refresh App
        // Intent refreshApp = new Intent(getActivity(), Intro.class);
        // startActivity(refreshApp);
        ProcessPhoenix.triggerRebirth(getApplicationContext());
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
        // endregion


    }


    // endregion
}
