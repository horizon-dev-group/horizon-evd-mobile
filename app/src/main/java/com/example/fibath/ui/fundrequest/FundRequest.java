package com.example.fibath.ui.fundrequest;

import static com.example.fibath.classes.api.APIInterface.APP_VERSION;
import static com.example.fibath.classes.api.APIInterface.BASE_URL;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.airbnb.lottie.LottieAnimationView;
import com.akexorcist.localizationactivity.LocalizationActivity;
import com.example.fibath.R;
import com.example.fibath.databinding.FundRequestBinding;
import com.example.fibath.classes.api.APIInterface;
import com.example.fibath.classes.model.FundRequestListResponse;
import com.example.fibath.classes.model.FundRequestResponse;
import com.example.fibath.classes.networking.communication.ServerCommunicator;
import com.example.fibath.classes.user.User;
import com.example.fibath.ui.helpers.UIHelpers;
import com.geniusforapp.fancydialog.FancyAlertDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class FundRequest extends LocalizationActivity implements AdapterView.OnItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {
    FundRequestBinding binding;
    View view;
    EditText dialogFundRequestAmount;
    Spinner dialogFundRequestType;
    ImageView dialogFundRequestTypeIcon;
    EditText dialogFundRequestBankName;
    ImageView dialogFundRequestBankNameIcon;
    EditText dialogFundRequestTransactionRef;
    ImageView dialogFundRequestTransactionRefIcon;
    EditText dialogFundRequestCRV;
    ImageView dialogFundRequestCRVIcon;
    EditText dialogFundRequestRemark;

    private SwipeRefreshLayout fundRequestHistoryListContainer;
    private RecyclerView fundRequestHistoryList;
    private FundRequestsAdapter fundRequestsAdapter;
    private ArrayList<FundRequestListResponse> fundRequestListResponses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FundRequestBinding.inflate(getLayoutInflater());
        view = binding.getRoot();
        try {
            setContentView(view);
            initUI();
        } catch (Exception exception) {

        }
    }

    // region Init UI
    private void initUI() {
//        binding.toolbar.setNavigationIcon(R.drawable.ic_fragment_home_dashboard_hamberger_menu);
        UIHelpers.makeWindowTransparent(this);

        fundRequestHistoryListContainer = view.findViewById(R.id.fundRequestHistoryListContainer);
        fundRequestHistoryList = view.findViewById(R.id.fundRequestHistoryList);

        fundRequestHistoryListContainer.setOnRefreshListener(this);
        fundRequestHistoryListContainer.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        fundRequestHistoryListContainer.post(() -> {
            fundRequestHistoryListContainer.setRefreshing(true);

            // Fetching data from server
            getAllSentRequests();
        });

        fundRequestHistoryList.setHasFixedSize(true);
        fundRequestHistoryList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        fundRequestHistoryList.removeAllViews();
        fundRequestsAdapter = new FundRequestsAdapter(getApplicationContext(), fundRequestListResponses);
        fundRequestHistoryList.setAdapter(fundRequestsAdapter);
        initEventHandlers();
    }
    // endregion

    // region Init Event Handlers
    private void initEventHandlers() {
        // region Top Bar
//        binding.fundRequestNewFundRequest.setOnClickListener(v -> NewHome.drawer.openMenu());


        binding.fundRequestNewFundRequest.setOnClickListener(v -> {
            MaterialDialog dialog = new MaterialDialog.Builder(FundRequest.this)
                    .customView(R.layout.dialog_new_fund_request, false)
                    .cancelable(true)
                    .autoDismiss(true)
                    .build();

            assert dialog.getCustomView() != null;
            dialogFundRequestAmount = dialog.getCustomView().findViewById(R.id.dialogFundRequestAmount);
            dialogFundRequestType = dialog.getCustomView().findViewById(R.id.dialogFundRequestType);
            dialogFundRequestTypeIcon = dialog.getCustomView().findViewById(R.id.dialogFundRequestTypeIcon);

            dialogFundRequestBankName = dialog.getCustomView().findViewById(R.id.dialogFundRequestBankName);
            dialogFundRequestBankNameIcon = dialog.getCustomView().findViewById(R.id.dialogFundRequestBankNameIcon);

            dialogFundRequestTransactionRef = dialog.getCustomView().findViewById(R.id.dialogFundRequestTransactionRef);
            dialogFundRequestTransactionRefIcon = dialog.getCustomView().findViewById(R.id.dialogFundRequestTransactionRefIcon);

            dialogFundRequestCRV = dialog.getCustomView().findViewById(R.id.dialogFundRequestCRV);
            dialogFundRequestCRVIcon = dialog.getCustomView().findViewById(R.id.dialogFundRequestCRVIcon);

            dialogFundRequestRemark = dialog.getCustomView().findViewById(R.id.dialogFundRequestRemark);

            // Spinner click listener
            dialogFundRequestType.setOnItemSelectedListener(this);

            // Spinner Drop down elements
            List<String> categories = new ArrayList<>();
            categories.add(getResources().getString(R.string.dialog_fund_option_bank));
            categories.add(getResources().getString(R.string.dialog_fund_option_cash));

            // Creating adapter for spinner
            ArrayAdapter<String> spinnerDataAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, categories);

            // Drop down layout style - list view with radio button
            spinnerDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // attaching data adapter to spinner
            dialogFundRequestType.setAdapter(spinnerDataAdapter);

            Button dialogFundRequestRequest = dialog.getCustomView().findViewById(R.id.dialogFundRequestRequest);
            Button dialogFundRequestCancel = dialog.getCustomView().findViewById(R.id.dialogFundRequestCancel);
//agentName.addTextChangedListener(new TextValidator(agentName) {
//    @Override
//    public void validate(TextView textView, String text) {
//        if(text.equals("")||text.length()<4){
//            textView.setError("Name Should Not Be Less than 4 Character");
//return;
//        }
//    }
//});
//            String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
            dialogFundRequestRequest.setOnClickListener(v1 -> {
                boolean errorExists = false;
                if (dialogFundRequestAmount.getText().toString().equals("")) {
                    errorExists = true;
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.amount_should_not_be_empty), Toast.LENGTH_LONG).show();
                } else {
                    if (Double.parseDouble(dialogFundRequestAmount.getText().toString()) < 0) {
                        errorExists = true;
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.dialog_fund_amount_cannot_be_negative), Toast.LENGTH_LONG).show();
                    } else if (Double.parseDouble(dialogFundRequestAmount.getText().toString()) > 25000000) {
                        errorExists = true;
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.dialog_fund_amount_cannot_exceed_25000000), Toast.LENGTH_LONG).show();
                    }
                }

                if (dialogFundRequestType.getSelectedItem().toString().equals(getResources().getString(R.string.dialog_fund_option_bank))) {
                    if (dialogFundRequestBankName.getText().toString().equals("")) {
                        errorExists = true;
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.dialog_fund_amount_cannot_be_empty), Toast.LENGTH_LONG).show();
                    }

                    if (dialogFundRequestTransactionRef.getText().toString().length() < 3) {
                        errorExists = true;
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.dialog_fund_trans_ref_num_has_to_be_3_chars), Toast.LENGTH_LONG).show();
                    }
                } else if (dialogFundRequestType.getSelectedItem().toString().equals(getResources().getString(R.string.dialog_fund_option_cash))) {
                    if (dialogFundRequestCRV.getText().toString().length() < 3) {
                        errorExists = true;
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.dialog_fund_crv_num_has_to_be_3_chars), Toast.LENGTH_LONG).show();
                    }
                }

                if (dialogFundRequestRemark.getText().toString().length() < 5) {
                    errorExists = true;
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.dialog_fund_remark_has_to_be_5_chars), Toast.LENGTH_LONG).show();
                }

                if (!errorExists) {
                    HashMap<String, String> fundRequestData = new HashMap<>();
                    fundRequestData.put("fund_amount", dialogFundRequestAmount.getText().toString());
                    fundRequestData.put("payment_mode", dialogFundRequestType.getSelectedItem().toString());
                    if (dialogFundRequestType.getSelectedItem().toString().equals(getResources().getString(R.string.dialog_fund_option_bank))) {
                        fundRequestData.put("bank_name", dialogFundRequestBankName.getText().toString());
                        fundRequestData.put("bank_transaction_reference_no", dialogFundRequestTransactionRef.getText().toString());
                    } else if (dialogFundRequestType.getSelectedItem().toString().equals(getResources().getString(R.string.dialog_fund_option_cash))) {
                        fundRequestData.put("crv_no", dialogFundRequestCRV.getText().toString());
                    }
                    fundRequestData.put("remark", dialogFundRequestRemark.getText().toString());
                    sndFundRequest(fundRequestData);
                }
            });

            dialogFundRequestCancel.setOnClickListener(v1 -> {
                if (dialog.isShowing()) dialog.hide();
            });

            if (!dialog.isShowing()) dialog.show();
        });

//        binding.vouchersMenuTransfer.setOnClickListener(v -> {
//            final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(this)
//                    .setBackgroundColor(R.color.colorPrimaryWarning)
//                    .setimageResource(R.drawable.ic_dialog_warning)
//                    .setTextTitle("Transfer Warning")
//                    .setTitleColor(R.color.colorLightWhite)
//                    .setTextSubTitle("Are you sure you want to transfer " + binding.vouchersMenuAmount.getText().toString() + " Birr to Some Name.")
//                    .setSubtitleColor(R.color.colorLightWhite)
//                    .setBodyColor(R.color.red)
//                    .setPositiveButtonText("Yes")
//                    .setPositiveColor(R.color.colorLightWhite)
//                    .setOnPositiveClicked((view1, dialog) -> {
//                        dialog.dismiss();
//                    })
//                    .setNegativeButtonText("No")
//                    .setNegativeColor(R.color.colorLightWhite)
//                    .setOnNegativeClicked((view1, dialog) -> {
//                        dialog.dismiss();
//                    })
//                    .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
//                    .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
//                    .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
//                    .build();
//            alert.show();
//        });
//
//        binding.vouchersMenuAmount.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start,
//                                          int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (isNumeric(binding.vouchersMenuAmount.getText().toString())) {
//                    if (Double.parseDouble(binding.vouchersMenuAmount.getText().toString()) > 5000) {
//                        binding.vouchersMenuTotalAmountToTransfer.setTextColor(Color.RED);
//                        binding.vouchersMenuTotalAmountToTransfer.setText(binding.vouchersMenuAmount.getText().toString() + " Br.");
//                    } else {
//                        binding.vouchersMenuTotalAmountToTransfer.setTextColor(Color.WHITE);
//                        binding.vouchersMenuTotalAmountToTransfer.setText(binding.vouchersMenuAmount.getText().toString() + " Br.");
//                    }
//                } else {
//                    binding.vouchersMenuTotalAmountToTransfer.setText("-");
//                }
//            }
//        });
    }
    // endregion

    //region Send req
    public void sndFundRequest(HashMap<String, String> agentData) {
        APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
        myInterface.fundRequest(User.getUserSessionToken(), APP_VERSION, agentData)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<FundRequestResponse>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Response<FundRequestResponse> fundRequestResponse) {
                if (fundRequestResponse.code() == 201) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.dialog_fund_request_sent), Toast.LENGTH_SHORT).show();
                    dialogFundRequestAmount.setText("");
                    dialogFundRequestBankName.setText("");
                    dialogFundRequestTransactionRef.setText("");
                    dialogFundRequestCRV.setText("");
                    dialogFundRequestRemark.setText("");

                    MaterialDialog dialog = new MaterialDialog.Builder(FundRequest.this)
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
                    dialogLottieDescription.setText(getResources().getString(R.string.dialog_fund_request_sent_title));
                    if (!dialog.isShowing()) dialog.show();
                    getAllSentRequests();
                } else if (fundRequestResponse.code() == 400) {
                    System.out.println("error is" + fundRequestResponse.body());

//                    Toast.makeText(getApplicationContext(), "Unable To Register Agents", Toast.LENGTH_SHORT).show();

//                    final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(MyAgents.this)
//                            .setBackgroundColor(R.color.colorPrimary)
//                            .setimageResource(R.drawable.ic_question_mark)
//                            .setTextTitle("Error")
//                            .setTitleColor(R.color.colorLightWhite)
//                            .setTextSubTitle("User already exists.")
//                            .setSubtitleColor(R.color.colorLightWhite)
//                            .setBodyColor(R.color.red)
//                            .setPositiveButtonText("Yes")
//                            .setNegativeButtonText("No")
//                            .setOnNegativeClicked((view12, dialog) -> dialog.dismiss())
//                            .setPositiveColor(R.color.colorLightWhite)
//                            .setOnPositiveClicked((view1, dialog) -> {
//                                dialog.dismiss();
//                            })
//                            .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
//                            .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
//                            .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
//                            .build();
//                    alert.show();

                    final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(FundRequest.this)
                            .setBackgroundColor(R.color.colorPrimaryWarning)
                            .setimageResource(R.drawable.ic_dialog_warning)
                            .setTextTitle(getResources().getString(R.string.dialog_error))
                            .setTitleColor(R.color.colorLightWhite)
                            .setTextSubTitle(getResources().getString(R.string.dialog_fund_request_unable_to_create_request))
                            .setSubtitleColor(R.color.colorLightWhite)
                            .setBodyColor(R.color.red)
                            .setPositiveButtonText(getResources().getString(R.string.dialog_okay))
                            .setPositiveColor(R.color.colorLightWhite)
                            .setOnPositiveClicked((view1, dialog) -> {
                                dialog.dismiss();
                            })
                            .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
                            .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                            .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
                            .build();
                    alert.show();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.someting_went_worong), Toast.LENGTH_SHORT).show();
                    System.out.println("Error code is" + fundRequestResponse.code());
                }
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("has" + e.toString());
            }

            @Override
            public void onComplete() {

            }
        });
    }


    //endregion

    // region Get all sent requests
    public void getAllSentRequests() {
// Showing refresh animation before making http call
        fundRequestHistoryListContainer.setRefreshing(true);

        APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
        myInterface.getSentFundRequests(User.getUserSessionToken(), APP_VERSION)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<ArrayList<FundRequestListResponse>>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Response<ArrayList<FundRequestListResponse>> allFundRequestsResponse) {
                if (allFundRequestsResponse.code() == 200) {
                    // adding contacts to contacts list
                    fundRequestListResponses.clear();
                    fundRequestListResponses.addAll(allFundRequestsResponse.body());
//                    myAgentsTotalUsersCount.setText(String.valueOf(responseTransactionHistory.body().size()) + " Agents");
//                    UsersSearchAdapter.agentListCopy.clear();
//                    UsersSearchAdapter.agentListCopy.addAll(responseTransactionHistory.body());

                    // refreshing recycler view
                    fundRequestsAdapter.notifyDataSetChanged();

//                    searchAdapter.add(responseTransactionHistory.body());
                } else if (allFundRequestsResponse.code() == 400) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.dialog_fund_request_you_dont_have_any_requests), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.someting_went_worong), Toast.LENGTH_SHORT).show();
                    System.out.println("Error code is" + allFundRequestsResponse.code());
                }
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("has" + e);
            }

            @Override
            public void onComplete() {
                // Stopping swipe refresh
                fundRequestHistoryListContainer.setRefreshing(false);
            }
        });
    }
    // endregion

    //    region SPinner Handlers
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fund_request_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        MenuItem createNewAgentButton = menu.findItem(R.id.action_search_add_new_agent);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search_add_new_agent) {
//            MaterialDialog dialog = new MaterialDialog.Builder(context)
//                    .customView(R.layout.dialog_create_new_agent, false)
//                    .cancelable(true)
//                    .autoDismiss(true)
//                    .build();
//
//            assert dialog.getCustomView() != null;
//            agentName = dialog.getCustomView().findViewById(R.id.dialogAgentFullName);
//            agentPhone = dialog.getCustomView().findViewById(R.id.dialogAgentPhone);
////            agentEmail = dialog.getCustomView().findViewById(R.id.dialogAgentEmail);
//            agentPassword = dialog.getCustomView().findViewById(R.id.dialogAgentPassword);
//            agentConfirmPassword = dialog.getCustomView().findViewById(R.id.dialogAgentConfirmPassword);
//            Button btnCreateAgent = dialog.getCustomView().findViewById(R.id.dialogAgentCreateAgent);
//            Button btnCancel = dialog.getCustomView().findViewById(R.id.dialogAgentCancel);
////agentName.addTextChangedListener(new TextValidator(agentName) {
////    @Override
////    public void validate(TextView textView, String text) {
////        if(text.equals("")||text.length()<4){
////            textView.setError("Name Should Not Be Less than 4 Character");
////return;
////        }
////    }
////});
////            String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
//            btnCreateAgent.setOnClickListener(v1 -> {
//                if (agentName.getText().toString().length() < 4 || agentName.getText().toString().equals("")) {
//
//                    Toast.makeText(context, "Name is required and has to be at least 4 characters.", Toast.LENGTH_LONG).show();
//
//
//                } else if (agentPhone.getText().toString().length() < 10 || agentPhone.getText().toString().equals("")) {
//                    Toast.makeText(context, "Please Enter Valid Phone", Toast.LENGTH_LONG).show();
//
//
//                }
////                else if (agentEmail.getText().toString().equals("") || !agentEmail.getText().toString().trim().matches(emailPattern)) {
////                    Toast.makeText(getApplicationContext(), "Please Enter Valid Email", Toast.LENGTH_LONG).show();
////
////
////                }
//                else if (agentPassword.getText().toString().equals("")) {
//                    Toast.makeText(context, "Please Enter Valid Password", Toast.LENGTH_LONG).show();
//                } else if (agentPassword.getText().toString().length() < 4) {
//                    Toast.makeText(context, "Password Character Length Should Not Be Less Than 4", Toast.LENGTH_LONG).show();
//
//
//                } else if (!agentConfirmPassword.getText().toString().equals(agentPassword.getText().toString())) {
//                    Toast.makeText(context, "Confirm Password Should Match Password", Toast.LENGTH_LONG).show();
//                } else {
//                    HashMap<String, String> agentData = new HashMap<>();
//
//                    agentData.put("name", agentName.getText().toString());
////                    agentData.put("email", agentEmail.getText().toString());
//                    agentData.put("phone", agentPhone.getText().toString());
//                    agentData.put("password", agentPassword.getText().toString());
//                    agentData.put("userType", "5eb1b4e50b65b13950b7bcca");
//                    registerAgent(agentData);
//                }
//            });
//
//            btnCancel.setOnClickListener(v1 -> {
//                if (dialog.isShowing()) dialog.hide();
//            });
//
//            if (!dialog.isShowing()) dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        if (item.equals(getResources().getString(R.string.dialog_fund_option_bank))) {
            dialogFundRequestBankName.setVisibility(View.VISIBLE);
            dialogFundRequestBankNameIcon.setVisibility(View.VISIBLE);
            dialogFundRequestTransactionRef.setVisibility(View.VISIBLE);
            dialogFundRequestTransactionRefIcon.setVisibility(View.VISIBLE);
            dialogFundRequestCRV.setVisibility(View.GONE);
            dialogFundRequestCRVIcon.setVisibility(View.GONE);
            dialogFundRequestTypeIcon.setImageResource(R.drawable.ic_bank_svgrepo_com);
        } else if (item.equals(getResources().getString(R.string.dialog_fund_option_cash))) {
            dialogFundRequestBankName.setVisibility(View.GONE);
            dialogFundRequestBankNameIcon.setVisibility(View.GONE);
            dialogFundRequestTransactionRef.setVisibility(View.GONE);
            dialogFundRequestTransactionRefIcon.setVisibility(View.GONE);
            dialogFundRequestCRV.setVisibility(View.VISIBLE);
            dialogFundRequestCRVIcon.setVisibility(View.VISIBLE);
            dialogFundRequestTypeIcon.setImageResource(R.drawable.ic_cash_svgrepo_com);
        }
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
    //    endregion

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {

        // Fetching data from server
        getAllSentRequests();
    }
}