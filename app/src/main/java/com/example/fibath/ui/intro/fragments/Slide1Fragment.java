package com.example.fibath.ui.intro.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.fibath.R;import com.example.fibath.classes.localization.LanguageSelector;
import com.example.fibath.classes.networking.Internet.CheckInternetConnection;
import com.example.fibath.classes.state.StateManager;
import com.example.fibath.ui.helpers.UIHelpers;
import com.jaredrummler.materialspinner.MaterialSpinner;

public class Slide1Fragment extends Fragment {
    // region Initializations

    // region UI Elements
    private MaterialSpinner languagePicker;
    // endregion
    // endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_app_intro_slide_1, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initUI(view);
    }

    // region ----- Initialize App -----

    // region 1: Initialize UI

    // 1.1: Init UI
    private void initUI(View view) {
        new CheckInternetConnection(requireActivity()).checkConnection();
        UIHelpers.makeWindowTransparent(requireActivity());
        initLanguagePicker(view);
    }

    // 1.2: Init UI Components
    private void initLanguagePicker(View view) {
        languagePicker = view.findViewById(R.id.user_setting_change_language);
        // languagePicker.setSelectedIndex();
        languagePicker.setItems("English", "አማርኛ", "Oromifa", "ትግርኛ", "Somali", "Afar");
        if (StateManager.getAppLanguage().equals("en")) {
            languagePicker.setSelectedIndex(0);
        } else if (StateManager.getAppLanguage().equals("am")) {
            languagePicker.setSelectedIndex(1);
        }else if(StateManager.getAppLanguage().equals("om")){
            languagePicker.setSelectedIndex(2);
        }
        languagePickerOnItemSelectedListener();
    }
    // endregion

    // 1.3: Init Event Listeners
    private void languagePickerOnItemSelectedListener() {
        languagePicker.setOnItemSelectedListener((MaterialSpinner.OnItemSelectedListener<String>) (view1, position, id, item) -> {
            languagePickerOnItemSelectedHandler(item);
        });
    }

    // 1.4: Init Event Handlers
    private void languagePickerOnItemSelectedHandler(Object item) {
        if (item.equals("English")) {
            changeAppLanguage("en");
        } else if (item.equals("አማርኛ")) {
            changeAppLanguage("am");
        }else if(item.equals("Oromifa")){
            changeAppLanguage("om");
        }
    }
    // endregion


    // endregion


    // region Change Language
    private void changeAppLanguage(String locale) {
        // region Save Selected Language
        LanguageSelector.saveAppLanguage(requireContext(), locale);
        // endregion

        // region Change Language
        LanguageSelector.setLanguage(requireContext());
        // endregion

        // region Refresh App
        // Intent refreshApp = new Intent(getActivity(), Intro.class);
        // startActivity(refreshApp);
        requireActivity().finish();
        requireActivity().overridePendingTransition(0, 0);
        startActivity(requireActivity().getIntent());
        requireActivity().overridePendingTransition(0, 0);
        // endregion

//        LanguageSelector.changeAppLanguage(getActivity().getApplicationContext(), locale);

//        Locale myLocale;
//        myLocale = new Locale(locale);
//        Resources resources = getResources();
//        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
//        Configuration configuration = resources.getConfiguration();
//        configuration.locale = myLocale;
//        resources.updateConfiguration(configuration, displayMetrics);
//
//        Intent refreshApp = new Intent(getActivity(), Intro.class);
//        refreshApp.putExtra("am", "am");
//        startActivity(refreshApp);


//        LanguageSelector.chooseLanguage(Objects.requireNonNull(getActivity()).getApplicationContext(), locale);
//        startActivity(new Intent(getActivity().getApplicationContext(), Intro.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        new CheckInternetConnection(requireActivity());
    }
    // endregion
}