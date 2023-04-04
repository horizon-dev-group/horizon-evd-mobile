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
import com.example.fibath.ui.transaction.VoucherTransactionHistoryDetail;

import java.util.ArrayList;
import java.util.List;

public class OfflineEachOfBulkTransactionHistoryAdapter extends RecyclerView.Adapter<OfflineEachOfBulkTransactionHistoryAdapter.BulkTransactionHolder> {
    private Context context;
    public List<DownloadedVouchers> transactionList;
    public ArrayList<DownloadedVouchers> selectedVoucherList=new ArrayList<>();

    public OfflineEachOfBulkTransactionHistoryAdapter(Context context, List<DownloadedVouchers> transactionList, ArrayList<DownloadedVouchers> selectedVoucherList) {
        this.context = context;
        this.transactionList = transactionList;
        this.selectedVoucherList=selectedVoucherList;
    }

    public void add(List<DownloadedVouchers> transactionList) {
        this.transactionList = transactionList;
        notifyDataSetChanged();
    }

    @Override
    public OfflineEachOfBulkTransactionHistoryAdapter.BulkTransactionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_of_transaction_history_item_layout, parent, false);

        return new OfflineEachOfBulkTransactionHistoryAdapter.BulkTransactionHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(OfflineEachOfBulkTransactionHistoryAdapter.BulkTransactionHolder holder, int position) {
        final DownloadedVouchers transactionInfo = transactionList.get(position);
        System.out.println("**********************************");
//        System.out.println("Amount is " +transactionInfo.getVoucher().getVoucherAmount());
        System.out.println("*************************************");


        holder.transactionStatus.setText(R.string.transaction_history_item_layout_text_status_success);
        holder.transactionStatus.setTextColor(ContextCompat.getColor(context, R.color.green));



//
//            mainType = mainType2.isEmpty() ? TransactionHistoryActivity.this.getString(R.string.txt_transac_main_type_VoucherSale) : mainType2;
        holder.transactionType.setText(R.string.transaction_history_item_layout_text_transac_main_type_VoucherSale);



        holder.transactionTypeMain.setText(transactionInfo.getVoucherSerialNumber());
        holder.transactionTypeMain.setTextColor(ContextCompat.getColor(context, R.color.green));
holder.textViewPageCount.setText((position+1)+" ");
        holder.textViewPageCount.setTextColor(ContextCompat.getColor(context, R.color.red));


        holder.transactionPrice.setText( transactionInfo.getVoucherAmount() + " Birr");
//        String[] date=transactionInfo.getVoucherId().getUpdatedAt().split("T");
        holder.transactionDate.setText(transactionInfo.getTimestamp());
        if(selectedVoucherList.contains(transactionList.get(position)))
            holder.transactionCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.accent_color));
        else
            holder.transactionCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.mdtp_white));

        holder.transactionCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("voucher redownload is "+transactionInfo.getVoucherReDownload());
                Intent intent = new Intent(context, VoucherTransactionHistoryDetail.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("TRANSACTION_AMOUNT",  transactionInfo.getVoucherAmount() + " Birr");
                intent.putExtra("TRANSACTION_DATE", transactionInfo.getTimestamp());
                intent.putExtra("TRANSACTION_EXPIRY_DATE", transactionInfo.getVoucherExpiryDate());
                intent.putExtra("TRANSACTION_SERIAL_NUMBER", transactionInfo.getVoucherSerialNumber());
                intent.putExtra("TRANSACTION_STATUS",holder.transactionStatus.getText().toString());
                intent.putExtra("TRANSACTION_PIN_NUMBER",transactionInfo.getVoucherNumber());
                intent.putExtra("TRANSACTION_REDOWNLOAD", Math.max(transactionInfo.getVoucherReDownload(), 0));


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
        TextView transactionPrice;
        TextView transactionType;
        TextView transactionTypeMain;
        TextView textViewPageCount;
        CardView transactionCardView;


        public BulkTransactionHolder(View itemView) {
            super(itemView);
            transactionDate = itemView.findViewById(R.id.transaction_history_item_layout_screen_transaction_date);
            transactionStatus = itemView.findViewById(R.id.transaction_history_item_layout_screen_transaction_status);
            transactionPrice = itemView.findViewById(R.id.transaction_history_item_layout_screen_transaction_price);
            transactionType = itemView.findViewById(R.id.transaction_history_item_layout_screen_transaction_type);
            transactionTypeMain = itemView.findViewById(R.id.transaction_history_item_layout_screen_transaction_type_main);
            textViewPageCount = itemView.findViewById(R.id.textViewPageCount);
            transactionCardView = itemView.findViewById(R.id.transaction_history_item_layout_screen_card_view);


        }
    }

}