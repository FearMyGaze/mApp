package com.github.fearmygaze.mercury.view.util.AccountInteractions;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.firebase.UserActions;
import com.github.fearmygaze.mercury.firebase.interfaces.CallBackResponse;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.PrivatePreference;
import com.github.fearmygaze.mercury.view.activity.Starting;
import com.google.android.material.button.MaterialButton;

public class DeleteAccount extends AppCompatActivity {

    MaterialButton cancel, confirm;

    Bundle bundle;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        cancel = findViewById(R.id.deleteAccountCancel);
        confirm = findViewById(R.id.deleteAccountConfirm);

        bundle = getIntent().getExtras();
        if (bundle == null) finish();
        user = bundle.getParcelable(User.PARCEL);
        if (user == null) finish();

        cancel.setOnClickListener(v -> onBackPressed());

        new Handler().postDelayed(() -> confirm.setEnabled(true), 2345);

        confirm.setOnClickListener(v -> {
            new UserActions(v.getContext()).deleteAccount(new CallBackResponse<String>() {
                @Override
                public void onSuccess(String message) {
                    User.deleteRoomUser(user, DeleteAccount.this);
                    new PrivatePreference(v.getContext()).clearAllValues();
                    Intent intent = new Intent(DeleteAccount.this, Starting.class); //TODO: this resets the activity stack
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    Toast.makeText(DeleteAccount.this, message, Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(DeleteAccount.this, message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(DeleteAccount.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        });

    }


}
