package com.lidorttol.opipis.ui.splash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.lidorttol.opipis.R;
import com.lidorttol.opipis.data.Banio;
import com.lidorttol.opipis.ui.main.MainActivity;
import com.lidorttol.opipis.ui.main.MainActivityViewModel;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private TextView txtNoInternet;
    private Button btnRetry;
    private FirebaseFirestore database;

    MainActivityViewModel viewModelActivityMain;
    private static Application application;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        viewModelActivityMain = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        application = getApplication();
        setupViews();
        setSplash();
    }

    private void setSplash() {
        new Handler().postDelayed(() -> {
            if (isConnected()) {
                readDatabase();
            } else {
                // No hay conexión a Internet en este momento
                txtNoInternet.setVisibility(View.VISIBLE);
                btnRetry.setVisibility(View.VISIBLE);
            }
        }, 1000);
    }


    private void setupViews() {
        txtNoInternet = ActivityCompat.requireViewById(this, R.id.splash_lblError);
        btnRetry = ActivityCompat.requireViewById(this, R.id.splash_btnRetry);
        database = FirebaseFirestore.getInstance();

        btnRetry.setOnClickListener(v -> {
            if (isConnected()) {
                readDatabase();
            }
        });
    }

    public static boolean isConnected() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            connected = true;
        }

        return connected;
    }

    private void navigateToMain() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void readDatabase() {
        database.collection("banios").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Banio> banios = new ArrayList<>();
                if (!task.getResult().isEmpty()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Banio banio = document.toObject(Banio.class);
                        banios.add(banio);
                    }
                    viewModelActivityMain.setBaniosLiveData(banios);
                    navigateToMain();

                } else {
                    txtNoInternet.setText("No hay baños registrados,\n ¡Sé el primero!");
                    txtNoInternet.setVisibility(View.VISIBLE);
                    btnRetry.setText("ENTRAR");
                    btnRetry.setVisibility(View.VISIBLE);
                    btnRetry.setOnClickListener(v -> navigateToMain());
                }
            } else {
                Log.w("", "Error getting documents.", task.getException());
                txtNoInternet.setText("Error al recuperar los datos.");
                txtNoInternet.setVisibility(View.VISIBLE);
                btnRetry.setVisibility(View.VISIBLE);
            }
        });

    }

}
