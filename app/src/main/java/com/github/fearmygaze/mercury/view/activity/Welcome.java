package com.github.fearmygaze.mercury.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.fearmygaze.mercury.R;
import com.google.android.material.button.MaterialButton;

public class Welcome extends AppCompatActivity {

    MaterialButton signIn, signUp;
    TypedValue typedValue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        signUp = findViewById(R.id.welcomeSignUp);
        signIn = findViewById(R.id.welcomeSignIn);

        typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.colorPrimary, typedValue, true);

        SpannableStringBuilder spannableBuilder = new SpannableStringBuilder();
        spannableBuilder.append(String.format("%s ", getString(R.string.welcomeHaveAccount) + ""));
        int start = spannableBuilder.length();
        spannableBuilder.append(getString(R.string.welcomeHaveAccountP2));
        int end = spannableBuilder.length();
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(getColor(typedValue.resourceId));
        spannableBuilder.setSpan(colorSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        signIn.setText(spannableBuilder);

        signUp.setOnClickListener(v -> {
            startActivity(new Intent(Welcome.this, SignUp.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        signIn.setOnClickListener(v -> {
            startActivity(new Intent(Welcome.this, SignIn.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

    }
}
