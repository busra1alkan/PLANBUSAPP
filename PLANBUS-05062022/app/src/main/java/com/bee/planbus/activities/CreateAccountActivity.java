package com.bee.planbus.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bee.planbus.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateAccountActivity extends BaseActivity {

    @BindView(R.id.txtLogin)
    TextView txtLogin;
    @BindView(R.id.btnRegister)
    Button btnRegister;
    @BindView(R.id.edtEmailReg)
    EditText edtEmailReg;
    @BindView(R.id.edtPassReg)
    EditText edtPassReg;
    @BindView(R.id.edtPassAgainReg)
    EditText edtPassAgainReg;
    @BindView(R.id.rltProgress)
    RelativeLayout rltProgress;


    Boolean passwordVisiblePass = false;
    Boolean passwordVisiblePassAgain = false;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://planbus-67521-default-rtdb.firebaseio.com/");
    FirebaseAuth firebaseAuth;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        ButterKnife.bind(this);

        txtLogin.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        firebaseAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLoadingView(true);
                createUser();
            }
        });

        Log.e("TAG", "onCreate: ");


        edtPassReg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int Right = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= edtPassReg.getRight() - edtPassReg.getCompoundDrawables()[Right].getBounds().width()) {
                        int selection = edtPassReg.getSelectionEnd();
                        if (passwordVisiblePass) {
                            edtPassReg.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_off_24, 0);
                            edtPassReg.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisiblePass = false;
                        } else {
                            edtPassReg.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_24, 0);
                            edtPassReg.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisiblePass = true;
                        }
                        edtPassReg.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

        edtPassAgainReg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int Right = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= edtPassAgainReg.getRight() - edtPassAgainReg.getCompoundDrawables()[Right].getBounds().width()) {
                        int selection = edtPassAgainReg.getSelectionEnd();
                        if (passwordVisiblePassAgain) {
                            edtPassAgainReg.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_off_24, 0);
                            edtPassAgainReg.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisiblePassAgain = false;
                        } else {
                            edtPassAgainReg.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_24, 0);
                            edtPassAgainReg.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisiblePassAgain = true;
                        }
                        edtPassAgainReg.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void createUser() {
        String email = edtEmailReg.getText().toString().trim();
        String pass = edtPassReg.getText().toString().trim();
        String passAgain = edtPassAgainReg.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            setLoadingView(false);
            warning(context, "Lütfen E-Mail adresinizi giriniz.");
            edtEmailReg.requestFocus();
        } else if (TextUtils.isEmpty(pass)) {
            setLoadingView(false);
            warning(context, "Lütfen şifrenizi giriniz..");
            edtPassReg.requestFocus();
        } else if (TextUtils.isEmpty(passAgain)) {
            setLoadingView(false);
            warning(context, "Lütfen şifrenizi iki alanda da giriniz.");
            edtPassAgainReg.requestFocus();
        } else if (!pass.equals(passAgain)) {
            setLoadingView(false);
            Toast.makeText(context, "Girdiğiniz şifreler aynı değil!", Toast.LENGTH_LONG).show();
        } else {
            firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        setLoadingView(false);
                        Toast.makeText(context, "Aramıza hoşgeldin!", Toast.LENGTH_LONG).show();
                        sendEmailVerification();
                    } else {
                        setLoadingView(false);
                        Toast.makeText(context, "Kayıt yapılamadı, lütfen tekrar deneyiniz." + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void sendEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(), "Doğrulama maili gönderildi, mail adresinizden doğrulama yapınız.", Toast.LENGTH_LONG).show();
                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(CreateAccountActivity.this, LoginActivity.class));
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Doğrulama maili gönderilemedi. Lütfen tekrar deneyiniz.", Toast.LENGTH_LONG).show();
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
            //MainActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            rltProgress.setVisibility(View.GONE);
            //MainActivity.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        }
    }
}