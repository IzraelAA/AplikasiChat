package com.izrael.aplikasichat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Main2Activity extends AppCompatActivity {
private TextInputEditText editpass,editLogin;
private TextInputLayout editloginlay,editpaslay;
    private FirebaseAuth mAuth;
    private long backprassedtime;
    private Toast backToast;
    private FirebaseUser firebaseUser;
private Button  buttonLogin,Registrasi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        editloginlay = findViewById(R.id.Email_Registrasi);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        mAuth = FirebaseAuth.getInstance();
        editpaslay = findViewById(R.id.LoginPassword_registrasi);
        editLogin =findViewById(R.id.edt_login);
        editpass = findViewById(R.id.loginpaswordedt);
        buttonLogin = findViewById(R.id.button_login);
        Registrasi = findViewById(R.id.button_registrasi_Login);
        Registrasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main2Activity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editLogin.getText().toString())||TextUtils.isEmpty(editpass.getText().toString())){
                    Toast.makeText(Main2Activity.this, "Username or Password Di ISI", Toast.LENGTH_SHORT).show();
                }else if ( !validateEmail() || !validatePassword()) {
                    Toast.makeText(Main2Activity.this, "YANG BENER GOBLOK", Toast.LENGTH_SHORT).show();
                }else {
                    mAuth.signInWithEmailAndPassword(editLogin.getText().toString(),editpass.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        Intent intent =new Intent(Main2Activity.this, Menu.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }else {
                                        Toast.makeText(Main2Activity.this, "Di inget inget Lah ada yang salah noh!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null){
            Intent intent = new Intent(Main2Activity.this,Menu.class);
            startActivity(intent);
        }
    }

    private boolean validatePassword() {
        String password = editpass.getText().toString().trim();

        if (password.isEmpty()) {
            editpaslay.setError("Password Cant Be Empety");
            return false;
        }  else {
            editpaslay.setError(null);
            return true;
        }
    }

    private boolean validateEmail() {
        String emailInput = editloginlay.getEditText().getText().toString().trim();

        if (emailInput.isEmpty()) {
            editloginlay.setError("Email Can't Empety");
            return false;
        } else {
            editloginlay.setError(null);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        if (backprassedtime + 2000 > System.currentTimeMillis()){
            backToast.cancel();
            finish();
            super.onBackPressed();
        }else {
            backToast = Toast.makeText(this, "Tekan Lagi Untuk Keluar", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backprassedtime = System.currentTimeMillis();
    }
}
