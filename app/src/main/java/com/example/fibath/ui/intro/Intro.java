package com.example.fibath.ui.intro;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fibath.classes.user.User;
import com.example.fibath.ui.helpers.UIHelpers;
import com.example.fibath.ui.home.NewHome;
import com.example.fibath.ui.intro.fragments.Slide1Fragment;
import com.example.fibath.ui.intro.fragments.Slide2Fragment;
import com.example.fibath.ui.intro.fragments.Slide3Fragment;
import com.example.fibath.ui.intro.fragments.Slide4Fragment;
import com.example.fibath.ui.intro.fragments.Slide5Fragment;
import com.example.fibath.ui.login.Login;
import com.github.paolorotolo.appintro.AppIntro;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;


public class Intro extends AppIntro {

    boolean result_user_available = true;
    User user = new User();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        new IPDialog().show(getSupportFragmentManager(),"solo");
        user.loadUserSessionData(this);

        if (!User.isIsFirstTime() && User.getUserSessionToken() == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
        } else if (User.getUserSessionToken() != null) {
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
//            Intent intent = new Intent(getApplicationContext(), Home.class);
//            startActivity(intent);
        } else {
            initUI();
            requestPermission();
        }


    }


    public void requestPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.SEND_SMS)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }


    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Intro.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    // region ----- Initialize App -----

    // region Initialize UI
    private void initUI() {
        // region Set status bar transparent
        UIHelpers.makeWindowTransparent(this);
        // endregion
//        HashMap<String, RequestBody> map = new HashMap<>();
//        String str = "multipart/form-data";
//        map.put("token", RequestBody.create(MediaType.parse(str), App_TOKEN));
//        map.put(NotificationCompat.CATEGORY_EMAIL, RequestBody.create(MediaType.parse(str), User.getUserSessionEmail()));
//        map.put("password", RequestBody.create(MediaType.parse(str), User.getUserSessionPassword()));
//        result_user_available = user.checkUserSession(this,map);
//        if (!result_user_available) {
//            startActivity(new Intent(this, Login.class));
//        } else {
//            startActivity(new Intent(this, Home.class));
//        }

        System.out.println("user id" + User.getUserSessionId());
        System.out.println("user name" + User.getUserSessionUsername());

        System.out.println("email is" + User.getUserSessionEmail());
        System.out.println("password is" + User.getUserSessionPassword());
        initWelcomeSliders();
    }
    // endregion

    // region Initialize UI Components
    private void initWelcomeSliders() {
        addSlide(new Slide1Fragment());
        addSlide(new Slide2Fragment());
        addSlide(new Slide3Fragment());
        addSlide(new Slide4Fragment());
        addSlide(new Slide5Fragment());

        showSkipButton(true);
        // showPagerIndicator(false);

        // Override bar/separator color
        setBarColor(Color.parseColor("#00000000"));
        setSeparatorColor(Color.parseColor("#00000000"));

        // Hide Skip/Done button
//        showSkipButton(false);
//        showDoneButton(false);

//        setFadeAnimation(); // OR
//        setZoomAnimation(); // OR
//        setFlowAnimation(); // OR
//        setSlideOverAnimation(); // OR
//        setDepthAnimation(); // OR
    }
    // endregion

    // endregion

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        Intent intent = new Intent(this, Login.class); // Call the AppIntro java class
        startActivity(intent);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        Intent intent = new Intent(this, Login.class); // Call the AppIntro java class
        startActivity(intent);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
//    public void showPermissionRationale(final PermissionToken token) {
//        new AlertDialog.Builder(this).setTitle("Warning")
//                .setMessage("Hey")
//                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//                    @Override public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        token.cancelPermissionRequest();
//                    }
//                })
//                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        token.continuePermissionRequest();
//                    }
//                })
//                .setOnDismissListener(new DialogInterface.OnDismissListener() {
//                    @Override public void onDismiss(DialogInterface dialog) {
//                        token.cancelPermissionRequest();
//                    }
//                })
//                .show();
//    }

//    public void showPermissionGranted(String permission) {
//        TextView feedbackView = getFeedbackViewForPermission(permission);
//        feedbackView.setText(R.string.permission_granted_feedback);
//        feedbackView.setTextColor(ContextCompat.getColor(this, R.color.permission_granted));
//    }
//
//    public void showPermissionDenied(String permission, boolean isPermanentlyDenied) {
//        TextView feedbackView = getFeedbackViewForPermission(permission);
//        feedbackView.setText(isPermanentlyDenied ? R.string.permission_permanently_denied_feedback
//                : R.string.permission_denied_feedback);
//        feedbackView.setTextColor(ContextCompat.getColor(this, R.color.permission_denied));
//    }
}