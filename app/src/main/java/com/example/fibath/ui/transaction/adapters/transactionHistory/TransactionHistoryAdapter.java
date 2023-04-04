package com.example.fibath.ui.transaction.adapters.transactionHistory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fibath.R;import com.example.fibath.classes.model.VoucherTransaction;
import com.example.fibath.ui.transaction.EachOfBulkTransactionHistory;

import java.util.List;

public class TransactionHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<VoucherTransaction> transactionList;
    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private boolean isLoadingAdded = false;

    public TransactionHistoryAdapter(Context context, List<VoucherTransaction> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.transaction_history_item_layout, parent, false);
        viewHolder = new TransactionHolder(v1);
        return viewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holders, final int position) {
        final VoucherTransaction transactionInfo = transactionList.get(position);
        switch (getItemViewType(position)) {
            case ITEM:
                final TransactionHolder holder = (TransactionHolder) holders;
                if (transactionInfo.getId() == null) {
                    holder.transactionCardView.setVisibility(View.GONE);
                } else {
                    int countFive = 0;
                    int countTen = 0;
                    int countFifteen = 0;
                    int countTwentyFive = 0;
                    int countTwenty = 0;
                    int countFifty = 0;
                    int countOneHundred = 0;
                    int countTwoHundred = 0;
                    int countTwoHundredAndFifty = 0;
                    int countFiveHundred = 0;
                    int countOneThousand = 0;
                    if(transactionInfo.getVouchers().get(0).getVoucherType()!=null&&transactionInfo.getVouchers().get(0).getVoucherType().equals("USSD") ){
                        holder.transactionType.setTextColor(ContextCompat.getColor(context, R.color.red));

                        holder.transactionType.setText("AIRTIME");

                    }else{
                        holder.transactionType.setText(transactionInfo.getVouchers().get(0).getPrintType());

                    }
                    holder.transactionTypeMain.setText(transactionInfo.getId());
                    holder.transactionTypeMain.setTextColor(ContextCompat.getColor(context, R.color.green));
                    String[] date = transactionInfo.getVouchers().get(0).getCreatedAt().split("T");
                    holder.transactionDate.setText(date[0]);
                    for (int i = 0; i < transactionInfo.getVouchers().size(); i++) {
                        if (transactionInfo.getVouchers().get(i).getAmount().equals("5.00")) {
                            countFive++;
                        }
                        if (transactionInfo.getVouchers().get(i).getAmount().equals("10.00")) {
                            countTen++;
                        }
                        if (transactionInfo.getVouchers().get(i).getAmount().equals("15.00")) {
                            countFifteen++;
                        }
                        if (transactionInfo.getVouchers().get(i).getAmount().equals("20.00")) {
                            countTwenty++;
                        }  if (transactionInfo.getVouchers().get(i).getAmount().equals("25.00")) {
                            countTwentyFive++;
                        }
                        if (transactionInfo.getVouchers().get(i).getAmount().equals("50.00")) {
                            countFifty++;
                        }
                        if (transactionInfo.getVouchers().get(i).getAmount().equals("100.00")) {
                            countOneHundred++;
                        }
                        if (transactionInfo.getVouchers().get(i).getAmount().equals("200.00")) {
                            countTwoHundred++;
                        }
                        if (transactionInfo.getVouchers().get(i).getAmount().equals("250.00")) {
                            countTwoHundredAndFifty++;
                        }
                        if (transactionInfo.getVouchers().get(i).getAmount().equals("500.00")) {
                            countFiveHundred++;
                        }
                        if (transactionInfo.getVouchers().get(i).getAmount().equals("1000.00")) {
                            countOneThousand++;
                        }
                    }
                    String quantity = "";
                    if (countFive != 0) {
                        quantity = "5 Birr:" + countFive;
                    }
                    if (countTen != 0) {
                        quantity += " | 10 Birr:" + countTen;
                    }
                    if (countFifteen != 0) {
                        quantity += " | 15 Birr:" + countFifteen;
                    }
                    if (countTwenty != 0) {
                        quantity += " | 20 Birr:" + countTwenty;
                    } if (countTwentyFive != 0) {
                        quantity += " | 25 Birr:" + countTwentyFive;
                    }
                    if (countFifty != 0) {
                        quantity += " | 50 Birr:" + countFifty;
                    }
                    if (countOneHundred != 0) {
                        quantity += " | 100 Birr:" + countOneHundred;
                    }
                    if (countTwoHundred!= 0) {
                        quantity += " | 200 Birr:" + countTwoHundred;
                    }
                    if (countTwoHundredAndFifty != 0) {
                        quantity += " | 250 Birr:" + countTwoHundredAndFifty;
                    }
                    if (countFiveHundred != 0) {
                        quantity += " | 500 Birr:" + countFiveHundred;
                    }
                    if (countOneThousand != 0) {
                        quantity += " | 1000 Birr:" + countOneThousand;
                    }
                    holder.transactionQuantityCount.setTextColor(R.color.red);
                    holder.transactionQuantityCount.setText(quantity);
                    System.out.println(quantity);
                    holder.transactionCardView.setOnClickListener(v -> {
                        Intent intent = new Intent(context, EachOfBulkTransactionHistory.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("TRANSACTION_ID", transactionInfo.getId());
                        context.startActivity(intent);
                    });
                }
            case LOADING:
//                Do nothing
                break;
        }
    }

    @Override
    public int getItemCount() {
        return transactionList == null ? 0 : transactionList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == transactionList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void add(VoucherTransaction r) {
        transactionList.add(r);
        notifyItemInserted(transactionList.size() - 1);
    }

    public void addAll(List<VoucherTransaction> moveResults) {
        for (VoucherTransaction result : moveResults) {
            add(result);
        }
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new VoucherTransaction());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = transactionList.size() - 1;
        VoucherTransaction result = getItem(position);
        if (result != null) {
            transactionList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public VoucherTransaction getItem(int position) {
        return transactionList.get(position);
    }

    public class TransactionHolder extends RecyclerView.ViewHolder {
        TextView transactionDate;
        TextView transactionStatus;
        TextView transactionType;
        TextView transactionTypeMain;
        TextView transactionQuantityCount;
        CardView transactionCardView;

        public TransactionHolder(View itemView) {
            super(itemView);
            transactionDate = itemView.findViewById(R.id.transaction_history_item_layout_screen_transaction_date);
            transactionType = itemView.findViewById(R.id.transaction_history_item_layout_screen_transaction_type);
            transactionTypeMain = itemView.findViewById(R.id.transaction_history_item_layout_screen_transaction_type_main);
            transactionCardView = itemView.findViewById(R.id.transaction_history_item_layout_screen_card_view);
            transactionQuantityCount = itemView.findViewById(R.id.quantity_count);
        }
    }

    protected class LoadingVH extends RecyclerView.ViewHolder {
        public LoadingVH(View itemView) {
            super(itemView);
        }
    }
}
