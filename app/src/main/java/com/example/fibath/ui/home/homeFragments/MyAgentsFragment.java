package com.example.fibath.ui.home.homeFragments;

import static com.example.fibath.classes.api.APIInterface.APP_VERSION;
import static com.example.fibath.classes.api.APIInterface.BASE_URL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.fibath.R;
import com.example.fibath.databinding.ActivityMyAgentsBinding;
import com.example.fibath.classes.api.APIInterface;
import com.example.fibath.classes.model.AgentUser;
import com.example.fibath.classes.networking.communication.ServerCommunicator;
import com.example.fibath.classes.user.User;
import com.example.fibath.ui.agents.UsersSearchAdapter;
import com.example.fibath.ui.home.NewHome;
import com.geniusforapp.fancydialog.FancyAlertDialog;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class MyAgentsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private ActivityMyAgentsBinding binding;
    private UsersSearchAdapter searchAdapter;
    private ArrayList<AgentUser> agentList = new ArrayList<>();

    EditText agentName, agentPhone, agentEmail, agentPassword, agentConfirmPassword;
    View view;
    Context context;
    Activity activity;
    private SwipeRefreshLayout myAgentsUsersListContainer;
    private RecyclerView myAgentsUsersList;
    TextView myAgentsTotalUsersCountTitle, myAgentsTotalUsersCount;

    public MyAgentsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ActivityMyAgentsBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        initUI();
        return view;
    }

    // region Init UI
    private void initUI() {
        myAgentsUsersListContainer = view.findViewById(R.id.myAgentsUsersListContainer);
        myAgentsUsersList = view.findViewById(R.id.myAgentsUsersList);
        myAgentsTotalUsersCountTitle = view.findViewById(R.id.myAgentsTotalUsersCountTitle);
        myAgentsTotalUsersCount = view.findViewById(R.id.myAgentsTotalUsersCount);
        // SwipeRefreshLayout
//        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        myAgentsUsersListContainer.setOnRefreshListener(this);
        myAgentsUsersListContainer.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        myAgentsUsersListContainer.post(() -> {
            myAgentsUsersListContainer.setRefreshing(true);

            // Fetching data from server
            getAllChildAgents();
        });

        setHasOptionsMenu(true);
        context = requireActivity();
        activity = requireActivity();
        // region Search Bar
        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.my_agnets_page_title));
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.toolbar.setNavigationIcon(R.drawable.ic_fragment_home_dashboard_hamberger_menu);
        binding.searchView.setVoiceSearch(true);
        binding.searchView.setHint(getResources().getString(R.string.my_agents_search_hint));
        binding.searchView.setCursorDrawable(R.drawable.ic_action_action_search);
        binding.searchView.setEllipsize(true);
//        binding.searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        // endregion
        myAgentsUsersList.setHasFixedSize(true);
        myAgentsUsersList.setLayoutManager(new LinearLayoutManager(context));
        myAgentsUsersList.removeAllViews();
        searchAdapter = new UsersSearchAdapter(context, agentList);
        myAgentsUsersList.setAdapter(searchAdapter);
        getAllChildAgents();

        initEventHandlers();
    }
    // endregion

    // region Init Event Handlers
    private void initEventHandlers() {
        // region Top Bar
        binding.toolbar.setNavigationOnClickListener(v -> NewHome.drawer.openMenu());


//        binding.myAgentsBackButton.setOnClickListener(v -> {
//            NewHome.drawer.openMenu();
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.agent_list_menu, menu);
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
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search_add_new_agent) {
            MaterialDialog dialog = new MaterialDialog.Builder(context)
                    .customView(R.layout.dialog_create_new_agent, false)
                    .cancelable(true)
                    .autoDismiss(true)
                    .build();

            assert dialog.getCustomView() != null;
            agentName = dialog.getCustomView().findViewById(R.id.dialogAgentFullName);
            agentPhone = dialog.getCustomView().findViewById(R.id.dialogAgentPhone);
//            agentEmail = dialog.getCustomView().findViewById(R.id.dialogAgentEmail);
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
//            String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
            btnCreateAgent.setOnClickListener(v1 -> {
                if (agentName.getText().toString().length() < 4 || agentName.getText().toString().equals("")) {
                    Toast.makeText(context, getResources().getString(R.string.my_agnets_name_required), Toast.LENGTH_LONG).show();
                } else if (agentPhone.getText().toString().length() < 10 || agentPhone.getText().toString().equals("")) {
                    Toast.makeText(context, getResources().getString(R.string.my_agnets_enter_valid_phone), Toast.LENGTH_LONG).show();
                }
//                else if (agentEmail.getText().toString().equals("") || !agentEmail.getText().toString().trim().matches(emailPattern)) {
//                    Toast.makeText(getApplicationContext(), "Please Enter Valid Email", Toast.LENGTH_LONG).show();
//
//
//                }
                else if (agentPassword.getText().toString().equals("")) {
                    Toast.makeText(context, getResources().getString(R.string.my_agnets_enter_valid_password), Toast.LENGTH_LONG).show();
                } else if (agentPassword.getText().toString().length() < 4) {
                    Toast.makeText(context, getResources().getString(R.string.my_agnets_enter_valid_password_length), Toast.LENGTH_LONG).show();


                } else if (!agentConfirmPassword.getText().toString().equals(agentPassword.getText().toString())) {
                    Toast.makeText(context, getResources().getString(R.string.my_agnets_password_should_match), Toast.LENGTH_LONG).show();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == activity.RESULT_OK) {
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

    // region fetch Child Users
    public void getAllChildAgents() {
// Showing refresh animation before making http call
        myAgentsUsersListContainer.setRefreshing(true);

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
                    // adding contacts to contacts list
                    agentList.clear();
                    agentList.addAll(responseTransactionHistory.body());
                    myAgentsTotalUsersCount.setText(responseTransactionHistory.body().size() + " " + getResources().getString(R.string.my_agnets_agents));
//                    UsersSearchAdapter.agentListCopy.clear();
//                    UsersSearchAdapter.agentListCopy.addAll(responseTransactionHistory.body());

                    // refreshing recycler view
                    searchAdapter.notifyDataSetChanged();

//                    searchAdapter.add(responseTransactionHistory.body());


                } else if (responseTransactionHistory.code() == 400) {
                    Toast.makeText(context, getResources().getString(R.string.my_agnets_you_dont_have_agents), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, getResources().getString(R.string.someting_went_worong), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("has" + e);
            }

            @Override
            public void onComplete() {
// Stopping swipe refresh
                myAgentsUsersListContainer.setRefreshing(false);
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
                    Toast.makeText(context, getResources().getString(R.string.my_agnets_agent_registered), Toast.LENGTH_SHORT).show();
                    agentName.setText("");
//                    agentData.put("email", agentEmail.getText().toString());
                    agentPhone.setText("");
                    agentPassword.setText("");
                    agentConfirmPassword.setText("");
                    getAllChildAgents();
                } else if (responseTransactionHistory.code() == 400) {
                    System.out.println("error is" + responseTransactionHistory.body());

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

                    final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(context)
                            .setBackgroundColor(R.color.colorPrimaryWarning)
                            .setimageResource(R.drawable.ic_dialog_warning)
                            .setTextTitle(getResources().getString(R.string.dialog_error))
                            .setTitleColor(R.color.colorLightWhite)
                            .setTextSubTitle(getResources().getString(R.string.my_agnets_user_exists))
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
                    Toast.makeText(context, getResources().getString(R.string.someting_went_worong), Toast.LENGTH_SHORT).show();
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

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {

        // Fetching data from server
        getAllChildAgents();
    }

}
