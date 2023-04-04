package com.example.fibath.ui.sample;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.fibath.R;
public class IPDialog extends DialogFragment implements DialogInterface.OnClickListener {
    public static String IP="http://10.0.2.2/evd/webservices/";

    private View form;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        form=requireActivity().getLayoutInflater().inflate(R.layout.dialog,null);
        AlertDialog.Builder builder=new AlertDialog.Builder(requireActivity());

        return ( builder.setTitle("Enter Your IP").setView(form)

                .setPositiveButton("OK",this)

                .setNegativeButton("Cancel",null).create());
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
//        Toast.makeText(getContext(),"You Dismmised The dialog",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        Toast.makeText(getContext(),"You Predded The back button",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        EditText ip_address=(EditText)form.findViewById(R.id.ip_field);
        IP="http://"+ip_address.getText().toString()+"/evdlive/webservices/";
        Toast.makeText(getContext(),"You IP is"+IP,Toast.LENGTH_SHORT).show();
//        Intent intent= new Intent(getContext(), Intro.class);
//        startActivity(intent);


    }
}
