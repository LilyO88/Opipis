package com.lidorttol.opipis.ui.map;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lidorttol.opipis.R;
import com.lidorttol.opipis.base.YesNoDialogFragment;
import com.lidorttol.opipis.data.Banio;
import com.lidorttol.opipis.data.Opinion;
import com.lidorttol.opipis.ui.main.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapFragment extends Fragment implements OnMapReadyCallback, YesNoDialogFragment.Listener {

    private static final int RQ_PERMISOS = 1;
    private static final String[] PERMISOS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final String TAG_DIALOG_FRAGMENT = "TAG_DIALOG_FRAGMENT";
    private static final int RC_DIALOG_FRAGMENT = 1;

    private FirebaseFirestore database;
    private NavController navController;
    private MapFragmentViewModel viewModelMapFragment;

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private Marker marker;
    private CameraPosition cameraPosition;

    private TextView lblMore;
    private TextView lblDirection;
    private ConstraintLayout cl_window;
    private RatingBar ratBath;
    private Button btnAddNewBath;

    private LatLng sanroque;
    private LatLng locationClickMap;
    private String lastID;
    private List<Opinion> listOpinions;
    private List<Banio> listBanios;
    private FirebaseAuth fAuth;

    public MapFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void updateBaths() {
        for(Banio banio: listBanios) {
            double globales = 0;
            double global = 0;
            int totalOpinions = 0;
            for(Opinion opinion: listOpinions) {
                if(opinion.getId_banio().equals(banio.getId_banio())) {
                    globales += opinion.getGlobal();
                    totalOpinions++;
                }
            }
            global = globales / totalOpinions;
            updateGlobalBath(banio.getId_banio(), global);
        }
    }

    private void updateGlobalBath(String id_banio, double global) {
        database.collection("banios").document(id_banio).update("puntuacion", global)
                .addOnSuccessListener(aVoid -> Log.d("", "DocumentSnapshot successfully updated!"))
                .addOnFailureListener(e -> Log.d("", "Error updating document", e));
    }

    private void recoverBaths() {
        database.collection("banios").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                viewModelMapFragment.setBaniosLiveData(task.getResult().toObjects(Banio.class));
            }
        });
    }

    private void recoverOpinions() {
        database.collection("opiniones").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                viewModelMapFragment.setOpinionsLiveData(task.getResult().toObjects(Opinion.class));
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {

        fAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        viewModelMapFragment = ViewModelProviders.of(this).get(MapFragmentViewModel.class);
        listOpinions = new ArrayList<>();
        listBanios = new ArrayList<>();
        recoverOpinions();
        recoverBaths();
        observeOpinions();
        observeBaths();

        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = NavHostFragment.findNavController(MapFragment.this);
        mMapView = ViewCompat.requireViewById(requireView(), R.id.map);

        if (MainActivity.isConnected()) {
            if (mMapView != null) {
                mMapView.onCreate(savedInstanceState);
                mMapView.onResume(); // Necesario para obtener el mapa para mostrar inmediatamente

                mMapView.getMapAsync(this);
            }
        } else {
            navController.navigate(R.id.action_mapFragment_to_noInternetFragment);
        }
        //Solicitud de permiso de localización
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            MapFragment.this.requestPermissions(PERMISOS, RQ_PERMISOS);
        }
        setupViews();
    }

    private void observeBaths() {
        viewModelMapFragment.getBaniosLiveData().observe(getViewLifecycleOwner(), banios -> {
            listBanios = banios;
            updateBaths();
        });
    }

    private void observeOpinions() {
        viewModelMapFragment.getOpinionsLiveData().observe(getViewLifecycleOwner(), opinions -> {
            listOpinions = opinions;
            updateBaths();
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void setupViews() {
        btnAddNewBath = ViewCompat.requireViewById(getView(), R.id.map_btnAddBath);
        lblMore = ViewCompat.requireViewById(requireView(), R.id.wind_lblMore);
        lblDirection = ViewCompat.requireViewById(requireView(), R.id.wind_lblDirection);
        cl_window = ViewCompat.requireViewById(requireView(), R.id.cl_window_map);
        ratBath = ViewCompat.requireViewById(requireView(), R.id.wind_ratBath);

        //Innicializar el objeto, sino lo considera nulo
        sanroque = new LatLng(36.210135, -5.393580);
        cameraPosition = new CameraPosition.Builder().target(sanroque).zoom(14).build();

        btnAddNewBath.setOnClickListener(v -> {
            FirebaseUser user = fAuth.getCurrentUser();
            if(user != null) { //Esta logueado
                showConfirmationDialog();
            } else {
                showDialog();
            }
        });
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton("Aceptar", (dialog, id) -> {
        }).setTitle("Aviso").setMessage("Debe estar logueado en la aplicación para poder añadir un nuevo baño.");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Configuración del cuadro de diálogo
    private void showConfirmationDialog() {
        YesNoDialogFragment dialogFragment = YesNoDialogFragment.newInstance(
                "Registrar nuevo baño",
                "¿Estás seguro de que quieres agregar un nuevo baño en el lugar indicado? " +
                        "En caso afirmativo, a continuación deberá dejar la primera opinión.", "Aceptar",
                "Cancelar", this, RC_DIALOG_FRAGMENT);
        dialogFragment.show(this.getFragmentManager(), TAG_DIALOG_FRAGMENT);
    }

    //Implementados los métodos de YESNODIALOG
    @Override
    public void onPositiveButtonClick(DialogInterface dialog) {
        getLastBathID();
        Geocoder geocoder = new Geocoder(getContext());
        String direccion = "";
        try {
            direccion = geocoder.getFromLocation(locationClickMap.latitude, locationClickMap.longitude, 1).get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Object> newBath = new HashMap<>();
        newBath.put("id_banio", lastID);
        newBath.put("direccion", direccion);
        newBath.put("latitud", locationClickMap.latitude);
        newBath.put("longitud", locationClickMap.longitude);
        newBath.put("puntuacion", 0);

        database.collection("banios").document(lastID).set(newBath)
                .addOnSuccessListener(aVoid -> Log.d("", "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.d("", "Error writing document", e));

        navigateToOpinion();
    }

    private void navigateToOpinion() {
        Bundle arguments = new Bundle();
        arguments.putString("id_banio", lastID);
        arguments.putBoolean("new_bath", true);
        navController.navigate(R.id.action_mapFragment_to_opinionFragment, arguments);
    }

    private void getLastBathID() {
        database.collection("banios").get().addOnCompleteListener(task -> {
            int last = -1;
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (Integer.parseInt(document.getId()) > last) {
                        last = Integer.parseInt(document.getId());
                    }
                }
                lastID = String.valueOf(last + 1);
            } else {
                Log.d("", "Error getting documents.", task.getException());
            }
        });
    }

    @Override
    public void onNegativeButtonClick(DialogInterface dialog) {
        dialog.dismiss();
        if (marker != null) {
            marker.remove();
        }
        btnAddNewBath.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case RQ_PERMISOS: {
                // Si se cancela la petición, el array de resultado estará vacío.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permiso de localización concedido
                    getCurrentLocation();
                    mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        }
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mGoogleMap.setMyLocationEnabled(true);

            //Obtener la ubicación actual
            LocationManager locationManager = (LocationManager) requireActivity().getSystemService(getContext().LOCATION_SERVICE);
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            };
            Location location;
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 500, locationListener);
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } else {
                locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 1000, 500, locationListener);
                location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }

            if (location != null) {
                LatLng latLngActual = new LatLng(location.getLatitude(), location.getLongitude());
                cameraPosition = new CameraPosition.Builder().target(latLngActual).zoom(16).build();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (MainActivity.isConnected()) {
            setupBaths();
            setupMarkersListener();
        }

        //Para agregar los botones de zoom y ubicación
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

        //Listener en el mapa
        setupListenerMap();
    }

    private void setupListenerMap() {
        //Recoger las coordenadas de donde clicas en el mapa
        mGoogleMap.setOnMapClickListener(latLng -> {
            if (MainActivity.isConnected()) {
                locationClickMap = latLng;
                Geocoder geocoder = new Geocoder(getContext());
                String direccion = "";
                try {
                    direccion = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getAddressLine(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (marker != null) {
                    marker.remove();
                }

                cl_window.setVisibility(View.INVISIBLE);

                if (TextUtils.equals(direccion, "")) {
                    if (marker != null) {
                        marker.remove();
                    }
                    Toast.makeText(requireActivity(), "No se ha recogido una dirección válida, inténtelo de nuevo.", Toast.LENGTH_LONG);
                } else {
                    marker = mGoogleMap.addMarker(new MarkerOptions().position(latLng)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)).title(direccion));
                    cameraPosition = new CameraPosition.Builder().target(latLng).zoom(16).build();
                    mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    marker.showInfoWindow();
                    btnAddNewBath.setVisibility(View.VISIBLE);
                }
            } else {
                navController.navigate(R.id.action_mapFragment_to_noInternetFragment);
            }
        });
    }

    private void setupMarkersListener() {
        mGoogleMap.setOnMarkerClickListener(marker -> {
            if (this.marker != null) {
                this.marker.remove();
            }
            btnAddNewBath.setVisibility(View.INVISIBLE);
            database.collection("banios").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Banio banio = null;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getId().equals(marker.getTag())) {
                            banio = document.toObject(Banio.class);
                            showWindow(banio);
                        }
                    }
                } else {
                    Log.d("", "Error getting documents.", task.getException());
                    Toast.makeText(getContext(), "Ha habido un error al recuperar " +
                            "las localizaiones.", Toast.LENGTH_LONG);
                }
            });
            return false;
        });
    }

    private void showWindow(Banio banio) {
        cl_window.setVisibility(View.VISIBLE);
        lblDirection.setText(banio.getDireccion());
        ratBath.setRating((float) banio.getPuntuacion());

        lblMore.setOnClickListener(v -> {
            Bundle arguments = new Bundle();
            arguments.putString("id_banio", banio.getId_banio());
            arguments.putBoolean("new_bath", true);
            navController.navigate(R.id.action_mapFragment_to_detailFragment, arguments);
        });
    }

    private void setupBaths() {
        database.collection("banios").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                LatLng latLng = null;
                int last = -1;
                int actualID = -1;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    actualID = Integer.parseInt(document.getId());
                    Banio banio = document.toObject(Banio.class);
                    latLng = new LatLng(banio.getLatitud(), banio.getLongitud());
                    mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(banio.getDireccion()))
                            .setTag(banio.getId_banio());
                    if (actualID > last) {
                        last = actualID;
                    }
                    lastID = String.valueOf(last + 1);
                }
                getCurrentLocation();
                mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            } else {
                Log.d("", "Error getting documents.", task.getException());
                Toast.makeText(getContext(), "Ha habido un error al recuperar las localizaiones.", Toast.LENGTH_LONG);
            }
        });
    }
}
