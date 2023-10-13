package com.bee.planbus.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bee.planbus.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ForgetPassActivity extends BaseActivity {

    @BindView(R.id.txtLoginForgetPass)
    TextView txtLoginForgetPass;
    @BindView(R.id.btnSendPass)
    TextView btnSendPass;
    @BindView(R.id.edtEmailForgetPass)
    TextView edtEmailForgetPass;
    @BindView(R.id.rltProgress)
    RelativeLayout rltProgress;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);
        ButterKnife.bind(ForgetPassActivity.this);


        txtLoginForgetPass.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        txtLoginForgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnSendPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLoadingView(true);
                forgetPassword();

            }
        });
    }

    private void forgetPassword() {
        String email = edtEmailForgetPass.getText().toString().trim();
        if (email.isEmpty()) {
            warning(context, "Lütfen E-Mail adresinizi giriniz.");
            edtEmailForgetPass.requestFocus();
        } else {
            FirebaseAuth auth = FirebaseAuth.getInstance();

            auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        setLoadingView(false);
                        Toast.makeText(ForgetPassActivity.this, "E-Mail adresinize şifre yenileme linki gönderildi!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(ForgetPassActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        setLoadingView(false);
                        Toast.makeText(ForgetPassActivity.this, "Hata:" + task.getException().getMessage(), Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }

    public static void warning(Context context, String title) {
        AlertDialog.Builder alertDialogPassword = new AlertDialog.Builder(context);
        alertDialogPassword.setTitle("UYARI!")
                .setMessage(title)
                .setCancelable(false)
                .setIcon(R.drawable.warning)
                .setNeutralButton("Tamam", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public void setLoadingView(boolean b) {
        if (b) {
            rltProgress.setVisibility(View.VISIBLE);
        } else {
            rltProgress.setVisibility(View.GONE);

        }
    }
}