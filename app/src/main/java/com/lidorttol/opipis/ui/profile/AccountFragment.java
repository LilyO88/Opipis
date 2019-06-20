package com.lidorttol.opipis.ui.profile;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.lidorttol.opipis.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {


    private FirebaseAuth fAuth;
    private NavController navController;
    private TextView lblEdit;
    private TextView lblChange;
    private TextView lblClose;

    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fAuth = FirebaseAuth.getInstance();
        navController = NavHostFragment.findNavController(AccountFragment.this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (fAuth.getCurrentUser() == null) {
            navController.popBackStack();
            navController.navigate(R.id.loginFragment);
        }
        setupViews();
    }

    private void setupViews() {
        lblEdit = ViewCompat.requireViewById(getView(), R.id.acc_lblEdit);
        lblChange = ViewCompat.requireViewById(getView(), R.id.acc_lblChange);
        lblClose = ViewCompat.requireViewById(getView(), R.id.acc_lblClose);

        setupListeners();
    }

    private void setupListeners() {
        lblEdit.setOnClickListener(v -> navController.navigate(R.id.editProfileFragment));
        lblChange.setOnClickListener(v -> navController.navigate(R.id.changePasswordFragment));
        lblClose.setOnClickListener(v -> {
            fAuth.signOut();
            navController.popBackStack();
        });
    }
}
