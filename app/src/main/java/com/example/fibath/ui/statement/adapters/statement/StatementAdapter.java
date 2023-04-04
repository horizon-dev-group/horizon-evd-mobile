package com.example.fibath.ui.statement.adapters.statement;

import static android.content.Context.MODE_PRIVATE;
import static com.example.fibath.classes.api.APIInterface.APP_VERSION;
import static com.example.fibath.classes.api.APIInterface.BASE_URL;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.airbnb.lottie.LottieAnimationView;
import com.daimajia.swipe.SwipeLayout;
import com.example.fibath.R;import com.example.fibath.classes.api.APIInterface;
import com.example.fibath.classes.model.FundResponse;
import com.example.fibath.classes.networking.communication.ServerCommunicator;
import com.example.fibath.classes.user.User;
import com.example.fibath.ui.statement.FundTransactionHistoryDetail;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class StatementAdapter extends RecyclerView.Adapter<StatementAdapter.StatementHolder> {
    private Context context;
    private List<FundResponse> modelStatementList;
    private String transactionType;

    public StatementAdapter(Context context, List<FundResponse> modelStatementList) {
        this.context = context;
        this.modelStatementList = modelStatementList;
    }

    public class StatementHolder extends RecyclerView.ViewHolder {
        TextView transactionID;
        ImageView transactionItemStatusIcon, transactionItemMore;
        TextView transactionItemAmount;
        TextView transactionItemTransactionId;
        TextView transactionItemTitle;
        TextView transactionItemDate;
        TextView transactionItemRemainingDeductible;
        FrameLayout transactionItemRevertTransaction;
        SwipeLayout transactionItemMainContainer;


        public StatementHolder(View itemView) {
            super(itemView);
            transactionItemStatusIcon = itemView.findViewById(R.id.transactionItemStatusIcon);
            transactionItemMainContainer = itemView.findViewById(R.id.transactionItemMainContainer);
            transactionItemTitle = itemView.findViewById(R.id.transactionItemTitle);
            transactionItemDate = itemView.findViewById(R.id.transactionItemDate);
            transactionItemTransactionId = itemView.findViewById(R.id.transactionItemTransactionId);
            transactionItemAmount = itemView.findViewById(R.id.transactionItemAmount);
            transactionItemMore = itemView.findViewById(R.id.transactionItemMore);
            transactionItemRevertTransaction = itemView.findViewById(R.id.transactionItemRevertTransaction);
            transactionItemRemainingDeductible = itemView.findViewById(R.id.transactionItemRemainingDeductible);
        }
    }

    public void add(List<FundResponse> modelStatementList, String transactionType) {
        this.modelStatementList = modelStatementList;
        this.transactionType = transactionType;
        notifyDataSetChanged();
    }

    @Override
    public StatementAdapter.StatementHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_fund_transaction_item, parent, false);

        return new StatementAdapter.StatementHolder(view);
    }

    @Override
    public void onBindViewHolder(StatementAdapter.StatementHolder holder, int position) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Preferences", MODE_PRIVATE);
        assert sharedPreferences != null;
        boolean showDeductedTransactions = sharedPreferences.getString("ShowDeductedTransactions", "1").equals("1");

        final FundResponse transactionInfo = modelStatementList.get(position);


//        Spannable sp1 = new SpannableString("From ");
//        sp1.setSpan(new ForegroundColorSpan(Color.RED), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        Spannable sp2 = new SpannableString("To ");
//        sp2.setSpan(new ForegroundColorSpan(Color.RED), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


//        holder.transactionItemTitle.setText("From " + transactionInfo.getSenderId().getName() + " To " + transactionInfo.getReceiverId().getName());

        holder.transactionItemTransactionId.setText(String.valueOf(transactionInfo.getTransactionId()));
        String[] date = transactionInfo.getCreatedAt().split("T");
        holder.transactionItemDate.setText(String.format(context.getResources().getString(R.string.transaction_item_date), date[0]));
        Double finalBalance = 0d;
        try {
            finalBalance = Double.parseDouble(String.valueOf(transactionInfo.getTransactionAmount()));
        } catch (Exception e) {
            Toast.makeText(context, context.getResources().getString(R.string.unable_to_get_balance), Toast.LENGTH_SHORT).show();
        }

        if (transactionType.equals("CREDIT")) {
            if (transactionInfo.getTransactionType().equals("Deducted") || transactionInfo.getTransactionType().equals("Non Trans Deduct")) {
                if (showDeductedTransactions) {
                    holder.transactionItemStatusIcon.setImageResource(R.drawable.ic_undo_green);
                    holder.transactionItemStatusIcon.setRotation(0f);
                    holder.transactionItemAmount.setTextColor(context.getResources().getColor(R.color.colorPrimaryPallet1));
                    holder.transactionItemAmount.setText("+" + transactionInfo.getTransactionAmount() + " Br.");
                    if (transactionInfo.getDeductedBy().getId().equals(User.getUserSessionId())) {
                        holder.transactionItemTitle.setText(String.format(context.getResources().getString(R.string.transaction_item_deducted_by_you), transactionInfo.getDeductedBy().getName()));
                    } else {
                        holder.transactionItemTitle.setText(String.format(context.getResources().getString(R.string.transaction_item_deducted_by), transactionInfo.getDeductedBy().getName()));
                    }
                } else {

                }
            } else {
                holder.transactionItemStatusIcon.setImageResource(R.drawable.ic_arrow_green);
                holder.transactionItemStatusIcon.setRotation(-45f);
                holder.transactionItemAmount.setTextColor(context.getResources().getColor(R.color.colorPrimaryPallet1));
                holder.transactionItemAmount.setText(String.format(context.getResources().getString(R.string.transaction_item_positive_amount), String.format("%.2f", finalBalance)));
                holder.transactionItemTitle.setText(String.format(context.getResources().getString(R.string.transaction_item_received_from), transactionInfo.getSenderId().getName()));
            }
            holder.transactionItemMore.setVisibility(View.GONE);
            holder.transactionItemRevertTransaction.setVisibility(View.GONE);
        } else if (transactionType.equals("DEBIT")) {
            if (transactionInfo.getTransactionType().equals("Deducted") && transactionInfo.getTransactionType().equals("Non Trans Deduct")) {
                if (showDeductedTransactions) {
                    holder.transactionItemStatusIcon.setImageResource(R.drawable.ic_undo_red);
                    holder.transactionItemStatusIcon.setRotation(0f);
                    holder.transactionItemAmount.setTextColor(context.getResources().getColor(R.color.colorPrimaryWarning));
                    holder.transactionItemAmount.setText(String.format(context.getResources().getString(R.string.transaction_item_nagative_amount), String.format("%.2f", finalBalance)));
//                if (transactionInfo.getDeductedBy().getId().equals(User.getUserSessionId())) {
//                    holder.transactionItemTitle.setText("Deducted By: " + "You");
//                } else {
                    holder.transactionItemTitle.setText(String.format(context.getResources().getString(R.string.transaction_item_deducted_by), transactionInfo.getDeductedBy().getName()));
//                }
                }
            } else {
                holder.transactionItemStatusIcon.setImageResource(R.drawable.ic_arrow_red);
                holder.transactionItemStatusIcon.setRotation(135f);
                holder.transactionItemAmount.setTextColor(context.getResources().getColor(R.color.colorPrimaryWarning));
                holder.transactionItemAmount.setText(String.format(context.getResources().getString(R.string.transaction_item_nagative_amount), String.format("%.2f", finalBalance)));
                holder.transactionItemTitle.setText(String.format(context.getResources().getString(R.string.transaction_item_sent_to), transactionInfo.getReceiverId().getName()));
            }

//            if (transactionInfo.getDeductable() != null) {
//                System.out.println("Deductable: " + Double.parseDouble(transactionInfo.getDeductable()));
//            } else {
//                System.out.println("Deductable: null");
//            }

            if (!transactionInfo.getTransactionType().equals("Deducted") || !transactionInfo.getTransactionType().equals("Non Trans Deduct")) {
                if (transactionInfo.getDeductable() == null) {
                    holder.transactionItemMore.setVisibility(View.VISIBLE);
                    holder.transactionItemRevertTransaction.setVisibility(View.VISIBLE);
                    holder.transactionItemRemainingDeductible.setVisibility(View.GONE);
                } else if (transactionInfo.getDeductable() != null && Double.parseDouble(transactionInfo.getDeductable()) > 0) {
                    holder.transactionItemMore.setVisibility(View.VISIBLE);
                    holder.transactionItemRevertTransaction.setVisibility(View.VISIBLE);
                    Double refunded = Double.parseDouble(transactionInfo.getTransactionAmount()) - Double.parseDouble(transactionInfo.getDeductable());
                    if (refunded > 0) {
                        holder.transactionItemRemainingDeductible.setText(String.format(context.getResources().getString(R.string.transaction_item_amount_deducted), refunded.toString()));
                        holder.transactionItemRemainingDeductible.setVisibility(View.VISIBLE);
                    }
                } else {
                    holder.transactionItemRemainingDeductible.setVisibility(View.GONE);
//                    holder.transactionItemMore.setVisibility(View.GONE);
//                    holder.transactionItemRevertTransaction.setVisibility(View.GONE);
                    holder.transactionItemRevertTransaction.setEnabled(false);
//                    holder.transactionItemRevertTransaction.setBackgroundColor(R.color.colorPrimaryGray);
                    holder.transactionItemRevertTransaction.setBackgroundColor(Color.parseColor("#454545"));
                }
            } else {
//                if (transactionInfo.getDeductable() == null && !transactionInfo.getSenderId().getId().equals(User.getUserSessionId())) {
//                    holder.transactionItemMore.setVisibility(View.VISIBLE);
//                    holder.transactionItemRevertTransaction.setVisibility(View.VISIBLE);
//                }
                if (transactionInfo.getDeductable() != null && Double.parseDouble(transactionInfo.getDeductable()) > 0 && !transactionInfo.getSenderId().getId().equals(User.getUserSessionId())) {
                    holder.transactionItemMore.setVisibility(View.VISIBLE);
                    holder.transactionItemRevertTransaction.setVisibility(View.VISIBLE);
                    holder.transactionItemRemainingDeductible.setVisibility(View.GONE);
                    holder.transactionItemRemainingDeductible.setTextColor(context.getResources().getColor(R.color.colorPrimaryPallet1));
                } else {
//                    holder.transactionItemMore.setVisibility(View.GONE);
//                    holder.transactionItemRevertTransaction.setVisibility(View.GONE);
                    holder.transactionItemRevertTransaction.setEnabled(false);
                    holder.transactionItemRemainingDeductible.setVisibility(View.GONE);
//                    holder.transactionItemRevertTransaction.setBackgroundColor(R.color.colorPrimaryGray);
                    holder.transactionItemRevertTransaction.setBackgroundColor(Color.parseColor("#454545"));

                }
            }
        }

        if (transactionType.equals("DEBIT")) {
            holder.transactionItemMore.setOnClickListener(view -> {
                holder.transactionItemMainContainer.toggle();
            });

            holder.transactionItemRevertTransaction.setOnClickListener(view -> {
                MaterialDialog dialog = new MaterialDialog.Builder(view.getContext())
                        .customView(R.layout.dialog_revert_transaction, false)
                        .cancelable(true)
                        .autoDismiss(true)
                        .build();

//            agentName = dialog.getCustomView().findViewById(R.id.dialogAgentFullName);
//            agentPhone = dialog.getCustomView().findViewById(R.id.dialogAgentPhone);
//            agentEmail = dialog.getCustomView().findViewById(R.id.dialogAgentEmail);
//            agentPassword = dialog.getCustomView().findViewById(R.id.dialogAgentPassword);
//            agentConfirmPassword = dialog.getCustomView().findViewById(R.id.dialogAgentConfirmPassword);
                TextView dialogRevertTransactionMaxDeductible = dialog.getCustomView().findViewById(R.id.dialogRevertTransactionMaxDeductible);
                TextView dialogRevertTransactionUserBalance = dialog.getCustomView().findViewById(R.id.dialogRevertTransactionUserBalance);
                EditText dialogRevertTransactionAmountToDeduct = dialog.getCustomView().findViewById(R.id.dialogRevertTransactionAmountToDeduct);
                Button dialogRevertTransactionCancel = dialog.getCustomView().findViewById(R.id.dialogRevertTransactionCancel);
                Button dialogRevertTransactionDeductTransaction = dialog.getCustomView().findViewById(R.id.dialogRevertTransactionDeductTransaction);


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
//            btnCreateAgent.setOnClickListener(v1 -> {
//                if (agentName.getText().toString().length() < 4 || agentName.getText().toString().equals("")
//                ) {
//
//                    Toast.makeText(context, "Please Fill Those Information", Toast.LENGTH_LONG).show();
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

                dialogRevertTransactionCancel.setOnClickListener(v1 -> {
                    if (dialog.isShowing()) dialog.hide();
                });

                dialogRevertTransactionDeductTransaction.setOnClickListener(v1 -> {
                    HashMap<String, Object> body = new HashMap<>();
                    body.put("amount", Double.parseDouble(dialogRevertTransactionAmountToDeduct.getText().toString()));
                    body.put("transaction_id", transactionInfo.getTransactionId());
                    revertTransaction(body);

                    if (dialog.isShowing()) dialog.hide();
                });

                if (!dialog.isShowing()) dialog.show();
            });

//        holder.transactionID.setText(String.valueOf(transactionInfo.getTransactionId()));

//        if (User.getUserSessionId().equals(transactionInfo.getReceiverId().getId())) {

//            holder.transactionType.setTextColor(ContextCompat.getColor(context, R.color.green));
//
//        } else {
//            holder.transactionType.setText("Debited");
//            holder.transactionType.setTextColor(ContextCompat.getColor(context, R.color.red));
//
//        }


//        holder.transactionItemMainContainer.setOnClickListener(v -> {
//            String[] date = transactionInfo.getCreatedAt().split("T");
//            Intent intent = new Intent(context, FundTransactionHistoryDetail.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.putExtra("STATEMENT_AMOUNT", transactionInfo.getTransactionAmount() + " Birr");
//            intent.putExtra("STATEMENT_DATE", date[0]);
////                intent.putExtra("STATEMENT_OPENING_BALANCE", (int) transactionInfo.getOpening()+".00 Birr");
////                intent.putExtra("STATEMENT_CLOSING_BALANCE", (int) transactionInfo.getClossing()+".00 Birr");
//            intent.putExtra("STATEMENT_RESPONSE", transactionInfo.getRemark());
//            intent.putExtra("STATEMENT_TRANSACTION_ID", String.valueOf(transactionInfo.getTransactionId()));
//            context.startActivity(intent);
//        });
            holder.transactionItemMainContainer.getSurfaceView().setOnClickListener(v -> {
                String[] newDate = transactionInfo.getCreatedAt().split("T");
                Intent intent = new Intent(context, FundTransactionHistoryDetail.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("STATEMENT_AMOUNT", transactionInfo.getTransactionAmount() + " Birr");
                intent.putExtra("STATEMENT_DATE", newDate[0]);
//                intent.putExtra("STATEMENT_OPENING_BALANCE", (int) transactionInfo.getOpening()+".00 Birr");
//                intent.putExtra("STATEMENT_CLOSING_BALANCE", (int) transactionInfo.getClossing()+".00 Birr");
                intent.putExtra("STATEMENT_RESPONSE", transactionInfo.getRemark());
                intent.putExtra("STATEMENT_TRANSACTION_ID", String.valueOf(transactionInfo.getTransactionId()));
                context.startActivity(intent);
            });
        }
    }

    private void revertTransaction(HashMap<String, Object> body) {
        APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
        myInterface.revertTransaction(User.getUserSessionToken(), APP_VERSION, body)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<FundResponse>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Response<FundResponse> responseTransactionHistory) {
                if (responseTransactionHistory.code() == 201) {
                    Toast.makeText(context, "Deducted Successfully.", Toast.LENGTH_SHORT).show();
//                    MaterialDialog dialog = new MaterialDialog.Builder(context)
//                            .customView(R.layout.dialog_lottie_message, false)
//                            .cancelable(true)
//                            .autoDismiss(true)
//                            .build();
//                    assert dialog.getCustomView() != null;
//                    LottieAnimationView dialogLottieAnimation = dialog.getCustomView().findViewById(R.id.dialogLottieAnimation);
//                    TextView dialogLottieTitle = dialog.getCustomView().findViewById(R.id.dialogLottieTitle);
//                    TextView dialogLottieDescription = dialog.getCustomView().findViewById(R.id.dialogLottieDescription);
//                    dialogLottieAnimation.setAnimation("success-dialog.json");
//                    dialogLottieTitle.setText(context.getResources().getString(R.string.dialog_success));
//                    dialogLottieDescription.setText(context.getResources().getString(R.string.transaction_item_deduction_successful_item));
//                    if (!dialog.isShowing()) dialog.show();
                } else if (responseTransactionHistory.code() == 400) {
                    Toast.makeText(context, context.getResources().getString(R.string.transaction_item_deduction_cant_deduct), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.someting_went_worong), Toast.LENGTH_SHORT).show();
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

    @Override
    public int getItemCount() {
        return modelStatementList.size();
    }
}
