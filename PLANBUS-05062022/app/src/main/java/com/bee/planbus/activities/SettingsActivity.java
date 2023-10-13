package com.bee.planbus.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bee.planbus.R;
import com.bee.planbus.modals.LanguageSettingModal;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.layoutUpdatePassword)
    LinearLayout layoutUpdatePassword;
    @BindView(R.id.layoutLanguage)
    LinearLayout layoutLanguage;
    @BindView(R.id.txtSettingsHeader)
    TextView txtSettingsHeader;
    @BindView(R.id.txtSettingsLanguage)
    TextView txtSettingsLanguage;
    @BindView(R.id.txtSettingUpdatePass)
    TextView txtSettingUpdatePass;

    Boolean passwordVisible = false;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        layoutUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePassDialog();
            }
        });

        layoutLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeLanguageDialog(txtSettingsHeader, txtSettingUpdatePass, txtSettingsLanguage);
            }
        });
    }

    public void showChangePassDialog() {

        View view = LayoutInflater.from(SettingsActivity.this).inflate(R.layout.dialog_update_pass, null);

        EditText edtCurrentPass = view.findViewById(R.id.edtCurrentPass);
        EditText edtNewPass = view.findViewById(R.id.edtNewPass);
        Button btnUpdatePass = view.findViewById(R.id.btnUpdatePass);


        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.show();

        edtCurrentPass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int Right = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= edtCurrentPass.getRight() - edtCurrentPass.getCompoundDrawables()[Right].getBounds().width()) {
                        int selection = edtCurrentPass.getSelectionEnd();
                        if (passwordVisible) {
                            edtCurrentPass.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_off_24, 0);
                            edtCurrentPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;
                        } else {
                            edtCurrentPass.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_24, 0);
                            edtCurrentPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;
                        }
                        edtCurrentPass.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });
        edtNewPass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int Right = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= edtNewPass.getRight() - edtNewPass.getCompoundDrawables()[Right].getBounds().width()) {
                        int selection = edtNewPass.getSelectionEnd();
                        if (passwordVisible) {
                            edtNewPass.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_off_24, 0);
                            edtNewPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;
                        } else {
                            edtNewPass.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_24, 0);
                            edtNewPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;
                        }
                        edtNewPass.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

        btnUpdatePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentPass = edtNewPass.getText().toString().trim();
                String newPass = edtNewPass.getText().toString().trim();
                if (TextUtils.isEmpty(currentPass)) {
                    Toast.makeText(SettingsActivity.this, "Lütfen mevcut şifrenizi giriniz!", Toast.LENGTH_LONG).show();
                    edtNewPass.requestFocus();
                }
                if (newPass.length() < 6) {
                    Toast.makeText(SettingsActivity.this, "Yeni şifreniz en az 6 karakterli olmalı!", Toast.LENGTH_LONG).show();
                    edtNewPass.requestFocus();
                }


                updatePassword(currentPass, newPass, dialog);

            }
        });
    }

    public void updatePassword(String currentPass, String newPass, AlertDialog dialog) {

        AuthCredential authCredential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), currentPass);
        firebaseUser.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        firebaseUser.updatePassword(newPass).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        dialog.dismiss();
                                        Toast.makeText(SettingsActivity.this, "Şifreniz güncellendi...", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SettingsActivity.this, "Hata:" + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SettingsActivity.this, "Hata:" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void showChangeLanguageDialog(TextView txtHeader, TextView txtPass, TextView txtLanguage) {

        LanguageSettingModal languageSettingModal = new LanguageSettingModal(txtHeader,txtPass,txtLanguage);

        languageSettingModal.show(SettingsActivity.this.getSupportFragmentManager(),"");

        /*View view = LayoutInflater.from(SettingsActivity.this).inflate(R.layout.dialog_language_settings, null);

        TextView txtChoseLanguage = view.findViewById(R.id.txtChoseLanguage);
        RadioGroup rgLanguage = view.findViewById(R.id.rgLanguage);
        RadioButton rbTurkce = view.findViewById(R.id.rbTurkce);
        RadioButton rbEnglish = view.findViewById(R.id.rbEnglish);
        Button btnChoseLanguage = view.findViewById(R.id.btnChoseLanguage);


        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.show();

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
                        txtChoseLanguage.setText("Chose Language");
                        rbEnglish.setText("English");
                        rbTurkce.setText("Turkish");
                        btnChoseLanguage.setText("Chose Language");
                        break;

                }
            }
        });*/


    }

    /*public void setLocale(String language) {
        Resources resources = getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = new Locale(language);
        resources.updateConfiguration(configuration, metrics);
        onConfigurationChanged(configuration);
    }*/


}