package com.example.fibath.ui.home.homeFragments;

import static com.example.fibath.classes.api.APIInterface.APP_VERSION;
import static com.example.fibath.classes.api.APIInterface.BASE_URL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.fibath.R;
import com.example.fibath.databinding.FundTransactionTabbedLayoutBinding;
import com.example.fibath.classes.api.APIInterface;
import com.example.fibath.classes.model.AccountResponse;
import com.example.fibath.classes.model.TransactionReportResponse;
import com.example.fibath.classes.networking.communication.ServerCommunicator;
import com.example.fibath.classes.state.StateManager;
import com.example.fibath.classes.user.User;
import com.example.fibath.ui.fundrequest.FundRequest;
import com.example.fibath.ui.home.NewHome;
import com.example.fibath.ui.statement.FundTransactionAdapter;
import com.example.fibath.ui.transfer.MoneyTransfer;

import java.text.DecimalFormat;
import java.util.HashMap;

import io.ak1.BubbleTabBar;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class BalanceTransferHistoryFragment extends Fragment {
    private FundTransactionTabbedLayoutBinding binding;
//    private FundTransactionBodyBinding bodyBinding;
//    private CompCollapsibleLayoutHeaderBinding headerBinding;

    View view;
    Context context;
    Activity activity;

    // region UI Elements
    ViewPager viewPager;
    BubbleTabBar fundTransactionNavigation;
    TextView fundTransactionTodayReceivedAmount, fundTransactionTodaySentAmount, fundTransactionBalanceTitle, fundTransactionBalance;
//    AnimatedTabLayout transactionTypeChangerTab;
    // endregion

    public BalanceTransferHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FundTransactionTabbedLayoutBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        initUI(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
//        Toast.makeText(getContext(), "Transfer Fragment Resumed", Toast.LENGTH_LONG).show();
    }

    // region Init UI
    private void initUI(View view) {
//        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.fundTransactionToolbar);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Fund Transfers");
//        binding.fundTransactionToolbar.setNavigationIcon(R.drawable.ic_fragment_home_dashboard_hamberger_menu);
//        binding.fundTransactionSearchView.setVoiceSearch(true);
//        binding.fundTransactionSearchView.setCursorDrawable(R.drawable.ic_action_action_search);
//        binding.fundTransactionSearchView.setEllipsize(true);

        binding.fundTransactionBackButton.setOnClickListener(v -> {
            NewHome.drawer.openMenu();
        });

        binding.fundTransactionNewTransferButton.setOnClickListener(v -> {
            requireActivity().startActivity(new Intent(getActivity(), MoneyTransfer.class));
        });

        binding.fundTransactionNewFundRequest.setOnClickListener(v -> {
            requireActivity().startActivity(new Intent(getActivity(), FundRequest.class));
        });

        viewPager = view.findViewById(R.id.viewPager);
        fundTransactionNavigation = view.findViewById(R.id.fundTransactionNavigation);
        fundTransactionTodaySentAmount = view.findViewById(R.id.fundTransactionTodaySentAmount);
        fundTransactionTodayReceivedAmount = view.findViewById(R.id.fundTransactionTodayReceivedAmount);
        fundTransactionBalanceTitle = view.findViewById(R.id.fundTransactionBalanceTitle);
        fundTransactionBalance = view.findViewById(R.id.fundTransactionBalance);
        fundTransactionTodayReceivedAmount = view.findViewById(R.id.fundTransactionTodayReceivedAmount);
//        transactionTypeChangerTab = view.findViewById(R.id.transactionTypeChangerTab);
//        viewPager.setBackgroundColor(Color.parseColor("#ff0000"));
//        context = view.getContext();
//        activity = (Activity) view.getContext();
//        headerBinding.tabLayout.addTab(headerBinding.tabLayout.newTab().setText("Sent"));
//        headerBinding.tabLayout.addTab(headerBinding.tabLayout.newTab().setText("Received"));
//        headerBinding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final FundTransactionAdapter adapter = new FundTransactionAdapter(context, getFragmentManager(), 2);
        viewPager.setAdapter(adapter);
        try {
            initEventHandlers();
            getTodaySentTransactionReport();
            getTodayReceivedTransactionReport();
            getUserBalance(context);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

//        transactionTypeChangerTab.setupViewPager(viewPager);
//        atl.setTabChangeListener(object : AnimatedTabLayout.OnChangeListener {
//            override fun onChanged(position: Int) {
//            }
//        })

//        bodyBinding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(headerBinding.tabLayout));
//
//        headerBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                bodyBinding.viewPager.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
    }
    // endregion

    // region Init Event Handlers
    private void initEventHandlers() {
        // region Top Bar
//        binding.toolbar.setNavigationOnClickListener(v -> NewHome.drawer.openMenu());

        // region Fund Transaction Navigation
        fundTransactionNavigation.addBubbleListener(id -> {
            switch (id) {
                case R.id.fundTransactionReceivedTransactions:
                    viewPager.setCurrentItem(0);
                    return;
                case R.id.fundTransactionSentTransactions:
                    viewPager.setCurrentItem(1);
                    return;
                default:
                    viewPager.setCurrentItem(0);
            }
        });
        // endregion

        // region ViewPager Event Handler
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                fundTransactionNavigation.setSelected(position, false);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        // endregion

    }
    // endregion

    // region fetch Child Users fundTransactionTodaySentAmount
    public void getTodaySentTransactionReport() {
        HashMap<String, String> agentData = new HashMap<>();
        agentData.put("user_id", User.getUserSessionId());
        APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
        myInterface.getTotalBalanceSentToday(User.getUserSessionToken(), APP_VERSION, agentData)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<TransactionReportResponse>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Response<TransactionReportResponse> todaySentTransaction) {
                if (todaySentTransaction.code() == 200) {
                    Double finalBalance = 0d;
                    try {
                        finalBalance = Double.parseDouble(String.valueOf(todaySentTransaction.body().getTransactionAmount()));
                    } catch (Exception e) {
                        Toast.makeText(context, context.getResources().getString(R.string.unable_to_get_balance), Toast.LENGTH_SHORT).show();
                    }
                    fundTransactionTodaySentAmount.setText(String.format(getResources().getString(R.string.fund_transaction_sent_today_value), String.format("%.2f", finalBalance)));
                } else if (todaySentTransaction.code() == 400) {
                    fundTransactionTodaySentAmount.setText(getResources().getString(R.string.fund_transaction_sent_today_empty));
                } else {
                    fundTransactionTodaySentAmount.setText(getResources().getString(R.string.fund_transaction_sent_today_empty));
                }
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("has" + e);
            }

            @Override
            public void onComplete() {
                // Stopping swipe refresh
                // myAgentsUsersListContainer.setRefreshing(false);
            }
        });
    }

    public void getTodayReceivedTransactionReport() {
        HashMap<String, String> agentData = new HashMap<>();
        agentData.put("user_id", User.getUserSessionId());
        APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
        myInterface.getTotalBalanceReceivedToday(User.getUserSessionToken(), APP_VERSION, agentData)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<TransactionReportResponse>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Response<TransactionReportResponse> todaySentTransaction) {
//                Toast.makeText(getContext(), "You Don't Have Any dsaf", Toast.LENGTH_SHORT).show();
                if (todaySentTransaction.code() == 200) {
                    Double finalBalance = 0d;
                    try {
                        finalBalance = Double.parseDouble(String.valueOf(todaySentTransaction.body().getTransactionAmount()));
                    } catch (Exception e) {
                        Toast.makeText(context, context.getResources().getString(R.string.unable_to_get_balance), Toast.LENGTH_SHORT).show();
                    }
                    fundTransactionTodayReceivedAmount.setText(String.format(getResources().getString(R.string.fund_transaction_received_today_value), String.format("%.2f", finalBalance)));
                } else if (todaySentTransaction.code() == 400) {
                    fundTransactionTodayReceivedAmount.setText(getResources().getString(R.string.fund_transaction_received_today_empty));
                } else {
                    fundTransactionTodayReceivedAmount.setText(getResources().getString(R.string.fund_transaction_received_today_empty));
                }
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("has" + e);
            }

            @Override
            public void onComplete() {
// Stopping swipe refresh
//                myAgentsUsersListContainer.setRefreshing(false);
            }
        });
    }
    //endregion

    // region Get User Balance
    public void getUserBalance(final Context passedContext) {
        fundTransactionBalanceTitle.setText(getResources().getString(R.string.updating_balance));
        String token = User.getUserSessionToken();

        APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
        myInterface.retailerAccountInformation(token, APP_VERSION)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<AccountResponse>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(@NonNull Response<AccountResponse> balanceEndPointResponseData) {
                if (balanceEndPointResponseData.code() == 200) {
                    StateManager.setBalance(String.valueOf(balanceEndPointResponseData.body().getBalance()));
                    DecimalFormat df = new DecimalFormat();
                    df.setMaximumFractionDigits(2);
                    Double balance = 0d;
                    try {
                        balance = Double.parseDouble(String.valueOf(balanceEndPointResponseData.body().getBalance()));
                    } catch (Exception e) {
                        Toast.makeText(requireActivity(), getResources().getString(R.string.unable_to_get_balance), Toast.LENGTH_SHORT).show();
                    }
                    fundTransactionBalance.setText(String.format("%.2f", balance) + " Br.");
                    fundTransactionBalanceTitle.setText(getResources().getString(R.string.balance_updated));
                } else {
                    fundTransactionBalanceTitle.setText(getResources().getString(R.string.unable_to_update_balance_upper));
//                    Toast.makeText(passedContext, getResources().getString(R.string.someting_went_worong), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("has" + e);
            }

            @Override
            public void onComplete() {
//                fundTransactionBalanceTitle.setText("CURRENT BALANCE");
            }
        });
    }
    // endregion

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.fund_transaction_menu, menu);
//        MenuItem item = menu.findItem(R.id.action_search);
//        MenuItem createNewAgentButton = menu.findItem(R.id.action_search_add_new_agent);
//
////        binding.fundTransactionSearchView.setMenuItem(item);
////        // Search Box Text Changed Listener
////        binding.fundTransactionSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
////            @Override
////            public boolean onQueryTextSubmit(String query) {
//////                searchAdapter.getFilter().filter(query);
////                return false;
////            }
////
////            @Override
////            public boolean onQueryTextChange(String newText) {
////                System.out.println("Agent is typinggg");
//////                searchAdapter.getFilter().filter(newText);
////                return false;
////            }
////        });
////
////        // Search Box View Change Listener
////        binding.fundTransactionSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
////            @Override
////            public void onSearchViewShown() {
////                //Do some magic
////            }
////
////            @Override
////            public void onSearchViewClosed() {
////                //Do some magic
////            }
////        });
////        // endregion
//        super.onCreateOptionsMenu(menu, inflater);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
////        if (item.getItemId() == R.id.action_search_add_new_agent) {
////            MaterialDialog dialog = new MaterialDialog.Builder(context)
////                    .customView(R.layout.dialog_create_new_agent, false)
////                    .cancelable(true)
////                    .autoDismiss(true)
////                    .build();
////
////            agentName = dialog.getCustomView().findViewById(R.id.dialogAgentFullName);
////            agentPhone = dialog.getCustomView().findViewById(R.id.dialogAgentPhone);
////            agentEmail = dialog.getCustomView().findViewById(R.id.dialogAgentEmail);
////            agentPassword = dialog.getCustomView().findViewById(R.id.dialogAgentPassword);
////            agentConfirmPassword = dialog.getCustomView().findViewById(R.id.dialogAgentConfirmPassword);
////            Button btnCreateAgent = dialog.getCustomView().findViewById(R.id.dialogAgentCreateAgent);
////            Button btnCancel = dialog.getCustomView().findViewById(R.id.dialogAgentCancel);
//////agentName.addTextChangedListener(new TextValidator(agentName) {
//////    @Override
//////    public void validate(TextView textView, String text) {
//////        if(text.equals("")||text.length()<4){
//////            textView.setError("Name Should Not Be Less than 4 Character");
//////return;
//////        }
//////    }
//////});
////            String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
////            btnCreateAgent.setOnClickListener(v1 -> {
////                if (agentName.getText().toString().length() < 4 || agentName.getText().toString().equals("")
////                ) {
////
////                    Toast.makeText(context, "Please Fill Those Information", Toast.LENGTH_LONG).show();
////
////
////                } else if (agentPhone.getText().toString().length() < 10 || agentPhone.getText().toString().equals("")) {
////                    Toast.makeText(context, "Please Enter Valid Phone", Toast.LENGTH_LONG).show();
////
////
////                }
//////                else if (agentEmail.getText().toString().equals("") || !agentEmail.getText().toString().trim().matches(emailPattern)) {
//////                    Toast.makeText(getApplicationContext(), "Please Enter Valid Email", Toast.LENGTH_LONG).show();
//////
//////
//////                }
////                else if (agentPassword.getText().toString().equals("")) {
////                    Toast.makeText(context, "Please Enter Valid Password", Toast.LENGTH_LONG).show();
////                } else if (agentPassword.getText().toString().length() < 4) {
////                    Toast.makeText(context, "Password Character Length Should Not Be Less Than 4", Toast.LENGTH_LONG).show();
////
////
////                } else if (!agentConfirmPassword.getText().toString().equals(agentPassword.getText().toString())) {
////                    Toast.makeText(context, "Confirm Password Should Match Password", Toast.LENGTH_LONG).show();
////                } else {
////                    HashMap<String, String> agentData = new HashMap<>();
////
////                    agentData.put("name", agentName.getText().toString());
//////                    agentData.put("email", agentEmail.getText().toString());
////                    agentData.put("phone", agentPhone.getText().toString());
////                    agentData.put("password", agentPassword.getText().toString());
////                    agentData.put("userType", "5eb1b4e50b65b13950b7bcca");
////                    registerAgent(agentData);
////                }
////            });
////
////            btnCancel.setOnClickListener(v1 -> {
////                if (dialog.isShowing()) dialog.hide();
////            });
////
////            if (!dialog.isShowing()) dialog.show();
////            return true;
////        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        getActivity().getFragmentManager().popBackStack();
//        Toast.makeText(getContext(), "Transfer Fragment Destroyed", Toast.LENGTH_LONG).show();
    }
}
