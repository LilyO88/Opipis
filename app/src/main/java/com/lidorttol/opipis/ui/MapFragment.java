package com.lidorttol.opipis.ui;

import android.location.Geocoder;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lidorttol.opipis.R;
import com.lidorttol.opipis.data.Banio;

import java.io.IOException;
import java.util.HashMap;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    MapView mMapView;
    private GoogleMap mGoogleMap;
    FirebaseFirestore database;
    NavController navController;
//    DatabaseReference database;
//    View rootView;

    public MapFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
//        rootView = inflater.inflate(R.layout.fragment_map, container, false);
//        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();

        //        mMapView = rootView.findViewById(R.id.map);
        if(mMapView != null) {
            mMapView.onCreate(savedInstanceState);
            mMapView.onResume(); // Necesario para obtener el mapa para mostrar inmediatamente

            mMapView.getMapAsync(this);
        }
    }

    private void setupViews() {
        mMapView = ViewCompat.requireViewById(requireView(), R.id.map);
        database = FirebaseFirestore.getInstance();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

//        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.getTag();
                database.collection("banios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Banio banio = null;

                            for (QueryDocumentSnapshot document: task.getResult()) {
                                //Log.d("", document.getId() + " => " + document.getData());
                                if(document.getId().equals(marker.getTag())) {
                                    banio = document.toObject(Banio.class);
                                    showWindow(banio);
                                }
                            }
                        } else {
                            Log.w("", "Error getting documents.", task.getException());
                            Toast.makeText(getContext(), "Ha habido un error al recuperar las localizaiones.", Toast.LENGTH_LONG);
                        }
                    }
                });
                return false;
            }
        });

        readDatabase();


        //Para agregar los botones de zoom
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);

        //Recoger las coordenadas de donde clicas en el mapa
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }
        });
    }



    private void showWindow(Banio banio) {
        TextView txtVerMas = ViewCompat.requireViewById(requireView(), R.id.txtVerMas);
        TextView txtDirection = ViewCompat.requireViewById(requireView(), R.id.txtDirection);
        ConstraintLayout cl_window = ViewCompat.requireViewById(requireView(), R.id.cl_window_map);
        RatingBar rat = ViewCompat.requireViewById(requireView(), R.id.cal_bath);

        cl_window.setVisibility(View.VISIBLE);
        txtDirection.setText(banio.getDireccion());
        rat.setRating((float)banio.getPuntuacion());

        txtVerMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController = NavHostFragment.findNavController(MapFragment.this);
                navController.navigate(R.id.action_mapFragment_to_detailFragment); //CAMBIAR EL DESTINO Y AÑADIR EL PARÁMETRO DE ID DEL BAÑO A MOSTRAR
                Toast.makeText(getContext(), "Hay internet.", Toast.LENGTH_LONG);
            }
        });
    }


    private void readDatabase() {
        database.collection("banios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    LatLng latLng = null;
//                    HashMap markers = new HashMap();
//                    Marker marker;
                    for (QueryDocumentSnapshot document: task.getResult()) {
                        //Log.d("", document.getId() + " => " + document.getData());

                        Banio banio = document.toObject(Banio.class);
                        latLng = new LatLng(banio.getLatitud(), banio.getLongitud());
                        mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(banio.getDireccion())).setTag(banio.getId_banio());
                        /*marker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(banio.getDireccion()));
                        markers.put(banio.getId_banio(), marker);*/
                    }
                    //Para hacer zoom automáticamente a la ubicación del marcador.
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15).build();
                    mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                } else {
                    Log.w("", "Error getting documents.", task.getException());
                    Toast.makeText(getContext(), "Ha habido un error al recuperar las localizaiones.", Toast.LENGTH_LONG);
                }
            }
        });

    }



/*   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       return inflater.inflate(R.layout.fragment_map, container, false);
    }

*//*    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        getMapAsync(this);
        return rootView;
    }*//*

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if(getActivity()!=null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap map) {

        UiSettings settings = mMap.getUiSettings();
//        SearchView searchView = ViewCompat.requireViewById(getView(), R.id.searchView);
        // Posicionar el mapa en una localización y con un nivel de zoom
        LatLng latLng = new LatLng(36.679582, -5.444791);
        // Un zoom mayor que 13 hace que el emulador falle, pero un valor deseado para
        // callejero es 17 aprox.
        float zoom = 13;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        // Colocar un marcador en la misma posición
        mMap.addMarker(new MarkerOptions().position(latLng));
        // Más opciones para el marcador en:
        // https://developers.google.com/maps/documentation/android/marker
        settings.setZoomControlsEnabled(true);
//        searchView.setIconifiedByDefault(false);
        // Otras configuraciones pueden realizarse a través de UiSettings
        // UiSettings settings = getMap().getUiSettings();
    }*/


    /*
    *
    *  Geocoder geocoder = new Geocoder(getContext());
        String cadena = "";
        try {
            cadena = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        TextView mkl = ViewCompat.requireViewById(requireView(), R.id.txtDirection);
        ConstraintLayout cl = ViewCompat.requireViewById(requireView(), R.id.cl_window_map);

        cl.setVisibility(View.VISIBLE);
        mkl.setText(cadena);

        RatingBar rat = ViewCompat.requireViewById(requireView(), R.id.cal_bath);
        rat.setRating(3.5f);*/
}
