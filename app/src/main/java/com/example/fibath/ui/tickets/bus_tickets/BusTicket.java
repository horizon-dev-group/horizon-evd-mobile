package com.example.fibath.ui.tickets.bus_tickets;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.fibath.R;import com.example.fibath.ui.Printer.telpo.v330.Telpo330Printer;
import com.example.fibath.ui.helpers.UIHelpers;

import java.util.Calendar;

public class BusTicket extends AppCompatActivity {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    EditText spinnerTime;
    EditText spinnerDate;
    Calendar calender;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    int Year, Month, Day;
    Button bus_ticket_screen_btn_proceed;
    private String printContent = "busTicket";
    private String logo = "horizon_anbessa_logo.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bus_ticket);
        initUI();
    }

    // 1.1: Init UI
    private void initUI() {
        UIHelpers.makeWindowTransparent(this);
        calender = Calendar.getInstance();
        Year = calender.get(Calendar.YEAR);
        Month = calender.get(Calendar.MONTH);
        Day = calender.get(Calendar.DAY_OF_MONTH);
        spinnerTime = findViewById(R.id.spinnerTime);
        spinnerDate = findViewById(R.id.spinnerDate);
        bus_ticket_screen_btn_proceed = findViewById(R.id.bus_ticket_screen_btn_proceed);

        spinnerDateOnClickListener();
        spinnerTimeOnClickListener();
        proceedOnClickListener();
    }
    // endregion


    // 1.2: Init Event Listeners for spinnerTime
    private void spinnerTimeOnClickListener() {
        spinnerTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minute = cldr.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(BusTicket.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                spinnerTime.setText(hourOfDay + ":" + minute);

                            }


                        }, hour, minute, false);
                timePickerDialog.show();
            }
        });
    }

    // endregion

// 1.2: Init Event Listeners for spinnerDate
    private void spinnerDateOnClickListener() {
        spinnerDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                datePickerDialog = new DatePickerDialog(BusTicket.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                spinnerDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });
    }
    //endregion

    // 1.3: Init Event Listeners for proceed button
    private void proceedOnClickListener() {
        bus_ticket_screen_btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusTicket.this, Telpo330Printer.class);
                intent.putExtra("printType", printContent);
                intent.putExtra("logo", logo);
                startActivity(intent);
            }
        });
    }
    //endregion

}
