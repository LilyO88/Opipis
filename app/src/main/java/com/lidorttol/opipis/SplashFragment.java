package com.lidorttol.opipis;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidorttol.opipis.ui.MainActivity;

import static androidx.core.content.ContextCompat.getCodeCacheDir;
import static androidx.core.content.ContextCompat.getSystemService;


/**
 * A simple {@link Fragment} subclass.
 */
public class SplashFragment extends Fragment {

    TextView txtNoInternet;
    Button exitButton;
    ImageView background;

    public SplashFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        getActivity().setTheme(R.style.AppTheme_NoActionBar);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();
        isConnected();
    }

    private void setupViews() {
        txtNoInternet = ViewCompat.requireViewById(getView(), R.id.txtNoInternet);
        exitButton = ViewCompat.requireViewById(getView(), R.id.exitApp);
        background = ViewCompat.requireViewById(getView(), R.id.logo);

        exitButton.setOnClickListener(v -> getActivity().finish());
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void isConnected() {
        Toast.makeText(getContext(), "ENTRA", Toast.LENGTH_LONG);

        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // Si hay conexión a Internet en este momento
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_splashFragment_to_mapFragment);
            Toast.makeText(getContext(), "Hay internet.", Toast.LENGTH_LONG);

        } else {
            // No hay conexión a Internet en este momento
            Log.d("AAAAAAAAAa", "NO HAY CONEXIÓN");
            Toast.makeText(getContext(), "No hay internet.", Toast.LENGTH_LONG);
            txtNoInternet.setVisibility(View.VISIBLE);
            exitButton.setVisibility(View.VISIBLE);
        }
    }


}
