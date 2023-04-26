package com.example.wisatawakatobi.features;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;

import com.google.android.material.textfield.TextInputLayout;

public class RegisterTextWatcher implements TextWatcher {
    TextInputLayout fullnameLayout, emailLayout, passwordLayout;
    private boolean emailValid = false, passwordValid = false, fullnameValid = false;

    //    private boolean userHasStartedTyping = false;
    private OnRegisterChangedListener listener;


    public RegisterTextWatcher(TextInputLayout emailLayout, TextInputLayout passwordLayout, TextInputLayout fullnameLayout) {
        this.emailLayout = emailLayout;
        this.passwordLayout = passwordLayout;
        this.fullnameLayout = fullnameLayout;
    }

    public void addOnRegisterChangedListener(OnRegisterChangedListener listener) {
        this.listener = listener;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//        if (!userHasStartedTyping) {
//            userHasStartedTyping = true;
//            return;
//        }

        String email = emailLayout.getEditText().getText().toString().trim();
        String password = passwordLayout.getEditText().getText().toString().trim();
        String fullname = fullnameLayout.getEditText().getText().toString().trim();


        if (!TextUtils.isEmpty(email)) {
            if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailValid = false;
                emailLayout.setError("Please enter a valid email address");
            } else {
                emailValid = true;
                emailLayout.setError(null);
            }
        }

        if (!TextUtils.isEmpty(password)) {
            if (TextUtils.isEmpty(password) || password.length() < 6) {
                passwordValid = false;
                passwordLayout.setError("Please enter a password with at least 6 characters");
            } else {
                passwordValid = true;
                passwordLayout.setError(null);
            }
        }


        if (TextUtils.isEmpty(fullname)) {
            fullnameValid = false;
            fullnameLayout.setError("Please enter name");
        } else {
            fullnameValid = true;
            fullnameLayout.setError(null);
        }

        if (listener != null) {
            listener.onRegisterChanged(emailValid, passwordValid, fullnameValid);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    public boolean isEmailValid() {
        return emailValid;
    }

    public boolean isPasswordValid() {
        return passwordValid;
    }

    public boolean isFullnameValid() {
        return fullnameValid;
    }

    public interface OnRegisterChangedListener {
        void onRegisterChanged(boolean emailValid, boolean passwordValid, boolean fullnameValid);
    }
}

