package com.example.socialmediatracker.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.example.socialmediatracker.ProgressGenerator;
import com.example.socialmediatracker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.libizo.CustomEditText;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity implements ProgressGenerator.OnCompleteListener{
    CustomEditText emailEt, passwordEt, nameEt;
    ActionProcessButton signUpBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();

        final ProgressGenerator progressGenerator = new ProgressGenerator(SignupActivity.this);
        emailEt = findViewById(R.id.sign_up_email);
        passwordEt = findViewById(R.id.sign_up_password);
        signUpBtn = findViewById(R.id.sign_up_btn);
        nameEt = findViewById(R.id.sign_up_name);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount(progressGenerator);
            }
        });
    }

    private void createAccount(ProgressGenerator progressGenerator) {
        final String name = nameEt.getText().toString();
        String mail = emailEt.getText().toString();
        String pass = passwordEt.getText().toString();

        if (name.isEmpty()){
            nameEt.setError("Enter name");
            nameEt.setBorderColor(Color.RED);
        }else if (mail.isEmpty()){
            emailEt.setError("Enter email");
            emailEt.setBorderColor(Color.RED);
        }else if (pass.isEmpty()){
            passwordEt.setError("Enter password");
            passwordEt.setBorderColor(Color.RED);
        }else {
            progressGenerator.start(signUpBtn);
            signUpBtn.setEnabled(false);
            emailEt.setEnabled(false);
            passwordEt.setEnabled(false);
            mAuth.createUserWithEmailAndPassword(mail, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                String UID = user.getUid();
                                databaseReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.user)).child(UID);
                                String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                HashMap<String, String> userMap = new HashMap<>();
                                userMap.put(getString(R.string.name), name);
                                userMap.put("device_token", deviceToken);

                                databaseReference.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            startActivity(new Intent(SignupActivity.this, Main2Activity.class));
                                        }else {
                                            // If sign in fails, display a message to the user.
                                            Toast.makeText(SignupActivity.this, task.getException().getMessage(),
                                                    Toast.LENGTH_SHORT).show();
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
    public void onComplete() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
    }
}
