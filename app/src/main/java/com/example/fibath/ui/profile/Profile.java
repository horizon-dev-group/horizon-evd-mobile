package com.example.fibath.ui.profile;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.example.fibath.R;import com.example.fibath.classes.api.APIInterface;

import com.example.fibath.classes.networking.Internet.CheckInternetConnection;
import com.example.fibath.classes.networking.communication.ServerCommunicator;
import com.example.fibath.classes.user.User;
import com.example.fibath.ui.home.NewHome;
import com.victor.loading.rotate.RotateLoading;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

import static com.example.fibath.classes.api.APIInterface.APP_VERSION;
import static com.example.fibath.classes.api.APIInterface.BASE_URL;

public class Profile extends AppCompatActivity {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }


    @BindView(R.id.profile_screen_profile_image)
    CircleImageView profileScreenProfileImage;
    @BindView(R.id.profile_screen_btn_edit)
    Button profileScreenEditButton;
    @BindView(R.id.profile_screen_text_name)
    TextView profileScreenTextName;
    @BindView(R.id.profile_screen_text_email)
    TextView profileScreenTextEmail;
    @BindView(R.id.profile_screen_text_location)
    TextView profileScreenTextLocation;
    @BindView(R.id.profile_screen_text_lock_fund)
    TextView profileScreenTextLockFund;
    @BindView(R.id.profile_screen_text_phone)
    TextView profileScreenTextPhone;
    @BindView(R.id.profile_screen_text_terminal_number)
    TextView profileScreenTextTerminalNumber;
    @BindView(R.id.profile_screen_text_top_name)
    TextView profileScreenTextTopName;
    @BindView(R.id.profile_screen_toolbar)
    Toolbar profileScreenToolbar;
    @BindView(R.id.activity_profile_newton_cradle_loading)
    RotateLoading profileScreenTNewtonCradleLoading;
    String applicationVersion;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        new CheckInternetConnection(this).checkConnection();
        ButterKnife.bind(this);
        profileScreenToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getApplicationContext().startActivity(new Intent(getApplicationContext(), Home.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//                getApplicationContext().startActivity(new Intent(getApplicationContext(), Voucher.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                getApplicationContext().startActivity(new Intent(getApplicationContext(), NewHome.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

            }
        });
        profileScreenEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), EditProfile.class);
//                startActivity(intent);
            }
        });
        initUI();
    }

    // 1.1:region Init UI
    private void initUI() {

//        profileScreenTNewtonCradleLoading.setLoadingColor(R.color.colorWhite);
        getProfileInfo();
    }

    private void getProfileInfo() {
        profileScreenTNewtonCradleLoading.start();

        HashMap<String, String> requestBody=new HashMap<>();
        APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
        myInterface.retailerInfo(User.getUserSessionToken(),APP_VERSION)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<com.example.fibath.classes.model.User>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Response<com.example.fibath.classes.model.User> responseUserInfo) {
//                Log.d("Email  is",responseUserInfo.body().getEmail());



                if (responseUserInfo.code()==200) {
                    profileScreenTNewtonCradleLoading.stop();
                    System.out.println("********************************************************");

                    profileScreenTextEmail.setText(responseUserInfo.body().getPhone());
                    profileScreenTextName.setText(responseUserInfo.body().getName());
                    profileScreenTextLocation.setText(responseUserInfo.body().getRegion());
//                    profileScreenTextLockFund.setText(responseLogin.getUserInfo().getLockfund()+".00 Birr");
                    profileScreenTextPhone.setText(responseUserInfo.body().getPhone());
                    profileScreenTextTerminalNumber.setText(String.valueOf(responseUserInfo.body().getPosNumber()));
                    profileScreenTextTopName.setText(responseUserInfo.body().getName());

                    System.out.println("********************************************************");


                } else {
                    profileScreenTNewtonCradleLoading.stop();

                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.someting_went_worong), Toast.LENGTH_SHORT).show();
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


    @Override
    protected void onResume() {
        super.onResume();
        new CheckInternetConnection(this).checkConnection();
    }
}
