package com.example.fibath.ui.agents;

import static com.example.fibath.classes.api.APIInterface.APP_VERSION;
import static com.example.fibath.classes.api.APIInterface.BASE_URL;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.fibath.R;
import com.example.fibath.databinding.ActivityMyAgentsBinding;
import com.example.fibath.classes.api.APIInterface;
import com.example.fibath.classes.model.AgentUser;
import com.example.fibath.classes.networking.communication.ServerCommunicator;
import com.example.fibath.classes.user.User;
import com.geniusforapp.fancydialog.FancyAlertDialog;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class MyAgents extends AppCompatActivity {
    private ActivityMyAgentsBinding binding;
    private UsersSearchAdapter searchAdapter;
    private ArrayList<AgentUser> agentList = new ArrayList<>();

    EditText agentName, agentPhone, agentEmail, agentPassword, agentConfirmPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyAgentsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        initUI();
    }

    // region Init UI
    private void initUI() {
        // region Search Bar
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("My Agents");
        binding.toolbar.setNavigationIcon(R.drawable.ic_left_arrow);
        binding.searchView.setVoiceSearch(true);
        binding.searchView.setCursorDrawable(R.drawable.ic_action_action_search);
        binding.searchView.setEllipsize(true);
//        binding.searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        // endregion

//        binding.myAgentsUsersList.setHasFixedSize(true);
//        binding.myAgentsUsersList.setLayoutManager(new LinearLayoutManager(this));
//
//        binding.myAgentsUsersList.removeAllViews();


        System.out.println("*************FOUND USERS*************");
        searchAdapter = new UsersSearchAdapter(MyAgents.this, agentList);
//        binding.myAgentsUsersList.setAdapter(searchAdapter);
        getAllChildAgents();

        initEventHandlers();
    }
    // endregion

    // region Init Event Handlers
    private void initEventHandlers() {
        // region Top Bar
        binding.toolbar.setNavigationOnClickListener(v -> finish());


//        binding.myAgentsBackButton.setOnClickListener(v -> {
//            onBackPressed();
//        });

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.agent_list_menu, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        MenuItem createNewAgentButton = menu.findItem(R.id.action_search_add_new_agent);

        binding.searchView.setMenuItem(item);
        // Search Box Text Changed Listener
        binding.searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                searchAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                System.out.println("Agent is typinggg");

                searchAdapter.getFilter().filter(newText);
                return false;
            }
        });

        // Search Box View Change Listener
        binding.searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
        // endregion
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search_add_new_agent) {
            MaterialDialog dialog = new MaterialDialog.Builder(MyAgents.this)
                    .customView(R.layout.dialog_create_new_agent, false)
                    .cancelable(true)
                    .autoDismiss(true)
                    .build();

            agentName = dialog.getCustomView().findViewById(R.id.dialogAgentFullName);
            agentPhone = dialog.getCustomView().findViewById(R.id.dialogAgentPhone);
            agentEmail = dialog.getCustomView().findViewById(R.id.dialogAgentEmail);
            agentPassword = dialog.getCustomView().findViewById(R.id.dialogAgentPassword);
            agentConfirmPassword = dialog.getCustomView().findViewById(R.id.dialogAgentConfirmPassword);
            Button btnCreateAgent = dialog.getCustomView().findViewById(R.id.dialogAgentCreateAgent);
            Button btnCancel = dialog.getCustomView().findViewById(R.id.dialogAgentCancel);
//agentName.addTextChangedListener(new TextValidator(agentName) {
//    @Override
//    public void validate(TextView textView, String text) {
//        if(text.equals("")||text.length()<4){
//            textView.setError("Name Should Not Be Less than 4 Character");
//return;
//        }
//    }
//});
            String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
            btnCreateAgent.setOnClickListener(v1 -> {
                if (agentName.getText().toString().length() < 4 || agentName.getText().toString().equals("")
                ) {

                    Toast.makeText(getApplicationContext(), "Please Fill Those Information", Toast.LENGTH_LONG).show();


                } else if (agentPhone.getText().toString().length() < 10 || agentPhone.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please Enter Valid Phone", Toast.LENGTH_LONG).show();


                }
//                else if (agentEmail.getText().toString().equals("") || !agentEmail.getText().toString().trim().matches(emailPattern)) {
//                    Toast.makeText(getApplicationContext(), "Please Enter Valid Email", Toast.LENGTH_LONG).show();
//
//
//                }
                else if (agentPassword.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please Enter Valid Password", Toast.LENGTH_LONG).show();
                } else if (agentPassword.getText().toString().length() < 4) {
                    Toast.makeText(getApplicationContext(), "Password Character Length Should Not Be Less Than 4", Toast.LENGTH_LONG).show();


                } else if (!agentConfirmPassword.getText().toString().equals(agentPassword.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Confirm Password Should Match Password", Toast.LENGTH_LONG).show();
                } else {
                    HashMap<String, String> agentData = new HashMap<>();

                    agentData.put("name", agentName.getText().toString());
//                    agentData.put("email", agentEmail.getText().toString());
                    agentData.put("phone", agentPhone.getText().toString());
                    agentData.put("password", agentPassword.getText().toString());
                    agentData.put("userType", "5eb1b4e50b65b13950b7bcca");
                    registerAgent(agentData);
                }
            });

            btnCancel.setOnClickListener(v1 -> {
                if (dialog.isShowing()) dialog.hide();
            });

            if (!dialog.isShowing()) dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    binding.searchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //region fetch Child Users
    public void getAllChildAgents() {


        APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
        myInterface.getChildAgents(User.getUserSessionToken(), APP_VERSION)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<ArrayList<AgentUser>>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Response<ArrayList<AgentUser>> responseTransactionHistory) {


                if (responseTransactionHistory.code() == 200) {
                    System.out.println("********************************************************");
                    // adding contacts to contacts list
                    agentList.clear();
                    agentList.addAll(responseTransactionHistory.body());

                    // refreshing recycler view
                    searchAdapter.notifyDataSetChanged();

//                    searchAdapter.add(responseTransactionHistory.body());


                } else if (responseTransactionHistory.code() == 400) {

                    Toast.makeText(getApplicationContext(), "You Don't Have Any Agents", Toast.LENGTH_SHORT).show();


                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.someting_went_worong), Toast.LENGTH_SHORT).show();
                    System.out.println("Error code is" + responseTransactionHistory.code());

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

    //region Register Users
    public void registerAgent(HashMap<String, String> agentData) {


        APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
        myInterface.registerAgent(User.getUserSessionToken(), APP_VERSION, agentData)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<AgentUser>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Response<AgentUser> responseTransactionHistory) {


                if (responseTransactionHistory.code() == 201) {
                    Toast.makeText(getApplicationContext(), "Agent Successfully Registered", Toast.LENGTH_SHORT).show();

                    agentName.setText("");
//                    agentData.put("email", agentEmail.getText().toString());
                    agentPhone.setText("");
                    agentPassword.setText("");

                } else if (responseTransactionHistory.code() == 400) {
                    System.out.println("error is"+responseTransactionHistory.body());

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

                    final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(MyAgents.this)
                            .setBackgroundColor(R.color.colorPrimaryWarning)
                            .setimageResource(R.drawable.ic_dialog_warning)
                            .setTextTitle("Error")
                            .setTitleColor(R.color.colorLightWhite)
                            .setTextSubTitle("User already exists.")
                            .setSubtitleColor(R.color.colorLightWhite)
                            .setBodyColor(R.color.red)
                            .setPositiveButtonText("Okay")
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
                    System.out.println("Error code is" + responseTransactionHistory.code());
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

    public abstract class TextValidator implements TextWatcher {
        private final TextView textView;

        public TextValidator(TextView textView) {
            this.textView = textView;
        }

        public abstract void validate(TextView textView, String text);

        @Override
        final public void afterTextChanged(Editable s) {
            String text = textView.getText().toString();
            validate(textView, text);
        }

        @Override
        final public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* Don't care */ }

        @Override
        final public void onTextChanged(CharSequence s, int start, int before, int count) { /* Don't care */ }
    }

}