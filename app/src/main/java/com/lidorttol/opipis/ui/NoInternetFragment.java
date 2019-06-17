package com.lidorttol.opipis.ui;


import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lidorttol.opipis.R;
import com.lidorttol.opipis.ui.main.MainActivity;
import com.lidorttol.opipis.ui.main.MainActivityViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class NoInternetFragment extends Fragment {


    private Button btnRetry;
    private MainActivityViewModel viewModelActivityMain;
    private NavController navController;
    private Button btnExit;
//    private boolean connected;

    public NoInternetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
        viewModelActivityMain = ViewModelProviders.of(requireActivity()).get(MainActivityViewModel.class);
/*        try {
            observeConexion();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        setupViews();
    }

    private void setupViews() {
        btnRetry = ViewCompat.requireViewById(getView(), R.id.ni_btnRetry);
        btnExit = ViewCompat.requireViewById(getView(), R.id.ni_btnExit);
//        connected = false;

        navController = NavHostFragment.findNavController(NoInternetFragment.this);

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.isConnected()) {
                    navController.popBackStack();
                }
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Limpiamos la pila porque atualmente tiene mapFragment se recargaría
                getActivity().getSupportFragmentManager().getFragments().clear();
                requireActivity().finish();
            }
        });
    }

/*    private void observeConexion() throws InterruptedException {
        viewModelActivityMain.getConnected().observe(this, connected -> {
            this.connected = connected;
        });
    }*/

/*    @Override
    public void onDestroy() {
        if(MainActivity.isConnected()) {
            navController.popBackStack();
            super.onDestroy();
        } *//*else {
            navController.popBackStack();
            navController.navigate(R.id.action_mapFragment_to_noInternetFragment);
        }*//*
    }*/

    /*@Override //NO FUNCIONA FUNCIÓN ATRÁS
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        OnBackPressedCallback callback = new OnBackPressedCallback(
                true // default to enabled
        ) {
            @Override
            public void handleOnBackPressed() {
                if (MainActivity.isConnected()) {
                    navController.popBackStack();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(), // LifecycleOwner
                callback);
    }
*/

}
