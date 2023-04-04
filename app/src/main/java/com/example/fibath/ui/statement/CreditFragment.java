package com.example.fibath.ui.statement;

import static android.content.Context.MODE_PRIVATE;
import static com.example.fibath.classes.api.APIInterface.APP_VERSION;
import static com.example.fibath.classes.api.APIInterface.BASE_URL;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.fibath.R;import com.example.fibath.classes.api.APIInterface;
import com.example.fibath.classes.model.FundResponse;
import com.example.fibath.classes.networking.communication.ServerCommunicator;
import com.example.fibath.classes.user.User;
import com.example.fibath.ui.statement.adapters.statement.StatementAdapter;
import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class CreditFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    RotateLoading activityUserStatementNewtonCradleLoading;
    RecyclerView userStatementRecyclerview;
    private StatementAdapter statementAdapter;
    private SwipeRefreshLayout transactionCreditListContainer;
    private List<FundResponse> statementList = new ArrayList<>();
    private boolean firstCreted = true;

    public CreditFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_credit, container, false);
        transactionCreditListContainer = view.findViewById(R.id.transactionCreditListContainer);
        initUI(view);
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            System.out.println("Credit Fragment Visible");
            if (!firstCreted) {
                getTransactionHistory();
            }
        }
    }

    // region Init UI
    private void initUI(View view) {
        transactionCreditListContainer.setOnRefreshListener(this);
        transactionCreditListContainer.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
//        Toast.makeText(getContext(), "Credit Fragment Created", Toast.LENGTH_LONG).show();
        userStatementRecyclerview = view.findViewById(R.id.user_statement_screen_recyclerview);
        activityUserStatementNewtonCradleLoading = view.findViewById(R.id.activity_user_statement_newton_cradle_loading2);
        initRecyclerView();
        getTransactionHistory();
        firstCreted = false;
    }
    // endregion

    // region Init RecyclerView
    private void initRecyclerView() {
        statementAdapter = new StatementAdapter(requireActivity(), statementList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity());
        userStatementRecyclerview.setLayoutManager(linearLayoutManager);
        userStatementRecyclerview.setHasFixedSize(false);
        userStatementRecyclerview.setAdapter(statementAdapter);
    }
    // endregion

    // region get Transaction History
    private void getTransactionHistory() {
        transactionCreditListContainer.setRefreshing(true);
        APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
        myInterface.retailerGetReceivedFundTransaction(User.getUserSessionToken(), APP_VERSION)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<ArrayList<FundResponse>>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Response<ArrayList<FundResponse>> responseStatements) {
                if (responseStatements.code() == 200) {
                    activityUserStatementNewtonCradleLoading.stop();

                    SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Preferences", MODE_PRIVATE);
                    assert sharedPreferences != null;
                    boolean showDeductedTransactions = sharedPreferences.getString("ShowDeductedTransactions", "1").equals("1");

                    if (!showDeductedTransactions) {
                        ArrayList<FundResponse> finalStatements = new ArrayList<>();
                        for (int i = 0; i < responseStatements.body().size(); i++) {
                            if (!responseStatements.body().get(i).getTransactionType().equals("Deducted") || !responseStatements.body().get(i).getTransactionType().equals("Non Trans Deduct")) {
                                finalStatements.add(responseStatements.body().get(i));
                            }
                        }
                        statementAdapter.add(finalStatements, "CREDIT");
                    } else {
                        statementAdapter.add(responseStatements.body(), "CREDIT");
                    }
                    System.out.println("********************************************************");
                } else if (responseStatements.code() == 400) {
//                    activityUserStatementNewtonCradleLoading.stop();
                    Toast.makeText(requireActivity(), "You Don't Have Any Transaction", Toast.LENGTH_SHORT).show();
                } else {
//                    activityUserStatementNewtonCradleLoading.stop();
                    Toast.makeText(requireActivity(), getResources().getString(R.string.someting_went_worong), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("has" + e);
            }

            @Override
            public void onComplete() {
                transactionCreditListContainer.setRefreshing(false);
            }
        });
    }
    //endregion

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {

        // Fetching data from server
        getTransactionHistory();
    }

}
