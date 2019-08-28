package com.example.socialmediatracker.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmediatracker.R;
import com.example.socialmediatracker.helper.AppInfo;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    EditText emailEt, passwordEt;
    ImageButton loginBtn, signUpBtn;
    TextView trans, forgotPassTv;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private Sprite doubleBounce;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        emailEt = findViewById(R.id.login_email);
        passwordEt = findViewById(R.id.login_password);
        loginBtn = findViewById(R.id.login_btn);
        signUpBtn = findViewById(R.id.create_account_txt);
        progressBar = findViewById(R.id.progress_bar_login);
        forgotPassTv = findViewById(R.id.forgot_pass_txt);
        trans = findViewById(R.id.trans_login);
        doubleBounce = new DoubleBounce();

        mAuth = FirebaseAuth.getInstance();

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEt.getText().toString();
                String pass = passwordEt.getText().toString();


                if (email.isEmpty()){
                    emailEt.setError("Enter email");
                }else if (pass.isEmpty()){
                    passwordEt.setError("Enter password");
                }else {
                    AppInfo.hideKeyboard(LoginActivity.this);
                    loginBtn.setVisibility(View.INVISIBLE);
                    trans.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setIndeterminateDrawable(doubleBounce);

                    mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            trans.setVisibility(View.GONE);
                            loginBtn.setVisibility(View.VISIBLE);
                            if (task.isSuccessful()){
                                if (mAuth.getCurrentUser().isEmailVerified()){
                                   if (getIntent().getBooleanExtra("bool",false)){
                                       String UID = mAuth.getCurrentUser().getUid();
                                       databaseReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.user)).child(UID);
                                       String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                       HashMap<String, String> userMap = new HashMap<>();
                                       userMap.put(getString(R.string.name), getIntent().getStringExtra("name"));
                                       userMap.put("device_token", deviceToken);

                                       databaseReference.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               if (task.isSuccessful()){
                                                   Log.v("SignUp", "SignUp and data saved successfully!");
                                               }else {
                                                   // If sign in fails, display a message to the user.
                                                   Log.v("SignUp", task.getException().getMessage());
                                               }
                                           }
                                       });
                                   }

                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                   finish();
                                }else {
                                    Toast.makeText(LoginActivity.this, "Please verify your email first!", Toast.LENGTH_LONG).show();
                                }
                            }else {
                                Toast.makeText(LoginActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        forgotPassTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void showDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(LoginActivity.this);

        final Sprite doubleBounceDia;
        final View dialogView = LayoutInflater.from(LoginActivity.this).inflate(R.layout.forgot_pass_dialog, null);

        final EditText emailEt = dialogView.findViewById(R.id.email_pass);
        final Button resetBtn = dialogView.findViewById(R.id.send_mail_pass);
        final ProgressBar progressBarDia = dialogView.findViewById(R.id.progress_bar_pass);
        final TextView transDia = dialogView.findViewById(R.id.trans_pass);

        doubleBounceDia = new DoubleBounce();
        mBuilder.setView(dialogView);
        final AlertDialog dialog = mBuilder.create();
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEt.getText().toString();
                if (email.isEmpty()){
                    emailEt.setError("Please enter your email");
                }else {
                    resetBtn.setVisibility(View.INVISIBLE);
                    transDia.setVisibility(View.VISIBLE);
                    progressBarDia.setVisibility(View.VISIBLE);
                    progressBarDia.setIndeterminateDrawable(doubleBounceDia);
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressBarDia.setVisibility(View.GONE);
                            transDia.setVisibility(View.GONE);
                            resetBtn.setVisibility(View.VISIBLE);
                            if (task.isSuccessful()){
                                dialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Reset link send to your email!", Toast.LENGTH_LONG).show();
                            }else {
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

            }
        });

        dialog.show();
    }
}
