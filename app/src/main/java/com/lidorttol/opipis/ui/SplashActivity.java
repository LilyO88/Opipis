package com.lidorttol.opipis.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lidorttol.opipis.R;
import com.lidorttol.opipis.data.Banio;
import com.lidorttol.opipis.ui.main.MainActivity;
import com.lidorttol.opipis.ui.main.MainActivityViewModel;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private TextView txtNoInternet;
    private Button btnRetry;
    private ImageView logo;
    private FirebaseFirestore database;

    MainActivityViewModel viewModelActivityMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        viewModelActivityMain =  ViewModelProviders.of(this).get(MainActivityViewModel.class);
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
        txtNoInternet = ActivityCompat.requireViewById(this, R.id.splash_lblError);
        btnRetry = ActivityCompat.requireViewById(this, R.id.splash_btnRetry);
        logo = ActivityCompat.requireViewById(this, R.id.splash_logo);
        database = FirebaseFirestore.getInstance();

        btnRetry.setOnClickListener(v -> {
            try {
                isConnected();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }


/*    private void isConnected() throws InterruptedException {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // Si hay conexión a Internet en este momento
            readDatabase();
//            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//            startActivity(intent);
//            finish();
        } else {
            // No hay conexión a Internet en este momento
//            Log.d("AAAAAAAAAa", "NO HAY CONEXIÓN");
//            Toast.makeText(this, "No hay internet.", Toast.LENGTH_LONG);
            txtNoInternet.setVisibility(View.VISIBLE);
            btnRetry.setVisibility(View.VISIBLE);
        }
    }*/

    private void isConnected() throws InterruptedException {
        viewModelActivityMain.getConnected().observe(this, connected -> {
            if (connected) {
                // Si hay conexión a Internet en este momento
                readDatabase();
            } else {
                // No hay conexión a Internet en este momento
                txtNoInternet.setVisibility(View.VISIBLE);
                btnRetry.setVisibility(View.VISIBLE);
            }
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void readDatabase() {
        database.collection("banios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Banio> banios = new ArrayList<>();
//                    HashMap markers = new HashMap();
//                    Marker marker;
                    if(!task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document: task.getResult()) {
                            //Log.d("", document.getId() + " => " + document.getData());
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
//                    Toast.makeText(SplashActivity.this, "Ha habido un error al recuperar las localizaiones.", Toast.LENGTH_LONG);
                }
            }
        });

    }

}
