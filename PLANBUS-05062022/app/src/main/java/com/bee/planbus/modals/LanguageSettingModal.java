package com.bee.planbus.modals;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.bee.planbus.R;
import com.bee.planbus.utils.Const;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LanguageSettingModal extends AppCompatDialogFragment {

    @BindView(R.id.txtChoseLanguage)
    TextView txtChoseLanguage;
    @BindView(R.id.rgLanguage)
    RadioGroup rgLanguage;
    @BindView(R.id.rbEnglish)
    RadioButton rbEnglish;
    @BindView(R.id.rbTurkce)
    RadioButton rbTurkce;
    @BindView(R.id.btnChoseLanguage)
    Button btnChoseLanguage;

    TextView txtHeader;
    TextView txtPass;
    TextView txtLanguage;

    public LanguageSettingModal(TextView txtHeader, TextView txtPass, TextView txtLanguage) {
        this.txtHeader = txtHeader;
        this.txtPass = txtPass;
        this.txtLanguage = txtLanguage;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_language_settings, null);
        builder.setView(view);
        ButterKnife.bind(this, view);


        rgLanguage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbTurkce:
                        String language = "tr";
                        setLocale(language);
                        break;
                    case R.id.rbEnglish:
                        setLocale("en");
                        txtHeader.setText("Settings");
                        txtLanguage.setText("Select Language");
                        txtPass.setText("Update Password");
                        break;
                }
            }
        });

        return builder.create();
    }

    public void setLocale(String language) {
        Resources resources = getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = new Locale(language);
        resources.updateConfiguration(configuration, metrics);
        onConfigurationChanged(configuration);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        txtChoseLanguage.setText("Chose Language");
        rbEnglish.setText("English");
        rbTurkce.setText("Turkish");
        btnChoseLanguage.setText("Chose Language");
    }
}
