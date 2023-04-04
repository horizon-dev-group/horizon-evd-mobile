package com.example.fibath.ui.fundrequest;

import static com.example.fibath.classes.api.APIInterface.APP_VERSION;
import static com.example.fibath.classes.api.APIInterface.BASE_URL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.airbnb.lottie.LottieAnimationView;
import com.daimajia.swipe.SwipeLayout;
import com.example.fibath.R;import com.example.fibath.classes.api.APIInterface;
import com.example.fibath.classes.model.AgentUser;
import com.example.fibath.classes.model.ErrorResponse;
import com.example.fibath.classes.model.FundRequestListResponse;
import com.example.fibath.classes.model.MoneyTransferResponse;
import com.example.fibath.classes.networking.communication.ServerCommunicator;
import com.example.fibath.classes.user.User;
import com.geniusforapp.fancydialog.FancyAlertDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

//
public class FundRequestsAdapter extends RecyclerView.Adapter<FundRequestsAdapter.SearchViewHolder> /*implements Filterable*/ {
    private Context context;
    private ArrayList<FundRequestListResponse> fundRequestList;
    private ArrayList<FundRequestListResponse> filteredAgentList;


//    private DatabaseReference userDatabase;

    static class SearchViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage, user_sponsor_indicator, amountSave, userItemMore;
        TextView userName, userStatus, userBalance;
        ConstraintLayout userHolderMainContainer, nfc_image_right;
        FrameLayout user_holder_write_nfc_tag;
        FrameLayout userHolderEditUser;
        FrameLayout userHolderTransferMoney;
        FrameLayout user_holder_logout_user;
        SwipeLayout userListMainContainer;
        EditText user_holder_editText;

        SearchViewHolder(View itemView) {
            super(itemView);
            userHolderMainContainer = itemView.findViewById(R.id.user_holder_main_container);
            userHolderTransferMoney = itemView.findViewById(R.id.userHolderTransferMoney);
            userHolderEditUser = itemView.findViewById(R.id.userHolderEditUser);
            userListMainContainer = itemView.findViewById(R.id.userListMainContainer);
            userName = itemView.findViewById(R.id.user_name);
            profileImage = itemView.findViewById(R.id.user_image);
            userStatus = itemView.findViewById(R.id.user_status);
            user_sponsor_indicator = itemView.findViewById(R.id.user_sponsor_indicator);
            userBalance = itemView.findViewById(R.id.text_balance_right);
            userItemMore = itemView.findViewById(R.id.userItemMore);
        }
    }

    // region Constructor
    public FundRequestsAdapter(Context context, ArrayList<FundRequestListResponse> fundRequestList) {
        this.context = context;
        this.fundRequestList = fundRequestList;
        this.filteredAgentList = fundRequestList;
    }
    // endregion

    // region Create View Holder
    @NonNull
    @Override
    public FundRequestsAdapter.SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_fundrequest_item, parent, false);
        return new SearchViewHolder(view);
    }
    // endregion

    // region Bind View Holder
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(SearchViewHolder holder, int position) {
        final FundRequestListResponse fundRequestListResponse = filteredAgentList.get(position);

        holder.userName.setText(String.format(context.getResources().getString(R.string.fund_request_sent_from_to), User.getUserSessionUsername()));
        String[] date = fundRequestListResponse.getCreatedAt().split("T");

        holder.userStatus.setText(String.format(context.getResources().getString(R.string.transaction_item_date), date[0]));
        holder.userBalance.setText(String.format("%.0f", Double.parseDouble(fundRequestListResponse.getFundAmount())) + " " + context.getResources().getString(R.string.default_currency));

        if (fundRequestListResponse.getFundStatus().equals("SENT") || fundRequestListResponse.getFundStatus().equals("APPROVED")) {
            holder.profileImage.setImageResource(R.drawable.ic_clock_watch_svgrepo_com);
        } else if (fundRequestListResponse.getFundStatus().equals("RELEASED")) {
            holder.profileImage.setImageResource(R.drawable.ic_checked_svgrepo_com);
        } else if (fundRequestListResponse.getFundStatus().equals("DISAPPROVED")) {
            holder.profileImage.setImageResource(R.drawable.ic_cancel_svgrepo_com);
        }

        holder.userListMainContainer.setShowMode(SwipeLayout.ShowMode.LayDown);

        holder.userItemMore.setVisibility(View.GONE);
        holder.userItemMore.setOnClickListener(view -> {
            holder.userListMainContainer.toggle();
        });

        // region User Holder Swipe Listener
        holder.userListMainContainer.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onClose(SwipeLayout layout) {
                //when the SurfaceView totally cover the BottomView.
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                //you are swiping.
            }

            @Override
            public void onStartOpen(SwipeLayout layout) {

            }

            @Override
            public void onOpen(SwipeLayout layout) {
                //when the BottomView totally show.
//                Toast.makeText(context, "Toast Fully Opened.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                //when user's hand released.
            }
        });
        // endregion

        holder.userHolderTransferMoney.setVisibility(View.GONE);
        holder.userHolderTransferMoney.setOnClickListener(v -> {
//            MaterialDialog dialog = new MaterialDialog.Builder(context)
//                    .customView(R.layout.dialog_transfer_balance_to_agent, false)
//                    .cancelable(true)
//                    .autoDismiss(true)
//                    .build();
//            EditText transferAmount = dialog.getCustomView().findViewById(R.id.MyAgentBalanceTransferAmount);
//            EditText transferCommission = dialog.getCustomView().findViewById(R.id.MyAgentBalanceTransferCommission);
//            Button btnTransfer = dialog.getCustomView().findViewById(R.id.MyAgentBalanceTransferBtnTransfer);
//            Button btnCancel = dialog.getCustomView().findViewById(R.id.MyAgentBalanceTransferBtnCancel);
//            btnTransfer.setOnClickListener(v1 -> {
//                if (transferAmount.getText().toString().equals("") || Integer.parseInt(transferAmount.getText().toString().trim()) <= 0) {
//                    Toast.makeText(context, "Please Enter Valid Amount", Toast.LENGTH_SHORT).show();
//
//                } else {
//                    if (transferCommission.getText().toString().equals("") || Integer.parseInt(transferCommission.getText().toString().trim()) <= 0) {
//                        transferCommission.setText("0");
//                    }
//
//                    final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(context)
//                            .setBackgroundColor(R.color.colorPrimaryWarning)
//                            .setimageResource(R.drawable.ic_dialog_warning)
//                            .setTextTitle("Transfer Warning")
//                            .setTitleColor(R.color.colorLightWhite)
//                            .setTextSubTitle("Are you sure you want to transfer " + transferAmount.getText().toString() + " Birr to " + fundRequestListResponse.getName() + " with " + transferCommission.getText().toString() + " commission?")
//                            .setSubtitleColor(R.color.colorLightWhite)
//                            .setBodyColor(R.color.red)
//                            .setPositiveButtonText("Yes")
//                            .setPositiveColor(R.color.colorLightWhite)
//                            .setOnPositiveClicked((view1, dialogs) -> {
//                                HashMap<String, Object> transferData = new HashMap<>();
//                                transferData.put("amount", Integer.parseInt(transferAmount.getText().toString()));
//                                transferData.put("commission", Integer.parseInt(transferCommission.getText().toString()));
//                                transferData.put("receiver_id", fundRequestListResponse.getId());
//                                transferData.put("parent_remark", "From Mobile");
//                                transferMoney(transferData);
//
//                                transferAmount.setText("");
//                                transferCommission.setText("");
//
//                                dialogs.dismiss();
//                            })
//                            .setNegativeButtonText("No")
//                            .setNegativeColor(R.color.colorLightWhite)
//                            .setOnNegativeClicked((view1, dialogs) -> {
//                                dialogs.dismiss();
//                            })
//                            .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
//                            .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
//                            .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
//                            .build();
//                    alert.show();
//
//
//                }
//
//
////                Toast.makeText(context, "Transfering " + transferAmount.getText().toString() + " with " + transferCommission.getText().toString() + "% commission to " + userNameList.get(position), Toast.LENGTH_LONG).show();
//            });
//
//            btnCancel.setOnClickListener(v1 -> {
//                if (dialog.isShowing()) dialog.hide();
//            });
//
//            if (!dialog.isShowing()) dialog.show();
        });

        holder.userHolderEditUser.setVisibility(View.GONE);
        holder.userHolderEditUser.setOnClickListener(view -> {
//            MaterialDialog dialog = new MaterialDialog.Builder(context)
//                    .customView(R.layout.dialog_create_new_agent, false)
//                    .cancelable(true)
//                    .autoDismiss(true)
//                    .build();
//
//            assert dialog.getCustomView() != null;
//            ImageView imageView22 = dialog.getCustomView().findViewById(R.id.imageView22);
//            ImageView imageView23 = dialog.getCustomView().findViewById(R.id.imageView23);
//            TextView createAgentTitle = dialog.getCustomView().findViewById(R.id.createAgentTitle);
//            TextView createAgentDescription = dialog.getCustomView().findViewById(R.id.createAgentDescription);
//            EditText agentName = dialog.getCustomView().findViewById(R.id.dialogAgentFullName);
//            EditText agentPhone = dialog.getCustomView().findViewById(R.id.dialogAgentPhone);
////            agentEmail = dialog.getCustomView().findViewById(R.id.dialogAgentEmail);
//            EditText agentPassword = dialog.getCustomView().findViewById(R.id.dialogAgentPassword);
//            EditText agentConfirmPassword = dialog.getCustomView().findViewById(R.id.dialogAgentConfirmPassword);
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
//            agentName.setText(fundRequestListResponse.getName());
//            agentPhone.setText(fundRequestListResponse.getPhone());
//            createAgentTitle.setText("Update Agent");
//            createAgentDescription.setText("Enter agent name, phone in order to update.");
//            btnCreateAgent.setText("Update");
//            agentPassword.setVisibility(View.GONE);
//            agentConfirmPassword.setVisibility(View.GONE);
//            imageView22.setVisibility(View.GONE);
//            imageView23.setVisibility(View.GONE);
//
//            btnCreateAgent.setOnClickListener(v1 -> {
//                if (agentName.getText().toString().length() < 4 || agentName.getText().toString().equals("")) {
//                    Toast.makeText(context, "Name is required and has to be at least 4 characters.", Toast.LENGTH_LONG).show();
//                } else if (agentPhone.getText().toString().length() < 10 || agentPhone.getText().toString().equals("")) {
//                    Toast.makeText(context, "Please Enter Valid Phone", Toast.LENGTH_LONG).show();
//                }
//
//                HashMap<String, String> agentData = new HashMap<>();
//                agentData.put("name", agentName.getText().toString());
//                agentData.put("phone", agentPhone.getText().toString());
//                agentData.put("userId", fundRequestListResponse.getId());
//                updateAgent(agentData);
//            });
//
//            btnCancel.setOnClickListener(v1 -> {
//                if (dialog.isShowing()) dialog.hide();
//            });
//
//            if (!dialog.isShowing()) dialog.show();
        });


//        holder.userListMainContainer.setOnClickListener(e -> {
//            Intent intent = new Intent(context, ProfileNew.class);
//            intent.putExtra("userID", agentUser.getId());
//            context.startActivity(intent);
//        });

//        holder.userListMainContainer.getSurfaceView().setOnClickListener(v -> {
//            Intent intent = new Intent(context, ProfileNew.class);
//            intent.putExtra("userID", fundRequestListResponse.getId());
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);
//        });
    }
    // endregion

    // region Get Item Count
    @Override
    public int getItemCount() {
        return filteredAgentList.size();
    }
    // endregion

    public void add(ArrayList<FundRequestListResponse> fundRequestListResponses) {
        this.fundRequestList = fundRequestListResponses;
        notifyDataSetChanged();
    }

//    @Override
//    public Filter getFilter() {
//        return new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence charSequence) {
//                String charString = charSequence.toString();
//                if (charString.isEmpty()) {
//                    filteredAgentList = fundRequestList;
//                } else {
//                    ArrayList<FundRequestListResponse> filteredList = new ArrayList<>();
//                    System.out.println("Typed char is" + charString);
//                    for (FundRequestListResponse row : fundRequestList) {
//
//                        // name match condition. this might differ depending on your requirement
//                        // here we are looking for name or phone number match
//                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getPhone().contains(charSequence)) {
//
//                            System.out.println("char is added");
//
//                            filteredList.add(row);
//                        }
//                    }
//
//                    filteredAgentList = filteredList;
//                }
//
//                FilterResults filterResults = new FilterResults();
//                filterResults.values = filteredAgentList;
//                return filterResults;
//            }
//
//            @Override
//            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
//                filteredAgentList = (ArrayList<AgentUser>) filterResults.values;
//                notifyDataSetChanged();
//            }
//        };
//    }


    public void transferMoney(HashMap<String, Object> transferRequest) {
        MaterialDialog downloadProgress = new MaterialDialog.Builder(context)
                .customView(R.layout.money_transfer_dialog, false)
                .cancelable(false)
                .autoDismiss(false)
                .build();
        downloadProgress.show();
        Context contextForCompleteDialog = context;

        APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
        myInterface.transferMoneyWithCommission(User.getUserSessionToken(), APP_VERSION, transferRequest)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<MoneyTransferResponse>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(@NonNull Response<MoneyTransferResponse> voucherPurchaseResponse) {
                if (voucherPurchaseResponse.code() == 201) {
                    MaterialDialog dialog = new MaterialDialog.Builder(context)
                            .customView(R.layout.dialog_lottie_message, false)
                            .cancelable(true)
                            .autoDismiss(true)
                            .build();
                    assert dialog.getCustomView() != null;
                    LottieAnimationView dialogLottieAnimation = dialog.getCustomView().findViewById(R.id.dialogLottieAnimation);
                    TextView dialogLottieTitle = dialog.getCustomView().findViewById(R.id.dialogLottieTitle);
                    TextView dialogLottieDescription = dialog.getCustomView().findViewById(R.id.dialogLottieDescription);
                    dialogLottieAnimation.setAnimation("success-dialog.json");
                    dialogLottieTitle.setText("Success!");
                    dialogLottieDescription.setText("You have successfully transferred " + transferRequest.get("amount") + " birr.");
                    if (!dialog.isShowing()) dialog.show();
                } else {
                    switch (voucherPurchaseResponse.code()) {
                        case 400:
                            downloadProgress.dismiss();
                            Gson gson = new GsonBuilder().create();
                            ErrorResponse mError;
                            try {
                                mError = gson.fromJson(voucherPurchaseResponse.errorBody().string(), ErrorResponse.class);
                                FancyToast.makeText(context, mError.getErrors().get(0).getMsg(), FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                            } catch (IOException e) {
                                // handle failure to read error
                            }
                            break;
                        case 500:
                            downloadProgress.dismiss();
                            FancyToast.makeText(context, "Server Error! Please contact the system administrators.", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                            break;
                        default:
                            downloadProgress.dismiss();
                            FancyToast.makeText(context, "Unknown Error! Something went wrong.", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                            break;
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("has" + e);
                downloadProgress.dismiss();
            }

            @Override
            public void onComplete() {
                downloadProgress.dismiss();
            }
        });
    }

    // region Update Agent
    public void updateAgent(HashMap<String, String> agentData) {
        APIInterface myInterface = ServerCommunicator.getConnection(BASE_URL).create(APIInterface.class);
        myInterface.updateAgent(User.getUserSessionToken(), APP_VERSION, agentData)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<AgentUser>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Response<AgentUser> responseTransactionHistory) {
                if (responseTransactionHistory.code() == 201) {
                    Toast.makeText(context, "Agent Successfully Updated", Toast.LENGTH_SHORT).show();
//                    agentName.setText("");
////                    agentData.put("email", agentEmail.getText().toString());
//                    agentPhone.setText("");
//                    agentPassword.setText("");
//                    agentConfirmPassword.setText("");
//                    getAllChildAgents();
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
                            .setTextTitle("Error")
                            .setTitleColor(R.color.colorLightWhite)
                            .setTextSubTitle("Unable to Update.")
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
                    Toast.makeText(context, context.getResources().getString(R.string.someting_went_worong), Toast.LENGTH_SHORT).show();
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
    // endregion

}
