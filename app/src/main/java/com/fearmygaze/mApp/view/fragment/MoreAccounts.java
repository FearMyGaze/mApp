package com.fearmygaze.mApp.view.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.model.User1;
import com.fearmygaze.mApp.view.adapter.AdapterMoreAccounts;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MoreAccounts extends BottomSheetDialogFragment {

    View view;

    ConstraintLayout root;

    RecyclerView recyclerView;

    MaterialButton addExisting, createNew;

    List<User1> users;

    AdapterMoreAccounts accounts;

    ViewGroup.LayoutParams params;

    public MoreAccounts(ViewGroup.LayoutParams params){
        this.params = params;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bottom_sheet_more_accounts, container, false);

        root = view.findViewById(R.id.moreAccountsRoot);
        recyclerView = view.findViewById(R.id.moreAccountsRecycler);
        addExisting = view.findViewById(R.id.moreAccountsAddExisting);
        createNew = view.findViewById(R.id.moreAccountsCreateNew);

        root.setLayoutParams(params);

        users = new ArrayList<User1>();

        users.add(new User1(11, "asd0", "http://192.168.1.5:3000/images/profile/image.png", "asd0@email.com"));
        users.add(new User1(20, "asd1", "http://192.168.1.5:3000/images/profile/image.png", "asd1@email.com"));
        users.add(new User1(22, "asd1", "http://192.168.1.5:3000/images/profile/image.png", "asd1@email.com"));
        users.add(new User1(22, "asd1", "http://192.168.1.5:3000/images/profile/image.png", "asd1@email.com"));
        users.add(new User1(22, "asd1", "http://192.168.1.5:3000/images/profile/image.png", "asd1@email.com"));
        users.add(new User1(22, "asd1", "http://192.168.1.5:3000/images/profile/image.png", "asd1@email.com"));
        users.add(new User1(22, "asd1", "http://192.168.1.5:3000/images/profile/image.png", "asd1@email.com"));
        users.add(new User1(22, "asd1", "http://192.168.1.5:3000/images/profile/image.png", "asd1@email.com"));
        users.add(new User1(22, "asd1", "http://192.168.1.5:3000/images/profile/image.png", "asd1@email.com"));
        users.add(new User1(22, "asd1", "http://192.168.1.5:3000/images/profile/image.png", "asd1@email.com"));
        users.add(new User1(22, "asd1", "http://192.168.1.5:3000/images/profile/image.png", "asd1@email.com"));
        users.add(new User1(22, "asd1", "http://192.168.1.5:3000/images/profile/image.png", "asd1@email.com"));
        users.add(new User1(22, "asd1", "http://192.168.1.5:3000/images/profile/image.png", "asd1@email.com"));
        users.add(new User1(22, "asd1", "http://192.168.1.5:3000/images/profile/image.png", "asd1@email.com"));
        users.add(new User1(22, "asd1", "http://192.168.1.5:3000/images/profile/image.png", "asd1@email.com"));

        accounts = new AdapterMoreAccounts(users, 20, pos -> {
            Toast.makeText(view.getContext(), accounts.getUser(pos).getEmail(), Toast.LENGTH_LONG).show();
            dismiss();
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(accounts);

        return view;
    }


    @Override
    public int getTheme() {
        return R.style.CustomBottomSheetDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        ((BottomSheetDialog) dialog).getBehavior().addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    MaterialShapeDrawable newMaterialShapeDrawable = createMaterialShapeDrawable(bottomSheet);
                    ViewCompat.setBackground(bottomSheet, newMaterialShapeDrawable);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        return dialog;
    }

    @NotNull
    private MaterialShapeDrawable createMaterialShapeDrawable(@NonNull View bottomSheet) {
        ShapeAppearanceModel shapeAppearanceModel =

                //Create a ShapeAppearanceModel with the same shapeAppearanceOverlay used in the style
                ShapeAppearanceModel.builder(getContext(), 0, R.style.CustomShapeAppearanceBottomSheetDialog)
                        .build();

        //Create a new MaterialShapeDrawable (you can't use the original MaterialShapeDrawable in the BottomSheet)
        MaterialShapeDrawable currentMaterialShapeDrawable = (MaterialShapeDrawable) bottomSheet.getBackground();
        MaterialShapeDrawable newMaterialShapeDrawable = new MaterialShapeDrawable((shapeAppearanceModel));
        //Copy the attributes in the new MaterialShapeDrawable
        newMaterialShapeDrawable.initializeElevationOverlay(getContext());
        newMaterialShapeDrawable.setFillColor(currentMaterialShapeDrawable.getFillColor());
        newMaterialShapeDrawable.setTintList(currentMaterialShapeDrawable.getTintList());
        newMaterialShapeDrawable.setElevation(currentMaterialShapeDrawable.getElevation());
        newMaterialShapeDrawable.setStrokeWidth(currentMaterialShapeDrawable.getStrokeWidth());
        newMaterialShapeDrawable.setStrokeColor(currentMaterialShapeDrawable.getStrokeColor());
        return newMaterialShapeDrawable;
    }
}