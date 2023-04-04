package com.example.fibath.ui.transaction.adapters.transactionHistory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fibath.R;import com.example.fibath.ui.OfflineVoucher.database.model.DownloadedVouchers;
import com.example.fibath.ui.transaction.EachOfBulkTransactionHistory;

import java.util.List;

public class OfflineBulkTransactionHistoryAdapter extends RecyclerView.Adapter<OfflineBulkTransactionHistoryAdapter.BulkTransactionHolder> {
    private Context context;
    private List<DownloadedVouchers> transactionList;

    public OfflineBulkTransactionHistoryAdapter(Context context, List<DownloadedVouchers> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    public void add(List<DownloadedVouchers> transactionList) {
        this.transactionList = transactionList;
        notifyDataSetChanged();
    }

    @Override
    public OfflineBulkTransactionHistoryAdapter.BulkTransactionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_history_item_layout, parent, false);

        return new OfflineBulkTransactionHistoryAdapter.BulkTransactionHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(OfflineBulkTransactionHistoryAdapter.BulkTransactionHolder holder, int position) {
        final DownloadedVouchers transactionInfo = transactionList.get(position);


        holder.transactionTypeMain.setText(transactionInfo.getVoucherBulkId());
        holder.transactionDate.setText(transactionInfo.getVoucherCount());
        holder.transactionTypeMain.setTextColor(ContextCompat.getColor(context, R.color.green));
        holder.transactionCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EachOfBulkTransactionHistory.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("TRANSACTION_ID", transactionInfo.getVoucherBulkId());
                context.startActivity(intent);


            }
        });

    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public class BulkTransactionHolder extends RecyclerView.ViewHolder {
        TextView transactionDate;
        TextView transactionStatus;
        TextView transactionType;
        TextView transactionTypeMain;
        TextView transactionQuantityCount;
        CardView transactionCardView;


        public BulkTransactionHolder(View itemView) {
            super(itemView);
            transactionDate = itemView.findViewById(R.id.transaction_history_item_layout_screen_transaction_date);
            transactionType = itemView.findViewById(R.id.transaction_history_item_layout_screen_transaction_type);
            transactionTypeMain = itemView.findViewById(R.id.transaction_history_item_layout_screen_transaction_type_main);
            transactionCardView = itemView.findViewById(R.id.transaction_history_item_layout_screen_card_view);
            transactionQuantityCount = itemView.findViewById(R.id.quantity_count);

        }
    }
}