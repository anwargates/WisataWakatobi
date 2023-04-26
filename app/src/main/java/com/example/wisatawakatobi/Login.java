package com.example.wisatawakatobi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wisatawakatobi.features.CredentialsTextWatcher;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class Login extends AppCompatActivity {
    TextInputLayout email, password;
    Button loginBtn;
    TextView gotoRegister;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    CredentialsTextWatcher credentialsTextWatcher;

    ProgressDialog loadingDialog;
    MaterialAlertDialogBuilder errorDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        email = findViewById(R.id.registerEmailLayout);
        password = findViewById(R.id.registerPasswordLayout);
        loginBtn = findViewById(R.id.loginBtn);
        gotoRegister = findViewById(R.id.tvDaftar);
        loadingDialog = new ProgressDialog(Login.this);
        errorDialog = new MaterialAlertDialogBuilder(Login.this);

        // Disable the submit button initially
        loginBtn.setEnabled(false);

        // error handling
        credentialsTextWatcher = new CredentialsTextWatcher(email, password);
        email.getEditText().addTextChangedListener(credentialsTextWatcher);
        password.getEditText().addTextChangedListener(credentialsTextWatcher);

        // Add a listener to enable/disable the submit button as the fields are updated
        credentialsTextWatcher.addOnCredentialsChangedListener(new CredentialsTextWatcher.OnCredentialsChangedListener() {
            @Override
            public void onCredentialsChanged(boolean emailValid, boolean passwordValid) {
                loginBtn.setEnabled(emailValid && passwordValid);
            }
        });


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog.setCancelable(false);
                loadingDialog.setTitle("Mohon Tunggu");
                loadingDialog.setMessage("Sedang Login");
                loadingDialog.show();


                if (credentialsTextWatcher.isEmailValid() && credentialsTextWatcher.isPasswordValid()) {
                    // Both fields are valid, do something here
                    fAuth.signInWithEmailAndPassword(email.getEditText().getText().toString().trim(), password.getEditText().getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
//                            Toast.makeText(Login.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                            checkUserAccessLevel(authResult.getUser().getUid());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
//                            String message = "Login Failed: " + e.toString().substring(e.toString().indexOf(":") + 1).trim();
//                            Toast.makeText(Login.this, message, Toast.LENGTH_LONG).show();

                            String message = e.getMessage();
                            loadingDialog.dismiss();
                            errorDialog.setTitle("Login Failed").setMessage(message).show();
                        }
                    });
                } else {
                    // Display an error message
                    errorDialog.setTitle("Alert").setMessage("Please enter valid email and password").show();

//                    Toast.makeText(Login.this, "Please enter valid email and password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        gotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });


    }

    private void checkUserAccessLevel(String uid) {
        DocumentReference df = fStore.collection("Users").document(uid);

        // extract data from the document
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("GET USER INFO", "onSuccess: " + documentSnapshot.getString("isAdmin"));
                // identify the user access level
                if (Objects.equals(documentSnapshot.getString("isAdmin"), "1")) {
                    // user is admin
                    Log.d("USER CHECK", "Welcome to Admin Dashboard");
                    startActivity(new Intent(getApplicationContext(), Admin.class));
                    finish();
                } else {
                    // user is not admin
                    Log.d("USER CHECK", "Welcome to User Dashboard");
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
                loadingDialog.dismiss();
            }
        });
    }


//    public boolean checkField(TextInputLayout textField) {
//        if (textField.getEditText().getText().toString().isEmpty()) {
//            textField.setError("Cannot be empty");
//            valid = false;
//        } else {
//            if (textField.getEditText().getText().toString().length() < 6) {
//                textField.setError("Password must be 6 characters or more");
//                valid = false;
//            } else {
//                valid = true;
//            }
//        }
//        return valid;
//    }
}