package com.lidorttol.opipis.ui;

import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lidorttol.opipis.R;

import java.io.IOException;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    MapView mMapView;
    private GoogleMap mGoogleMap;
    View rootView;

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

        mMapView = ViewCompat.requireViewById(requireView(), R.id.map);
//        mMapView = rootView.findViewById(R.id.map);
        if(mMapView != null) {
            mMapView.onCreate(savedInstanceState);
            mMapView.onResume(); // Necesario para obtener el mapa para mostrar inmediatamente

            mMapView.getMapAsync(this);
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

//        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //  Para soltar un marcador en un punto del mapa
        LatLng latLng = new LatLng(36.679582, -5.444791);
        LatLng latLng2 = new LatLng(36.6, -5.4);
        mGoogleMap.addMarker(new MarkerOptions().position(latLng).title("Ubrique").snippet("Ubicación de Ubrique"));
        mGoogleMap.addMarker(new MarkerOptions().position(latLng2).title("Otro sitio").snippet("Ubicación inventada"));


        //Para sacar la dirección
        Geocoder geocoder = new Geocoder(getContext());
        String cadena = "";
        try {
            cadena = geocoder.getFromLocation(latLng.latitude, latLng.longitude,1).get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        TextView mkl = ViewCompat.requireViewById(requireView(), R.id.txtDirection);
        ConstraintLayout cl = ViewCompat.requireViewById(requireView(), R.id.cl_window_map);

        cl.setVisibility(View.VISIBLE);
        mkl.setText(cadena);

        RatingBar rat = ViewCompat.requireViewById(requireView(), R.id.cal_bath);
        rat.setRating(3.5f);

        //Para hacer zoom automáticamente a la ubicación del marcador.
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(12).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


        


        //Recoger las coordenadas de donde clicas en el mapa
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }
        });

        //Para agregar los botones de zoom
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
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
}
