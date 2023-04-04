package com.example.fibath.ui.tickets.train_tickets;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.fibath.R;import com.example.fibath.ui.Printer.telpo.v330.Telpo330Printer;
import com.example.fibath.ui.helpers.UIHelpers;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.Calendar;

public class TrainTicket extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    EditText spinnerTime, spinnerDate;
    Calendar calender;
    DatePickerDialog picker;
    TimePickerDialog timePickerDialog;
    int Year, Month, Day;
    //    @BindView(R.id.train_ticket_spinner_trip_type)
    MaterialSpinner tripType;
    //    @BindView(R.id.bus_ticket_screen_text_departure_date)
    TextView departureDateText;
    //    @BindView(R.id.bus_ticket_screen_text_departure_time)
    TextView departureTimeText;
    //    @BindView(R.id.bus_ticket_screen_edit_text_departure_date)
    EditText departureDate;
    //    @BindView(R.id.bus_ticket_screen_edit_text_departure_time)
    EditText departureTime;
    Button train_ticket_screen_btn_proceed;
    private String printContent = "trainTicket";
    private String logo = "horizon_ethiorailway_logo.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.train_ticket);
        initUI();
        spinnerDateOnClickListener();
        spinnerTimeOnClickListener();
    }

    // 1.1: Init UI
    private void initUI() {
        UIHelpers.makeWindowTransparent(this);
        tripType = findViewById(R.id.train_ticket_spinner_trip_type);
        departureDateText = findViewById(R.id.bus_ticket_screen_text_departure_date);
        departureTimeText = findViewById(R.id.bus_ticket_screen_text_departure_time);
        departureDate = findViewById(R.id.bus_ticket_screen_edit_text_departure_date);
        departureTime = findViewById(R.id.bus_ticket_screen_edit_text_departure_time);
        train_ticket_screen_btn_proceed = findViewById(R.id.train_ticket_screen_btn_proceed);
        spinnerTime = findViewById(R.id.bus_ticket_screen_edit_text_departure_time);
        spinnerDate = findViewById(R.id.bus_ticket_screen_edit_text_departure_date);
        initTripType();
        proceedOnClickListener();
    }

    // 1.2:region Init UI Components
    private void initTripType() {
        tripType.setItems("Select Trip Type", "Light Train", "Ethio Djibouti");

        tripTypeOnItemSelectedListener();
    }
    //endregion

    // 1.3:region Init Event Listeners
    private void tripTypeOnItemSelectedListener() {
        tripType.setOnItemSelectedListener((MaterialSpinner.OnItemSelectedListener<String>) (view1, position, id, item) -> {

            if (item.equals("Light Train")) {
                hideDepartureSection();

            } else if (item.equals("Ethio Djibouti")) {
                showDepartureSection();
            }


        });
    }
    //endregion


    // 1.4:region Hide Departure Section
    public void hideDepartureSection() {
        if (departureTime.getVisibility() == View.VISIBLE && departureDate.getVisibility() == View.VISIBLE && departureTimeText.getVisibility() == View.VISIBLE && departureDateText.getVisibility() == View.VISIBLE) {
            departureDate.setVisibility(View.GONE);
            departureTime.setVisibility(View.GONE);
            departureDateText.setVisibility(View.GONE);
            departureTimeText.setVisibility(View.GONE);
        }

    }
    //endregion

    // 1.5:region Show Departure Section
    public void showDepartureSection() {
        if (departureTime.getVisibility() == View.GONE && departureDate.getVisibility() == View.GONE && departureTimeText.getVisibility() == View.GONE && departureDateText.getVisibility() == View.GONE) {

            departureDate.setVisibility(View.VISIBLE);
            departureTime.setVisibility(View.VISIBLE);
            departureDateText.setVisibility(View.VISIBLE);
            departureTimeText.setVisibility(View.VISIBLE);
        }
    }
    //endregion

    // 1.6:region Init Event Listeners for proceed button
    private void proceedOnClickListener() {
        train_ticket_screen_btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrainTicket.this, Telpo330Printer.class);
                intent.putExtra("printType", printContent);
                intent.putExtra("logo", logo);
                startActivity(intent);
            }
        });
    }
    //endregion

    // 1.7:region Init Event Listeners for spinnerTime
    private void spinnerTimeOnClickListener() {
        spinnerTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minute = cldr.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(TrainTicket.this,
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

    // 1.8: Init Event Listeners for spinnerDate
    private void spinnerDateOnClickListener() {
        spinnerDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(TrainTicket.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                spinnerDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });
    }
    //endregion
}
