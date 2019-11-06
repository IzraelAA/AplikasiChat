package com.izrael.aplikasichat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private TextInputLayout textInputLayoutemail, textInputLayoutusername, textInputLayoutpassword;
    private              Button       buttonLogin;
    private static final int          RC_SIGN_IN = 100;
    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textInputLayoutusername = findViewById(R.id.userName);
        textInputLayoutemail = findViewById(R.id.Email_Registrasi);
        textInputLayoutpassword = findViewById(R.id.Password_registrasi);
        buttonLogin = findViewById(R.id.button_registrasi);
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);


        mAuth = FirebaseAuth.getInstance();
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailInput = textInputLayoutemail.getEditText().getText().toString();
                String password   = textInputLayoutpassword.getEditText().getText().toString();
                String username   = textInputLayoutusername.getEditText().getText().toString();
                if (TextUtils.isEmpty(emailInput) || TextUtils.isEmpty(password) || TextUtils.isEmpty(username)) {
                    Toast.makeText(MainActivity.this, "DI ISI BAMBANG!!!!", Toast.LENGTH_SHORT).show();
                }  else if (!validateEmail() || !validateUserName() || !validatePassword()) {
                    Toast.makeText(MainActivity.this, "YANG BENER GOBLOK", Toast.LENGTH_SHORT).show();
                }else{
                    confrimInput(username, emailInput, password);
                }
            }
        });
    }

    private boolean validateEmail() {
        String emailInput = textInputLayoutemail.getEditText().getText().toString().trim();

        if (emailInput.isEmpty()) {
            textInputLayoutemail.setError("Email Can't Empety");
            return false;
        } else {
            textInputLayoutemail.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String password = textInputLayoutpassword.getEditText().getText().toString().trim();

        if (password.isEmpty()) {
            textInputLayoutpassword.setError("Password Can't Empety");
            return false;
        } else {
            textInputLayoutpassword.setError(null);
            return true;
        }
    }

    private boolean validateUserName() {
        String username = textInputLayoutusername.getEditText().getText().toString().trim();
        if (username.isEmpty()) {
            textInputLayoutusername.setError("Username Can't Be Empety");
            return false;
        } else if (username.length() > 15) {
            textInputLayoutusername.setError("Username Too Long");
            return false;
        } else {
            textInputLayoutusername.setError(null);
            return true;
        }
    }

    public void confrimInput(final String username, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            assert firebaseUser != null;
                            String userid = firebaseUser.getUid();
                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                            HashMap<String, String> hashmap = new HashMap<>();
                            hashmap.put("id", userid);
                            hashmap.put("username", username);
                            hashmap.put("imageUrl", "default");
                            hashmap.put("status", "offline");
                            hashmap.put("search", username.toLowerCase());
                            reference.setValue(hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Nah Gitu Pinter", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(MainActivity.this, Menu.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(MainActivity.this, "Tidak Bisa Login Dengan Email Ini", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("", "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user   = mAuth.getCurrentUser();
                            Intent       intent = new Intent(MainActivity.this, Menu.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
//                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Login Failed....", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MainActivity.this, Main2Activity.class);
        startActivity(intent);
    }
}
