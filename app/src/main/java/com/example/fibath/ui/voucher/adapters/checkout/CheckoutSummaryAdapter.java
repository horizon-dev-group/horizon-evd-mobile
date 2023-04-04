package com.example.fibath.ui.voucher.adapters.checkout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.fibath.R;import com.example.fibath.ui.voucher.adapters.e_voucher.Vouchers;

import java.util.List;

public class CheckoutSummaryAdapter extends RecyclerView.Adapter<CheckoutSummaryAdapter.CheckoutHolder> {
private Context context;
private List<Vouchers> vouchersList;

    public CheckoutSummaryAdapter(Context context, List<Vouchers> vouchersList) {
        this.context = context;
        this.vouchersList = vouchersList;
    }

    public void add(List<Vouchers> vouchersList){
        this.vouchersList=vouchersList;
        notifyDataSetChanged();
    }
    @Override
    public CheckoutHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.checkout_item__voucher_middle_card,parent,false);

        return new CheckoutHolder(view);
    }

    @Override
    public void onBindViewHolder(CheckoutHolder holder, int position) {
        final Vouchers vouchers=vouchersList.get(position);
        holder.cardType.setText(vouchers.getMrp());
        holder.quantity.setText(vouchers.getAmount());
    }

    @Override
    public int getItemCount() {
        return vouchersList.size();
    }

    public class CheckoutHolder extends RecyclerView.ViewHolder {
        TextView cardType;
        TextView quantity;
        TextView checkout_item_voucher_list_textview_amount;


        public CheckoutHolder(View itemView) {
            super(itemView);
            cardType=itemView.findViewById(R.id.checkout_item_voucher_list_textview_type);
            quantity=itemView.findViewById(R.id.checkout_item_voucher_list_textview_amount);
        }
    }
}
