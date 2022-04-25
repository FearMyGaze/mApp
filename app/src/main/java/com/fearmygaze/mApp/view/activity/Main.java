package com.fearmygaze.mApp.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fearmygaze.mApp.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Main extends AppCompatActivity {

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userExists();
    }

    private void userExists(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            Task<Void> auth = user.reload();
            auth.addOnSuccessListener(unused -> {
                if (!user.isEmailVerified()){
                    showToast(getResources().getString(R.string.mainEmailVerificationError),1);
                    startActivity(new Intent(this,Starting.class));
                    finish();
                }
            }).addOnFailureListener(e -> {
                showToast(e.getMessage(),1);
                startActivity(new Intent(this,Starting.class));
                finish();
            });
        }else{
            startActivity(new Intent(this, Starting.class));
            finish();
        }
    }
    private void showToast(String message, int length){
        Toast.makeText(this, message, length).show();
    }
}