package com.lidorttol.opipis.ui.noInternet;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lidorttol.opipis.R;
import com.lidorttol.opipis.ui.main.MainActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class NoInternetFragment extends Fragment {


    private Button btnRetry;
    private NavController navController;
    private Button btnExit;

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
        setupViews();
    }

    private void setupViews() {
        btnRetry = ViewCompat.requireViewById(getView(), R.id.ni_btnRetry);
        btnExit = ViewCompat.requireViewById(getView(), R.id.ni_btnExit);

        navController = NavHostFragment.findNavController(NoInternetFragment.this);

        btnRetry.setOnClickListener(v -> {
            if (MainActivity.isConnected()) {
                navController.popBackStack();
            }
        });

        btnExit.setOnClickListener(v -> {
            //Limpiamos la pila porque atualmente tiene mapFragment se recargaría
            getActivity().getSupportFragmentManager().getFragments().clear();
            requireActivity().finish();
        });
    }
}
