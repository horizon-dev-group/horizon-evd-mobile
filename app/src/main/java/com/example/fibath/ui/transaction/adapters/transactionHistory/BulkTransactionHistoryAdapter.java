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

import com.example.fibath.R;import com.example.fibath.classes.model.BulkVoucherResponse;
import com.example.fibath.ui.transaction.EachOfBulkTransactionHistory;

import java.util.List;

public class BulkTransactionHistoryAdapter extends RecyclerView.Adapter<BulkTransactionHistoryAdapter.BulkTransactionHolder> {
    private Context context;
    private List<BulkVoucherResponse> transactionList;

    public BulkTransactionHistoryAdapter(Context context, List<BulkVoucherResponse> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    public void add(List<BulkVoucherResponse> transactionList) {
        this.transactionList = transactionList;
        notifyDataSetChanged();
    }

    @Override
    public BulkTransactionHistoryAdapter.BulkTransactionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bulk_voucher_transaction_history_item_layout, parent, false);

        return new BulkTransactionHistoryAdapter.BulkTransactionHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(BulkTransactionHistoryAdapter.BulkTransactionHolder holder, int position) {
        final BulkVoucherResponse transactionInfo = transactionList.get(position);
//        if(transactionInfo.getBulkType().equals("Single")){
//            holder.transactionType.setTextColor(R.color.red);
//        }else {
////            holder.transactionCardView.setCardBackgroundColor(R.color.red);
//        }
        System.out.println("**********************************");
        System.out.println("Amount is " +transactionInfo.getBulkId());
        System.out.println("*************************************");







//
//            mainType = mainType2.isEmpty() ? TransactionHistoryActivity.this.getString(R.string.txt_transac_main_type_VoucherSale) : mainType2;
//        holder.transactionType.setText(transactionInfo.getBulkType());



        holder.transactionTypeMain.setText(transactionInfo.getBulkId());
        holder.transactionTypeMain.setTextColor(ContextCompat.getColor(context, R.color.green));



        String[] date=transactionInfo.getCreatedAt().split("T");
        holder.transactionDate.setText(date[0]);
        holder.transactionCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EachOfBulkTransactionHistory.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.putExtra("TRANSACTION_AMOUNT",  transactionInfo.getVoucherId().getVoucherAmount() + " Birr");
//                intent.putExtra("TRANSACTION_DATE", date[0]);
//                intent.putExtra("TRANSACTION_SERIAL_NUMBER", transactionInfo.getVoucherId().getVoucherSerialNumber());
//                intent.putExtra("TRANSACTION_STATUS",holder.transactionStatus.getText().toString());
                intent.putExtra("BULK_ID",transactionInfo.getBulkId());
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
        TextView transactionType;
        TextView transactionTypeMain;
        CardView transactionCardView;


        public BulkTransactionHolder(View itemView) {
            super(itemView);
            transactionDate = itemView.findViewById(R.id.transaction_history_item_layout_screen_transaction_date);


            transactionType = itemView.findViewById(R.id.transaction_history_item_layout_screen_transaction_type);
            transactionTypeMain = itemView.findViewById(R.id.transaction_history_item_layout_screen_transaction_type_main);
            transactionCardView = itemView.findViewById(R.id.transaction_history_item_layout_screen_card_view);


        }
    }
}