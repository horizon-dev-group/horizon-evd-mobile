package com.example.fibath.ui.profile;

import static com.example.fibath.classes.api.APIInterface.APP_VERSION;
import static com.example.fibath.classes.api.APIInterface.BASE_URL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.airbnb.lottie.LottieAnimationView;
import com.example.fibath.R;import com.example.fibath.classes.api.APIInterface;
import com.example.fibath.classes.model.FundResponse;
import com.example.fibath.classes.networking.communication.ServerCommunicator;
import com.example.fibath.classes.user.User;
import com.example.fibath.ui.statement.adapters.statement.StatementAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class ProfileNew extends AppCompatActivity {
    //    private FirebaseRecyclerAdapter usersOnesReferenceFirebaseRecyclerAdapter;
    ArrayList<String> userIDList1stGen;
    ArrayList<String> userNameList1stGen;
    ArrayList<String> profileImageList1stGen;
    ArrayList<String> userStatusList1stGen;

    ArrayList<String> userIDList2ndGen;
    ArrayList<String> userNameList2ndGen;
    ArrayList<String> profileImageList2ndGen;
    ArrayList<String> userStatusList2ndGen;
//    UsersSearchAdapter userOnesAdapter, userTwosAdapter;

    // region Firebase Variables
//    private DatabaseReference userDatabase, usersOnesDatabaseReference, usersTwosDatabaseReference;
//    private ValueEventListener userListener, usersOnesReferenceListener, friendsListener;
    // endregion

    private LinearLayout personalInfo, experience, review;
    private TextView profileSponsoredBy, personalInfoBtn, experienceBtn, reviewBtn, name, profile_user_id, userFacebook, userTwitter, userInstagram, userLink4, userLink5, userLink6, profile_direct_signup_description, profile_indirect_signup_description, profile_edit, social_edit;
    private TextView profileGameStatsId, profileGameStatsWins, profileGameStatsLosses, profileGameStatsTotalMoves, profileGameStatsTotalCaptures;
    private TextView userAccountBalance;

    private CircleImageView image;
    //    private FABsMenu menu;
//    private TitleFAB button1, button2, button3, button4;
    private ImageView searchUsers, profile_back_button, profile_sponsored_by_icon, profile_logout_users, profileUserTemplateSelector;
    private ImageView profileNotTransDeduct;

    private String userID;

    private RecyclerView userOnesSignupsRecyclerView;

    private StatementAdapter statementAdapter;
    private RecyclerView transactionWithUserList;
    private List<FundResponse> statementList = new ArrayList<>();

    // region [Override] On Create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        userID = getIntent().getStringExtra("userID");
        initUI();
        initEventHandlers();
        // initCurrentUsersOnes();
        // initCurrentUserTwos();
        // initCurrentUserTwosAnother();
    }
    // endregion

    // region Init UI
    private void initUI() {
        // region Final Vars
        name = findViewById(R.id.profile_name);
        profile_user_id = findViewById(R.id.profile_user_id);
        // status = findViewById(R.id.profile_status);
        image = findViewById(R.id.profile_image);
        // menu = findViewById(R.id.profile_fabs_menu);

        profileNotTransDeduct = findViewById(R.id.profileNotTransDeduct);


        searchUsers = findViewById(R.id.profile_register_new_users);
        // endregion
        personalInfo = findViewById(R.id.personalinfo);
        experience = findViewById(R.id.experience);
        review = findViewById(R.id.review);
        personalInfoBtn = findViewById(R.id.users_list_count);
        experienceBtn = findViewById(R.id.experiencebtn);
        reviewBtn = findViewById(R.id.reviewbtn);
        profile_back_button = findViewById(R.id.profile_back_button);
        profile_sponsored_by_icon = findViewById(R.id.profile_sponsored_by_icon);

        profileSponsoredBy = findViewById(R.id.profile_sponsored_by);
        userAccountBalance = findViewById(R.id.userAccountBalance);
        // userOnesSignupsRecyclerView = findViewById(R.id.user_ones_signups_list);
        transactionWithUserList = findViewById(R.id.transactionWithUserList);
        profile_direct_signup_description = findViewById(R.id.profile_direct_signup_description);
        profile_indirect_signup_description = findViewById(R.id.profile_indirect_signup_description);
        profile_edit = findViewById(R.id.activity_profile_text_more);


        profile_logout_users = findViewById(R.id.profileNotTransDeduct);
        profileUserTemplateSelector = findViewById(R.id.profileUserTemplateSelector);

        // profileGameStatsId, profileGameStatsWins, profileGameStatsLosses, profileGameStatsTotalMoves, profileGameStatsTotalCaptures
        profileGameStatsId = findViewById(R.id.profileGameStatsId);
        profileGameStatsWins = findViewById(R.id.profileGameStatsWins);
        profileGameStatsLosses = findViewById(R.id.profileGameStatsLosses);
        profileGameStatsTotalMoves = findViewById(R.id.profileGameStatsTotalMoves);
        profileGameStatsTotalCaptures = findViewById(R.id.profileGameStatsTotalCaptures);

        userIDList1stGen = new ArrayList<>();
        userNameList1stGen = new ArrayList<>();
        profileImageList1stGen = new ArrayList<>();
        userStatusList1stGen = new ArrayList<>();

        userIDList2ndGen = new ArrayList<>();
        userNameList2ndGen = new ArrayList<>();
        profileImageList2ndGen = new ArrayList<>();
        userStatusList2ndGen = new ArrayList<>();

        /* making personal info visible */
        personalInfo.setVisibility(View.VISIBLE);
        experience.setVisibility(View.GONE);
        review.setVisibility(View.GONE);

        initRecyclerView();
        HashMap<String, String> map = new HashMap<>();
        map.put("receiver_id", userID);
        getTransactionHistory(map);
        getProfileInfo();
    }
    // endregion

    // region Init Event Handlers
    private void initEventHandlers() {
//        personalInfoBtn.setOnClickListener(v -> {
//            personalInfo.setVisibility(View.VISIBLE);
//            experience.setVisibility(View.GONE);
//            review.setVisibility(View.GONE);
//            personalInfoBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
//            experienceBtn.setTextColor(getResources().getColor(R.color.main_gray));
//            reviewBtn.setTextColor(getResources().getColor(R.color.main_gray));
//        });
//
//        experienceBtn.setOnClickListener(v -> {
//            personalInfo.setVisibility(View.GONE);
//            experience.setVisibility(View.VISIBLE);
//            review.setVisibility(View.GONE);
//            personalInfoBtn.setTextColor(getResources().getColor(R.color.main_gray));
//            experienceBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
//            reviewBtn.setTextColor(getResources().getColor(R.color.main_gray));
//        });
//
//        reviewBtn.setOnClickListener(v -> {
//            personalInfo.setVisibility(View.GONE);
//            experience.setVisibility(View.GONE);
//            review.setVisibility(View.VISIBLE);
//            personalInfoBtn.setTextColor(getResources().getColor(R.color.main_gray));
//            experienceBtn.setTextColor(getResources().getColor(R.color.main_gray));
//            reviewBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
//        });

//        profile_logout_users.setOnClickListener(view -> {
//            CapiUserManager.loadUserData(this);
//            DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(CapiUserManager.getCurrentUserID());
//            Map<String, Object> logoutUpdateLink = new HashMap<>();
//            logoutUpdateLink.put("logged_in", "false");
//            userDatabase.updateChildren(logoutUpdateLink).addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    Toast.makeText(this, "You Are Logged Out", Toast.LENGTH_SHORT).show();
//                    CapiUserManager.removeUserData(getApplicationContext());
//                    startActivity(new Intent(ProfileNew.this, Login.class));
//                    // finish();
//                } else {
//                    Toast.makeText(this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
//                }
//            });
//        });

        searchUsers.setOnClickListener(view -> {
//            startActivity(new Intent(ProfileNew.this, SearchUser.class));
        });

//        profile_indirect_signup_description.setOnClickListener(view -> {
//            userTwosAdapter = new UsersSearchAdapter(ProfileNew.this, userIDList2ndGen, profileImageList2ndGen, userNameList2ndGen, userStatusList2ndGen);
//            userTwosSignupsRecyclerView.setAdapter(userTwosAdapter);
//        });

//        profileSponsoredBy.setOnClickListener(v -> {
//            Intent userProfileIntent = new Intent(ProfileNew.this, Profile.class);
//            userProfileIntent.putExtra("userID", profileSponsoredBy.getText().toString());
//            startActivity(userProfileIntent);
//        });

        profile_edit.setOnClickListener(v -> {
            Intent balanceTransferIntent=new Intent(getApplicationContext(),BalanceTransferHistory.class);
            balanceTransferIntent.putExtra("userID",userID);
                    startActivity(balanceTransferIntent);

            // Toast.makeText(this, "Edit Text Pressd", Toast.LENGTH_SHORT).show();
        });

//        social_edit.setOnClickListener(v -> {
//            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileNew.this);
//            builder.setTitle("Enter your Social Links:");
//
//            View mView = ProfileNew.this.getLayoutInflater().inflate(R.layout.social_links_dialog, null);
//
//            final EditText userFB = mView.findViewById(R.id.social_links_dialog_facebook);
//            final EditText userTwi = mView.findViewById(R.id.social_links_dialog_twitter);
//            final EditText userInsta = mView.findViewById(R.id.social_links_dialog_instagram);
//            final EditText userLink4 = mView.findViewById(R.id.social_links_dialog_4);
//            final EditText userLink5 = mView.findViewById(R.id.social_links_dialog_5);
//            final EditText userLink6 = mView.findViewById(R.id.social_links_dialog_6);
//
//            builder.setPositiveButton("Update", (dialogInterface, i) -> {
//                final String newUserFacebook = userFB.getText().toString();
//                final String newUserTwitter = userTwi.getText().toString();
//                final String newUserInstagram = userInsta.getText().toString();
//                final String newUserLink4 = userLink4.getText().toString();
//                final String newUserLink5 = userLink5.getText().toString();
//                final String newUserLink6 = userLink6.getText().toString();
//
//
//                // Toast.makeText(ProfileNew.this, "Length can't exceed no more than 255 characters.", Toast.LENGTH_LONG).show();
//                // dialogInterface.dismiss();
//
//                // Updating on status on user data
//
//                // userDatabase.child("userFirstName").setValue(newStatus).addOnCompleteListener(task -> {
//                //     if (task.isSuccessful()) {
//                //         Toast.makeText(ProfileNew.this, "Status updated", Toast.LENGTH_SHORT).show();
//                //     } else {
//                //         Toast.makeText(ProfileNew.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
//                //     }
//                // });
//
//                Map<String, Object> socialLinksUpdateMap = new HashMap<>();
//                boolean shouldWeUpdate = false;
//                if (newUserFacebook.length() > 2 && newUserFacebook.length() < 256) {
//                    socialLinksUpdateMap.put("facebook", newUserFacebook);
//                    shouldWeUpdate = true;
//                }
//
//                if (newUserTwitter.length() > 2 && newUserTwitter.length() < 256) {
//                    socialLinksUpdateMap.put("instagram", newUserTwitter);
//                    shouldWeUpdate = true;
//                }
//
//                if (newUserInstagram.length() > 2 && newUserInstagram.length() < 256) {
//                    socialLinksUpdateMap.put("twitter", newUserInstagram);
//                    shouldWeUpdate = true;
//                }
//
//                if (newUserLink4.length() > 2 && newUserLink4.length() < 256) {
//                    socialLinksUpdateMap.put("userLink4", newUserLink4);
//                    shouldWeUpdate = true;
//                }
//
//                if (newUserLink5.length() > 2 && newUserLink5.length() < 256) {
//                    socialLinksUpdateMap.put("userLink5", newUserLink5);
//                    shouldWeUpdate = true;
//                }
//
//                if (newUserLink6.length() > 2 && newUserLink6.length() < 256) {
//                    socialLinksUpdateMap.put("userLink6", newUserLink6);
//                    shouldWeUpdate = true;
//                }
//
//                if (shouldWeUpdate) {
//                    userDatabase.updateChildren(socialLinksUpdateMap).addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(ProfileNew.this, "Social Links updated", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(ProfileNew.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    });
//                }
//            });
//
//            builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
//
//            builder.setView(mView);
//            AlertDialog dialog = builder.create();
//            dialog.show();
//        });

        profile_back_button.setOnClickListener(view -> {
            onBackPressed();
        });

        profileUserTemplateSelector.setOnClickListener(v -> {

        });

        profileNotTransDeduct.setOnClickListener(v -> {
            MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .customView(R.layout.dialog_revert_transaction, false)
                    .cancelable(true)
                    .autoDismiss(true)
                    .build();
            TextView dialogRevertTransactionMaxDeductible = dialog.getCustomView().findViewById(R.id.dialogRevertTransactionMaxDeductible);
            TextView dialogRevertTransactionUserBalance = dialog.getCustomView().findViewById(R.id.dialogRevertTransactionUserBalance);
            EditText dialogRevertTransactionAmountToDeduct = dialog.getCustomView().findViewById(R.id.dialogRevertTransactionAmountToDeduct);
            Button dialogRevertTransactionCancel = dialog.getCustomView().findViewById(R.id.dialogRevertTransactionCancel);
            Button dialogRevertTransactionDeductTransaction = dialog.getCustomView().findViewById(R.id.dialogRevertTransactionDeductTransaction);

            dialogRevertTransactionCancel.setOnClickListener(v1 -> {
                if (dialog.isShowing()) dialog.hide();
            });

            dialogRevertTransactionDeductTransaction.setOnClickListener(v1 -> {
                HashMap<String, Object> body = new HashMap<>();
                body.put("amount", Double.parseDouble(dialogRevertTransactionAmountToDeduct.getText().toString()));
                body.put("user_id", userID);
                deductUserBalance(body);

                if (dialog.isShowing()) dialog.hide();
            });

            if (!dialog.isShowing()) dialog.show();
        });
    }
    // endregion

    // region [Override] On Start
    protected void onStart() {
        super.onStart();


    }
    // endregion

    // region [Override] On Stop
    @Override
    protected void onStop() {
        super.onStop();
    }
    // endregion

    // region Revert Transaction
    private void deductUserBalance(HashMap<String, Object> body) {
        APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
        myInterface.deductUserBalance(User.getUserSessionToken(), APP_VERSION, body)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<FundResponse>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Response<FundResponse> responseTransactionHistory) {
                if (responseTransactionHistory.code() == 201) {
//                    Toast.makeText(context, "Deducted Successfully.", Toast.LENGTH_SHORT).show();
                    MaterialDialog dialog = new MaterialDialog.Builder(ProfileNew.this)
                            .customView(R.layout.dialog_lottie_message, false)
                            .cancelable(true)
                            .autoDismiss(true)
                            .build();
                    assert dialog.getCustomView() != null;
                    LottieAnimationView dialogLottieAnimation = dialog.getCustomView().findViewById(R.id.dialogLottieAnimation);
                    TextView dialogLottieTitle = dialog.getCustomView().findViewById(R.id.dialogLottieTitle);
                    TextView dialogLottieDescription = dialog.getCustomView().findViewById(R.id.dialogLottieDescription);
                    dialogLottieAnimation.setAnimation("success-dialog.json");
                    dialogLottieTitle.setText(getResources().getString(R.string.dialog_success));
                    dialogLottieDescription.setText(getResources().getString(R.string.fund_deduction_successful));
                    if (!dialog.isShowing()) dialog.show();
                } else if (responseTransactionHistory.code() == 400) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.fund_deduction_unable_to_deduct), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.someting_went_worong), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("has---" + e);
            }

            @Override
            public void onComplete() {
// Stopping swipe refresh
//                myAgentsUsersListContainer.setRefreshing(false);
            }
        });
    }
    // endregion

    // region Init RecyclerView
    private void initRecyclerView() {
        statementAdapter = new StatementAdapter(getApplicationContext(), statementList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        transactionWithUserList.setLayoutManager(linearLayoutManager);
        transactionWithUserList.setHasFixedSize(false);
        transactionWithUserList.setAdapter(statementAdapter);
        HashMap<String, String> map = new HashMap<>();
        map.put("receiver_id", userID);
        getTransactionHistory(map);
    }

    //endregion

    //region get Transaction History
    private void getTransactionHistory(HashMap<String, String> receiverId) {
//        activityUserStatementNewtonCradleLoading.start();

        APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
        myInterface.getChildUserTransaction(User.getUserSessionToken(), APP_VERSION, receiverId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<ArrayList<FundResponse>>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Response<ArrayList<FundResponse>> responseStatements) {
                if (responseStatements.code() == 200) {
//                    activityUserStatementNewtonCradleLoading.stop();
                    double totalTransactions = 0;
                    for (int i = 0; i < responseStatements.body().size(); i++) {
                        System.out.println("^^^^^^^^^^^^size is^^^^^"+responseStatements.body().size());
                        System.out.println("^^^^^^^^^^^^Amunt is^^^^^"+responseStatements.body().get(i).getTransactionAmount());

                        totalTransactions += Double.parseDouble(responseStatements.body().get(i).getTransactionAmount());
                    }
                    profile_direct_signup_description.setText(String.valueOf(totalTransactions) + " " + getResources().getString(R.string.default_currency_short));

                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Preferences", MODE_PRIVATE);
                    assert sharedPreferences != null;
                    boolean showDeductedTransactions = sharedPreferences.getString("ShowDeductedTransactions", "1").equals("1");

                    if (!showDeductedTransactions) {
                        ArrayList<FundResponse> finalStatements = new ArrayList<>();
                        for (int i = 0; i < responseStatements.body().size(); i++) {
                            if (!responseStatements.body().get(i).getTransactionType().equals("Deducted") || !responseStatements.body().get(i).getTransactionType().equals("Non Trans Deduct")) {
                                finalStatements.add(responseStatements.body().get(i));
                            }
                        }
                        statementAdapter.add(finalStatements, "DEBIT");
                    } else {
                        statementAdapter.add(responseStatements.body(), "DEBIT");
                    }
                } else if (responseStatements.code() == 400) {
//                    activityUserStatementNewtonCradleLoading.stop();
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.user_profile_no_transaction), Toast.LENGTH_SHORT).show();
                } else {
//                    activityUserStatementNewtonCradleLoading.stop();
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

    //region get Profile info
    private void getProfileInfo() {
        HashMap<String, String> requestBody = new HashMap<>();
        requestBody.put("user_id", userID);
        APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
        myInterface.getUserProfile(User.getUserSessionToken(), APP_VERSION, requestBody)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<com.example.fibath.classes.model.AgentUser>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Response<com.example.fibath.classes.model.AgentUser> responseUserInfo) {
//                Log.d("Email  is",responseUserInfo.body().getEmail());
                if (responseUserInfo.code() == 200) {
                    profileSponsoredBy.setText(responseUserInfo.body().getPhone());
                    name.setText(responseUserInfo.body().getName());
                    userAccountBalance.setText(responseUserInfo.body().getAccount().getBalance().toString() + " Br.");
//                    profileScreenTextLockFund.setText(responseLogin.getUserInfo().getLockfund()+".00 Birr");
//                    profileScreenTextPhone.setText(responseUserInfo.body().getPhone());
//                    profileScreenTextTerminalNumber.setText(String.valueOf(responseUserInfo.body().getPosNumber()));
//                    profileScreenTextTopName.setText(responseUserInfo.body().getName());
                } else {
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

//    // region [Override] On Activity Result
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == 1 && resultCode == RESULT_OK) {
//            Uri url = data.getData();
//            System.out.println("*******************************");
//            System.out.println("Image URL: " + url);
//            System.out.println("*******************************");
//
//            // Uploading selected picture
//            StorageReference file = FirebaseStorage.getInstance().getReference().child("profile_images").child(userID + ".jpg");
//            file.putFile(Objects.requireNonNull(url)).addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    String imageUrl = Objects.requireNonNull(task.getResult().getDownloadUrl()).toString();
//
//                    // Updating image on user data
//                    userDatabase.child("image").setValue(imageUrl).addOnCompleteListener(task1 -> {
//                        if (task1.isSuccessful()) {
//                            Toast.makeText(ProfileNew.this, "Picture updated", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Log.d("Profile", "updateImage listener failed: " + Objects.requireNonNull(task1.getException()).getMessage());
//                        }
//                    });
//                } else {
//                    Log.d("Profile", "uploadImage listener failed: " + Objects.requireNonNull(task.getException()).getMessage());
//                }
//            });
//        }
//    }
//    // endregion

//    // region [Override] On Back Pressed
//    @Override
//    public void onBackPressed() {
//        if (menu.isExpanded()) {
//            menu.collapse();
//        } else {
//            super.onBackPressed();
//        }
//    }
//    // endregion

//    // region Init My Profile
//    public void initializeCurrentUserProfile() {
//        menu.setVisibility(View.VISIBLE);
//        reviewBtn.setVisibility(View.VISIBLE);
//        // region Fab Button Initializations
//        if (button1 != null) {
//            menu.removeButton(button1);
//        }
//
//        if (button2 != null) {
//            menu.removeButton(button2);
//        }
//
//        if (button3 != null) {
//            menu.removeButton(button3);
//        }
//
//        if (button4 != null) {
//            menu.removeButton(button4);
//        }
//        // endregion
//
//        // region Button To Edit User Name
//        button1 = new TitleFAB(ProfileNew.this);
//        button1.setTitle("Change Name");
//        button1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
//        button1.setRippleColor(getResources().getColor(R.color.capitipalism_accent));
//        button1.setImageResource(R.drawable.ic_edit_white_24dp);
//        button1.setOnClickListener(view -> {
//            menu.collapse();
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileNew.this);
//            builder.setTitle("Enter your Name:");
//
//            View mView = ProfileNew.this.getLayoutInflater().inflate(R.layout.name_dialog, null);
//
//            final EditText firstName = mView.findViewById(R.id.name_dialog_first_name);
//            final EditText lastName = mView.findViewById(R.id.name_dialog_last_name);
//
//            builder.setPositiveButton("Update", (dialogInterface, i) -> {
//                final String newFirstName = firstName.getText().toString();
//                final String newLastName = lastName.getText().toString();
//
//                if (newFirstName.length() < 3 || newFirstName.length() > 254 || newLastName.length() < 3 || newLastName.length() > 254) {
//                    Toast.makeText(ProfileNew.this, "Length can't exceed no more than 254 characters.", Toast.LENGTH_LONG).show();
//                    dialogInterface.dismiss();
//                } else {
//                    // Updating on status on user data
//
//                    // userDatabase.child("userFirstName").setValue(newStatus).addOnCompleteListener(task -> {
//                    //     if (task.isSuccessful()) {
//                    //         Toast.makeText(ProfileNew.this, "Status updated", Toast.LENGTH_SHORT).show();
//                    //     } else {
//                    //         Toast.makeText(ProfileNew.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
//                    //     }
//                    // });
//
//                    Map<String, Object> userNameUpdateMap = new HashMap<>();
//                    userNameUpdateMap.put("userFirstName", newFirstName);
//                    userNameUpdateMap.put("userLastName", newLastName);
//
//                    userDatabase.updateChildren(userNameUpdateMap).addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(ProfileNew.this, "User Name updated", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(ProfileNew.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    });
//                }
//            });
//
//            builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
//
//            builder.setView(mView);
//            AlertDialog dialog = builder.create();
//            dialog.show();
//        });
//        menu.addButton(button1);
//        // endregion
//
//        // region Button To Change Profile Picture
//        button2 = new TitleFAB(ProfileNew.this);
//        button2.setTitle("Change Image");
//        button2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
//        button2.setRippleColor(getResources().getColor(R.color.capitipalism_accent));
//        button2.setImageResource(R.drawable.ic_image_white_24dp);
//        button2.setOnClickListener(view -> {
//            Intent gallery = new Intent();
//            gallery.setType("image/*");
//            gallery.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult(Intent.createChooser(gallery, "Select Image"), 1);
//
//            menu.collapse();
//        });
//        menu.addButton(button2);
//        // endregion
//
//        // region Button To Change User Status (About Info)
//        button3 = new TitleFAB(ProfileNew.this);
//        button3.setTitle("Change Status");
//        button3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
//        button3.setRippleColor(getResources().getColor(R.color.capitipalism_accent));
//        button3.setImageResource(R.drawable.ic_edit_white_24dp);
//        button3.setOnClickListener(view -> {
//            menu.collapse();
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileNew.this);
//            builder.setTitle("Enter your new Status:");
//
//            View mView = ProfileNew.this.getLayoutInflater().inflate(R.layout.status_dialog, null);
//
//            final EditText tmp = mView.findViewById(R.id.status_text);
//
//            builder.setPositiveButton("Update", (dialogInterface, i) -> {
//                final String newStatus = tmp.getText().toString();
//
//                if (newStatus.length() < 1 || newStatus.length() > 1000) {
//                    Toast.makeText(ProfileNew.this, "Status must be between 1-1000 characters.", Toast.LENGTH_LONG).show();
//                    dialogInterface.dismiss();
//                } else {
//
//                    // Updating on status on user data
//                    userDatabase.child("status").setValue(newStatus).addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(ProfileNew.this, "Status updated", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(ProfileNew.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    });
//                }
//            });
//
//            builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
//
//            builder.setView(mView);
//            AlertDialog dialog = builder.create();
//            dialog.show();
//        });
//        menu.addButton(button3);
//        // endregion
//
//        // region Button To Edit Social Links
//        button4 = new TitleFAB(ProfileNew.this);
//        button4.setTitle("Change Social Links");
//        button4.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
//        button4.setRippleColor(getResources().getColor(R.color.capitipalism_accent));
//        button4.setImageResource(R.drawable.ic_edit_white_24dp);
//        button4.setOnClickListener(view -> {
//            menu.collapse();
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileNew.this);
//            builder.setTitle("Enter your Social Links:");
//
//            View mView = ProfileNew.this.getLayoutInflater().inflate(R.layout.social_links_dialog, null);
//
//            final EditText userFB = mView.findViewById(R.id.social_links_dialog_facebook);
//            final EditText userTwi = mView.findViewById(R.id.social_links_dialog_twitter);
//            final EditText userInsta = mView.findViewById(R.id.social_links_dialog_instagram);
//            final EditText userLink4 = mView.findViewById(R.id.social_links_dialog_4);
//            final EditText userLink5 = mView.findViewById(R.id.social_links_dialog_5);
//            final EditText userLink6 = mView.findViewById(R.id.social_links_dialog_6);
//
//            builder.setPositiveButton("Update", (dialogInterface, i) -> {
//                final String newUserFacebook = userFB.getText().toString();
//                final String newUserTwitter = userTwi.getText().toString();
//                final String newUserInstagram = userInsta.getText().toString();
//                final String newUserLink4 = userLink4.getText().toString();
//                final String newUserLink5 = userLink5.getText().toString();
//                final String newUserLink6 = userLink6.getText().toString();
//
//
//                // Toast.makeText(ProfileNew.this, "Length can't exceed no more than 255 characters.", Toast.LENGTH_LONG).show();
//                // dialogInterface.dismiss();
//
//                // Updating on status on user data
//
//                // userDatabase.child("userFirstName").setValue(newStatus).addOnCompleteListener(task -> {
//                //     if (task.isSuccessful()) {
//                //         Toast.makeText(ProfileNew.this, "Status updated", Toast.LENGTH_SHORT).show();
//                //     } else {
//                //         Toast.makeText(ProfileNew.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
//                //     }
//                // });
//
//                Map<String, Object> socialLinksUpdateMap = new HashMap<>();
//                boolean shouldWeUpdate = false;
//                if (newUserFacebook.length() > 2 && newUserFacebook.length() < 256) {
//                    socialLinksUpdateMap.put("facebook", newUserFacebook);
//                    shouldWeUpdate = true;
//                }
//
//                if (newUserTwitter.length() > 2 && newUserTwitter.length() < 256) {
//                    socialLinksUpdateMap.put("instagram", newUserTwitter);
//                    shouldWeUpdate = true;
//                }
//
//                if (newUserInstagram.length() > 2 && newUserInstagram.length() < 256) {
//                    socialLinksUpdateMap.put("twitter", newUserInstagram);
//                    shouldWeUpdate = true;
//                }
//
//                if (newUserLink4.length() > 2 && newUserLink4.length() < 256) {
//                    socialLinksUpdateMap.put("userLink4", newUserLink4);
//                    shouldWeUpdate = true;
//                }
//
//                if (newUserLink5.length() > 2 && newUserLink5.length() < 256) {
//                    socialLinksUpdateMap.put("userLink5", newUserLink5);
//                    shouldWeUpdate = true;
//                }
//
//                if (newUserLink6.length() > 2 && newUserLink6.length() < 256) {
//                    socialLinksUpdateMap.put("userLink6", newUserLink6);
//                    shouldWeUpdate = true;
//                }
//
//                if (shouldWeUpdate) {
//                    userDatabase.updateChildren(socialLinksUpdateMap).addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(ProfileNew.this, "Social Links updated", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(ProfileNew.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    });
//                }
//            });
//
//            builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
//
//            builder.setView(mView);
//            AlertDialog dialog = builder.create();
//            dialog.show();
//        });
//        menu.addButton(button4);
//        // endregion
//    }
//    // endregion
//
//    // region TRY LOADING USERS SPONSORS
//    private void initCurrentUsersOnes() {
//        usersOnesDatabaseReference = FirebaseDatabase
//                .getInstance()
//                .getReference()
//                .child("Users")
//        ;
//
//        userOnesSignupsRecyclerView.setHasFixedSize(true);
//        userOnesSignupsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        // userOnesSignupsRecyclerView
//
//        usersOnesDatabaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                userNameList1stGen.clear();
//                userOnesSignupsRecyclerView.removeAllViews();
//
//                int counter = 0;
//
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    String userSponsorID = snapshot.child("userSponsorID").getValue(String.class);
//
//                    String uID = snapshot.getKey();
//                    String userName = snapshot.child("userFirstName").getValue(String.class) + " " + snapshot.child("userLastName").getValue(String.class) + " - ID: " + uID;
//                    String profilePic = snapshot.child("image").getValue(String.class);
//                    String status = snapshot.child("status").getValue(String.class);
//
//                    if (Objects.requireNonNull(userSponsorID).compareToIgnoreCase(userID) == 0) {
//                        userIDList1stGen.add(uID);
//                        userNameList1stGen.add(userName);
//                        profileImageList1stGen.add(profilePic);
//                        userStatusList1stGen.add(status);
//                        counter++;
//                    }
//
//                    if (counter == 25) {
//                        break;
//                    }
//                }
//
//                profile_direct_signup_description.setText(userID + " Registerd " + counter + " Users.");
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//        userOnesAdapter = new UsersSearchAdapter(ProfileNew.this, userIDList1stGen, profileImageList1stGen, userNameList1stGen, userStatusList1stGen);
//        userOnesSignupsRecyclerView.setAdapter(userOnesAdapter);
//        // friendsDatabase.keepSynced(true); // For offline use
//    }
//
//    private void initCurrentUserTwos() {
//        usersTwosDatabaseReference = FirebaseDatabase
//                .getInstance()
//                .getReference()
//                .child("Users");
//
//        userTwosSignupsRecyclerView.setHasFixedSize(true);
//        userTwosSignupsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        // userTwosSignupsRecyclerView
//
//        userNameList2ndGen.clear();
//        userTwosSignupsRecyclerView.removeAllViews();
//
//        final int[] firstGenCounter = {0};
//        final int[] secondGenCounter = {0};
//
//        usersTwosDatabaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                System.out.println("\n\n########################################################\n\n");
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    String firstGenUserSponsorID = snapshot.child("userSponsorID").getValue(String.class);
//                    String firstGenUserID = snapshot.getKey();
//                    // System.out.println("First Gen Spon ID: " + firstGenUserSponsorID + " firstGenUserID: " + firstGenUserID);
//
//                    if (Objects.requireNonNull(firstGenUserSponsorID).compareToIgnoreCase(userID) == 0) {
//                        usersTwosDatabaseReference.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot1) {
//                                for (DataSnapshot snapshot1 : dataSnapshot1.getChildren()) {
//                                    String secondGenUserSponsorID = snapshot1.child("userSponsorID").getValue(String.class);
//
//                                    String secondGenUserID = snapshot1.getKey();
//                                    String secondGenUserName = snapshot1.child("userFirstName").getValue(String.class) + " " + snapshot1.child("userLastName").getValue(String.class) + " - ID: " + secondGenUserID;
//                                    String secondGenProfilePic = snapshot1.child("image").getValue(String.class);
//                                    String secondGenStatus = snapshot1.child("status").getValue(String.class);
//
//                                    if (Objects.requireNonNull(secondGenUserSponsorID).compareToIgnoreCase(firstGenUserID) == 0) {
//
//                                        System.out.println("\n\nFound " + secondGenUserID);
//                                        userIDList2ndGen.add(secondGenUserID);
//                                        userNameList2ndGen.add(secondGenUserName);
//                                        profileImageList2ndGen.add(secondGenProfilePic);
//                                        userStatusList2ndGen.add(secondGenStatus);
//                                        secondGenCounter[0]++;
//                                    }
//
//                                    // System.out.println("COUNTER: " + secondGenCounter[0]);
//
//                                    if (secondGenCounter[0] == 50) {
//                                        break;
//                                    }
//                                }
//
//                                // profile_indirect_signup_description.setText(userID + " Registerd " + secondGenCounter[0] + " Users.");
//                                profile_indirect_signup_description.setText("Users Registered By " + firstGenUserSponsorID + " Registerd " + secondGenCounter[0] + " Users.");
//                                userTwosAdapter = new UsersSearchAdapter(ProfileNew.this, userIDList2ndGen, profileImageList2ndGen, userNameList2ndGen, userStatusList2ndGen);
//                                userTwosSignupsRecyclerView.setAdapter(userTwosAdapter);
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
//
//                        firstGenCounter[0]++;
//                    }
//
//
//                    if (firstGenCounter[0] == 25) {
//                        break;
//                    }
//                }
//
//                System.out.println("\n\n########################################################\n\n");
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//        // Timer myTimer = new Timer();
//        // myTimer.schedule(new TimerTask() {
//        //     @Override
//        //     public void run() {
//        //         runOnUiThread(() -> {
//        //             userTwosAdapter = new UsersSearchAdapter(ProfileNew.this, userIDList2ndGen, profileImageList2ndGen, userNameList2ndGen, userStatusList2ndGen);
//        //             userTwosSignupsRecyclerView.setAdapter(userTwosAdapter);
//        //         });
//        //
//        //     }
//        //
//        // }, 0, 1000);
//
//
//        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^SET ADAPTER^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
//        // friendsDatabase.keepSynced(true); // For offline use
//    }
//    // endregion
}
