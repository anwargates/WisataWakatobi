package com.example.wisatawakatobi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wisatawakatobi.features.RegisterTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    TextInputLayout fullName, email, password;
    Button registerBtn;
    TextView goToLogin;
    boolean valid = true;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    RegisterTextWatcher registerTextWatcher;

    ProgressDialog loadingDialog;
    MaterialAlertDialogBuilder errorDialog;
    TextView tvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tvVersion = findViewById(R.id.tvVersionRegister);
        String version =
                "Version " + BuildConfig.VERSION_NAME;
        tvVersion.setText(version);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        fullName = findViewById(R.id.registerFullnameLayout);
        email = findViewById(R.id.registerEmailLayout);
        password = findViewById(R.id.registerPasswordLayout);
        registerBtn = findViewById(R.id.submitBtn);
        goToLogin = findViewById(R.id.tvToLogin);

        loadingDialog = new ProgressDialog(Register.this);
        errorDialog = new MaterialAlertDialogBuilder(Register.this);

        // Disable the submit button initially
        registerBtn.setEnabled(false);

        // error handling
        registerTextWatcher = new RegisterTextWatcher(email, password, fullName);
        email.getEditText().addTextChangedListener(registerTextWatcher);
        password.getEditText().addTextChangedListener(registerTextWatcher);
        fullName.getEditText().addTextChangedListener(registerTextWatcher);


        // Add a listener to enable/disable the submit button as the fields are updated
        registerTextWatcher.addOnRegisterChangedListener(new RegisterTextWatcher.OnRegisterChangedListener() {
            @Override
            public void onRegisterChanged(boolean emailValid, boolean passwordValid, boolean fullnameValid) {
                registerBtn.setEnabled(emailValid && passwordValid && fullnameValid);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog.setCancelable(false);
                loadingDialog.setTitle("Mohon Tunggu");
                loadingDialog.setMessage("Sedang Membuat Akun");
                loadingDialog.show();


                if (registerTextWatcher.isEmailValid() && registerTextWatcher.isPasswordValid() && registerTextWatcher.isFullnameValid()) {
                    // start user registration
                    fAuth.createUserWithEmailAndPassword(email.getEditText().getText().toString(), password.getEditText().getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user = fAuth.getCurrentUser();
//                            Toast.makeText(Register.this, "Account Created", Toast.LENGTH_SHORT).show();
                            DocumentReference df = fStore.collection("Users").document(user.getUid());
                            Map<String, Object> userInfo = new HashMap<>();
                            userInfo.put("FullName", fullName.getEditText().getText().toString());
                            userInfo.put("UserEmail", email.getEditText().getText().toString());
                            userInfo.put("favorites", new ArrayList<String>());

                            // specify if the user is admin or not
                            userInfo.put("isAdmin", "0");

                            df.set(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finish();
                                    loadingDialog.dismiss();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
//                            String message = "Register Failed: " + e.toString().substring(e.toString().indexOf(":") + 1);
//                            Toast.makeText(Register.this, message, Toast.LENGTH_LONG).show();
                            String message = e.getMessage();
                            loadingDialog.dismiss();
                            errorDialog.setTitle("Register Failed").setMessage(message).show();
                        }
                    });
                }
            }
        });

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(getApplicationContext(), Login.class));
//                finish();
                onBackPressed();
            }
        });
    }

//    public boolean checkField(TextInputEditText textField) {
//        if (textField.getText().toString().isEmpty()) {
//            textField.setError("Error");
//            valid = false;
//        } else {
//            valid = true;
//        }
//
//        return valid;
//    }
}