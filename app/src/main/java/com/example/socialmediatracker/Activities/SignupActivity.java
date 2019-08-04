package com.example.socialmediatracker.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.example.socialmediatracker.R;
import com.example.socialmediatracker.helper.AppInfo;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity{
    EditText emailEt, passwordEt, nameEt;
    Button signUpBtn;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private Sprite doubleBounce;
    private TextView trans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();

        emailEt = findViewById(R.id.sign_up_email);
        passwordEt = findViewById(R.id.sign_up_password);
        signUpBtn = findViewById(R.id.sign_up_btn);
        nameEt = findViewById(R.id.sign_up_name);
        progressBar = findViewById(R.id.progress_bar_sign);
        trans = findViewById(R.id.trans_sign);
        doubleBounce = new DoubleBounce();


        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });
    }

    private void createAccount() {
        final String name = nameEt.getText().toString();
        String mail = emailEt.getText().toString();
        String pass = passwordEt.getText().toString();

        if (name.isEmpty()){
            nameEt.setError("Enter name");
        }else if (mail.isEmpty()){
            emailEt.setError("Enter email");
        }else if (pass.isEmpty()){
            passwordEt.setError("Enter password");
        }else {
            AppInfo.hideKeyboard(SignupActivity.this);
            signUpBtn.setVisibility(View.INVISIBLE);
            trans.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminateDrawable(doubleBounce);

            mAuth.createUserWithEmailAndPassword(mail, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            trans.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            signUpBtn.setVisibility(View.VISIBLE);
                            if (task.isSuccessful()) {
                                final FirebaseUser user = mAuth.getCurrentUser();
                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(SignupActivity.this, "SignUp Successfully. Please verify your email!", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                            intent.putExtra("name",name);
                                            intent.putExtra("bool", true);
                                            startActivity(intent);
                                            finish();
                                        }else {
                                            Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });


                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(SignupActivity.this, "Authentication failed. "+task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
    }
}
