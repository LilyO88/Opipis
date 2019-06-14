package com.lidorttol.opipis.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.ViewCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidorttol.opipis.R;

public class SplashActivity extends AppCompatActivity {

    TextView txtNoInternet;
    Button exitButton;
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setupViews();
        setSplash();
    }

    private void setSplash() {
        new Handler().postDelayed(() -> {
            try {
                isConnected();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 1000);
    }


    private void setupViews() {
        txtNoInternet = ActivityCompat.requireViewById(this, R.id.txtNoInternet);
        exitButton = ActivityCompat.requireViewById(this, R.id.exitApp);
        logo = ActivityCompat.requireViewById(this, R.id.logo);

        exitButton.setOnClickListener(v -> this.finish());
    }


    private void isConnected() throws InterruptedException {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // Si hay conexión a Internet en este momento
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            // No hay conexión a Internet en este momento
//            Log.d("AAAAAAAAAa", "NO HAY CONEXIÓN");
            Toast.makeText(this, "No hay internet.", Toast.LENGTH_LONG);
            txtNoInternet.setVisibility(View.VISIBLE);
            exitButton.setVisibility(View.VISIBLE);
        }
    }
}
