package com.topurayhan.authenticationapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    static EditText name, emailAddress1, password1;
    @SuppressLint("StaticFieldLeak")
    static Button signupButton;
    @SuppressLint("StaticFieldLeak")
    static TextView login;

    ProgressDialog progressDialog;
    boolean passwordVisible;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z.]+";
    private FirebaseAuth mAuth;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_signup);
        
        password1 = findViewById(R.id.password1);
        name = findViewById(R.id.name);
        emailAddress1 = findViewById(R.id.emailAddress1);
        signupButton = findViewById(R.id.signupButton);
        name = findViewById(R.id.name);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        login = findViewById(R.id.login);


        password1.setOnTouchListener((view, motionEvent) -> {
            final int Right = 2;
            if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                if (motionEvent.getRawX() >= password1.getRight() - password1.getCompoundDrawables()[Right].getBounds().width()){
                    int selection = password1.getSelectionEnd();
                    if(passwordVisible){
                        password1.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_lock_24, 0, R.drawable.ic_baseline_visibility_off_24, 0);
                        password1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        passwordVisible = false;
                    }
                    else{
                        password1.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_lock_24, 0, R.drawable.ic_baseline_visibility_24, 0);
                        password1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        passwordVisible = true;
                    }
                    password1.setSelection(selection);
                    return true;
                }
            }
            return false;
        });

        signupButton.setOnClickListener(view -> performAuth());

        login.setOnClickListener(view -> openLoginActivity());
    }

    private void performAuth() {
        //String fullName = name.getText().toString();
        String email = emailAddress1.getText().toString();
        String password = password1.getText().toString();

        if (!email.matches(emailPattern)){
            Log.d("TAG", "Enter a valid email!");
            emailAddress1.setError("Enter a valid email!");
        }
        else if (password.isEmpty() || password.length() < 8){
            Log.d("TAG", "Enter a valid email!");
            password1.setError("Enter a valid password!");
        }
        else {
            progressDialog.setMessage("Please wait...");
            progressDialog.setTitle("Registration");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "createUserWithEmail:success");
                    //updateUI(user);
                    openLoginActivity();
                    Toast.makeText(SignUpActivity.this, "Successfully registered!", Toast.LENGTH_SHORT).show();
                }
                else{
                    progressDialog.dismiss();
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "createUserWithEmail:failure", task.getException());
                    Toast.makeText(SignUpActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                    //updateUI(null);
                }
            });
        }
    }


    public void openLoginActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(15);
    }
}
