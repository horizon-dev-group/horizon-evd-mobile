package com.example.fibath.ui.password;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fibath.R;import com.example.fibath.classes.user.User;
import com.victor.loading.rotate.RotateLoading;

import java.util.HashMap;

public class ChangePassword extends AppCompatActivity {
    EditText old_password, new_password, confirm_password;
    Button change_password;
    RotateLoading changePasswordProgress;
    TextView printerStatus;
    Switch printer_option;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_change_password);

        initUI();
        initEventListeners();
    }

    private void initEventListeners() {

    }

    private void initUI() {
        old_password = findViewById(R.id.activity_change_password_old_password);
        new_password = findViewById(R.id.activity_change_password_new_password);
        confirm_password = findViewById(R.id.activity_change_password_confirm_password);
        change_password = findViewById(R.id.activity_change_password_btn_change_password);
        changePasswordProgress = findViewById(R.id.change_password_progress);
        printer_option = findViewById(R.id.printer_switch);
        printerStatus = findViewById(R.id.printer_status);
//        change_password.setVisibility(View.INVISIBLE);
        confirm_password.addTextChangedListener(new ValidationTextWatcher(confirm_password));
        new_password.addTextChangedListener(new ValidationTextWatcher(new_password));
        old_password.addTextChangedListener(new ValidationTextWatcher(old_password));

        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateOldPassword()) {
                    return;
                }
                if (!validateNewPassword()) {
                    return;
                }
                if (!validateConfirmPassword()) {
                    return;
                }
//                Toast.makeText(ChangePassword.this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                changePassword();
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        boolean isSmallPrinter = sharedPreferences.getBoolean("user_print_type", true);
        System.out.println("@@@@IS Bluettoth@@@" + isSmallPrinter);
        if (isSmallPrinter) {
            printer_option.setChecked(true);

            sharedPreferences.edit().putBoolean("user_print_type", true).commit();
        } else {
            printer_option.setChecked(false);
            printerStatus.setText("Big Printer Selected");

        }
//        if(User.getUserPrinterType()){
//            printer_option.setChecked(true);
//        }else{
//        }

        printer_option.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sharedPreferences.edit().putBoolean("user_print_type", true).commit();
                    System.out.println("@@@@IS Bluettoth@@@" + isSmallPrinter);
                    printerStatus.setText("Small Printer Selected");
                } else {

                    sharedPreferences.edit().putBoolean("user_print_type", false).commit();
                    printerStatus.setText("Big Printer Selected");
                    System.out.println("@@@@IS Bluettoth@@@" + isSmallPrinter);


                }
            }
        });
    }


    private boolean validateOldPassword() {
        if (old_password.getText().toString().equals("")) {
            old_password.setError("Enter Your Password");
            return false;
        }
        return true;
    }

    private boolean validateNewPassword() {
        if (new_password.getText().toString().equals("")) {
            new_password.setError("Enter Your New  Password");
            return false;
        } else if (new_password.getText().length() < 7) {
            new_password.setError(" Your Password Should Be At least 7 character ");
            return false;

        }
        return true;
    }

    private boolean validateConfirmPassword() {
        if (confirm_password.getText().toString().equals("")) {
            confirm_password.setError("Confirm Your   Password");
            return false;
        } else if (!confirm_password.getText().toString().equals(new_password.getText().toString())) {
            confirm_password.setError(" Your Confirm Password Should Be The Same ");
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
//        user.changePassword(getApplicationContext(), map);
    }
}
