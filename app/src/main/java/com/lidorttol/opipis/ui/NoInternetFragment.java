package com.lidorttol.opipis.ui;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lidorttol.opipis.R;
import com.lidorttol.opipis.ui.main.MainActivityViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class NoInternetFragment extends Fragment {


    private Button btnRetry;
    private MainActivityViewModel viewModelActivityMain;
    private NavController navController;

    public NoInternetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_no_internet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();
    }

    private void setupViews() {
        btnRetry = ViewCompat.requireViewById(getView(), R.id.ni_btnRetry);
        viewModelActivityMain = ViewModelProviders.of(requireActivity()).get(MainActivityViewModel.class);

        navController = NavHostFragment.findNavController(NoInternetFragment.this);

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    isConnected();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void isConnected() throws InterruptedException {
        viewModelActivityMain.getConnected().observe(this, connected -> {
            if (connected) {
                // Si hay conexión a Internet en este momento
                navController.popBackStack();
            }
        });
    }
}
