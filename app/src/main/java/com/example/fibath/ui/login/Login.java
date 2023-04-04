package com.example.fibath.ui.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fibath.R;
import com.example.fibath.classes.state.StateManager;
import com.example.fibath.classes.user.User;
import com.example.fibath.ui.helpers.UIHelpers;
import com.example.fibath.ui.home.NewHome;
import com.victor.loading.rotate.RotateLoading;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

// public class Login extends LocalizationActivity {
public class Login extends AppCompatActivity {
    // region UI Elements
    @BindView(R.id.login_screen_btn_login)
    Button btnLogin;
    @BindView(R.id.activity_login_newton_cradle_loading)
    RotateLoading activityLoginNewtonCradleLoading;
    @BindView(R.id.login_page_user_id)
    EditText userEmail;
    @BindView(R.id.login_page_password)
    EditText userPassword;
    @BindView(R.id.login_text_view_progress)
    TextView progressTextView;
    // endregion

    User user = new User();

    // region On Create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initUI();
        initEventListeners();
    }
    // endregion

    // region Initialize UI
    private void initUI() {
        UIHelpers.makeWindowTransparent(this);
        user.loadUserSessionData(this);
        if (User.getUserSessionToken() != null) {
            SharedPreferences sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
//            Toast.makeText(getApplicationContext(),"HomePageMode: " + sharedPreferences.getString("HomePageMode", ""), Toast.LENGTH_LONG).show();
            if (sharedPreferences.getString("HomePageMode", "1").equals("1")) {
//                Intent intent = new Intent(getApplicationContext(), Voucher.class);
                Intent intent = new Intent(getApplicationContext(), NewHome.class);
                startActivity(intent);
            } else {
//                Intent intent = new Intent(getApplicationContext(), Home.class);
//                Intent intent = new Intent(getApplicationContext(), Voucher.class);
                Intent intent = new Intent(getApplicationContext(), NewHome.class);
                startActivity(intent);
            }
        }
    }
    // endregion

    // region Initialize Event Listeners
    private void initEventListeners() {
        btnLogin.setOnClickListener(view -> {
            btnLoginOnClickHandler();
        });
    }
    // endregion

    // region Login
    private void btnLoginOnClickHandler() {
        if (userEmail.getText().toString().equals("") || userPassword.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "please Enter Your Credential", Toast.LENGTH_SHORT).show();
        } else {
            HashMap<String, String> map = new HashMap<>();
            map.put("email", userEmail.getText().toString().trim());
            map.put("password", userPassword.getText().toString());
            StateManager.setPassword(userPassword.getText().toString());
            user.login(getApplicationContext(), map, activityLoginNewtonCradleLoading);
            progressTextView.setText("Loading....");
        }
    }
    // endregion

    public void startLoadingBar() {
        activityLoginNewtonCradleLoading.start();
    }

    public void stopLoadingBar() {
        activityLoginNewtonCradleLoading.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }
}
