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
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bee.planbus.R;
import com.bee.planbus.utils.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.txtCreateAccountLogin)
    TextView txtCreateAccountLogin;
    @BindView(R.id.txtForgetPass)
    TextView txtForgetPass;
    @BindView(R.id.edtEmail)
    EditText edtEmail;
    @BindView(R.id.edtPass)
    EditText edtPass;
    @BindView(R.id.rltProgress)
    RelativeLayout rltProgress;


    Boolean passwordVisible = false;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(LoginActivity.this);

        txtForgetPass.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        firebaseAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null){
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }

        edtPass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int Right = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= edtPass.getRight() - edtPass.getCompoundDrawables()[Right].getBounds().width()) {
                        int selection = edtPass.getSelectionEnd();
                        if (passwordVisible) {
                            edtPass.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_off_24, 0);
                            edtPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;
                        } else {
                            edtPass.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_24, 0);
                            edtPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;
                        }
                        edtPass.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLoadingView(true);
                loginUser();

            }
        });

        txtCreateAccountLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
                startActivity(intent);
            }
        });

        txtForgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgetPassActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser(){
        String email = edtEmail.getText().toString().trim();
        String pass = edtPass.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            setLoadingView(false);
            warning(context, "Lütfen E-mail adresinizi giriniz..");
            edtEmail.requestFocus();
        } else if(TextUtils.isEmpty(pass)){
            setLoadingView(false);
            warning(context, "Lütfen şifrenizi giriniz..");
            edtPass.requestFocus();
        } else {
            firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        checkEmailVerification();
                    } else {
                        setLoadingView(false);
                        Toast.makeText(context, "Giriş başarısız, lütfen tekrar deneyin."+ task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void checkEmailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser.isEmailVerified() == true){
            setLoadingView(false);
            Toast.makeText(getApplicationContext(), "Hoşgeldin!",Toast.LENGTH_LONG).show();
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();

        } else {
            setLoadingView(false);
            Toast.makeText(getApplicationContext(),"Lütfen mail adresinizi doğrulayınız.",Toast.LENGTH_LONG).show();
            firebaseAuth.signOut();
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