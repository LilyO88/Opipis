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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lidorttol.opipis.R;
import com.lidorttol.opipis.base.YesNoDialogFragment;
import com.lidorttol.opipis.data.Banio;
import com.lidorttol.opipis.ui.main.MainActivityViewModel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MapFragment extends Fragment implements OnMapReadyCallback, YesNoDialogFragment.Listener{

    private static final int RQ_PERMISOS = 1;
    private static final String[] PERMISOS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final String TAG_DIALOG_FRAGMENT = "TAG_DIALOG_FRAGMENT";
    private static final int RC_DIALOG_FRAGMENT = 1;

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private FirebaseFirestore database;
    private NavController navController;
    private MainActivityViewModel viewModelMainActivity;
    private CameraPosition cameraPosition;
    private boolean connected;
    private Button btnAddNewBath;
    private TextView map_lblDireccion;
    private Marker marker;
    private TextView lblMore;
    private TextView lblDirection;
    private ConstraintLayout cl_window;
    private RatingBar ratBath;
    private LatLng sanroque;
    private LatLng locationClickMap;
    private String lastID;

    //    View rootView;

    public MapFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        viewModelMainActivity =   ViewModelProviders.of(requireActivity()).get(MainActivityViewModel.class);
        setupViews();

        try {
            isConnected();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //INTERNET
        if (mMapView != null) {
            mMapView.onCreate(savedInstanceState);
            mMapView.onResume(); // Necesario para obtener el mapa para mostrar inmediatamente

            mMapView.getMapAsync(this);
        }

        //Solicitud de permiso de localización
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            MapFragment.this.requestPermissions(PERMISOS, RQ_PERMISOS);
        }
    }


    private void isConnected() throws InterruptedException {
        viewModelMainActivity.getConnected().observe(this, connected -> {
            if (!connected) {
                // No hay conexión a Internet en este momento
                navController.navigate(R.id.action_mapFragment_to_noInternetFragment);
            }
        });

    }

    private void setupViews() {
        mMapView = ViewCompat.requireViewById(requireView(), R.id.map);
        database = FirebaseFirestore.getInstance();
        btnAddNewBath = ViewCompat.requireViewById(getView(), R.id.map_btnAddBath);
        map_lblDireccion = ViewCompat.requireViewById(getView(), R.id.map_lblDireccion);
        lblMore = ViewCompat.requireViewById(requireView(), R.id.wind_lblMore);
        lblDirection = ViewCompat.requireViewById(requireView(), R.id.wind_lblDirection);
        cl_window = ViewCompat.requireViewById(requireView(), R.id.cl_window_map);
        ratBath = ViewCompat.requireViewById(requireView(), R.id.wind_ratBath);

        navController = NavHostFragment.findNavController(MapFragment.this);

        //Innicializar el objeto, sino lo considera nulo
        sanroque = new LatLng(36.210135, -5.393580);
        cameraPosition = new CameraPosition.Builder().target(sanroque).zoom(14).build();

        try {
            viewModelMainActivity.getConnected().observe(getViewLifecycleOwner(), connectedO -> {
                connected = connectedO;
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        btnAddNewBath.setOnClickListener(v -> {
            showConfirmationDialog();
        });
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
        database.collection("banios").get().addOnCompleteListener(task -> {
            int last = -1;
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if(Integer.parseInt(document.getId()) > last) {
                        last = Integer.parseInt(document.getId());
                    }
                }
                lastID = String.valueOf(last + 1);
            } else {
                Log.d("", "Error getting documents.", task.getException());
            }
        });
        Geocoder geocoder = new Geocoder(getContext());
        String direccion = "";
        try {
            direccion = geocoder.getFromLocation(locationClickMap.latitude, locationClickMap.longitude, 1).get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Banio newBath = new Banio(lastID, direccion, locationClickMap.latitude, locationClickMap.longitude, 0);
        Map<String, Object> newBath = new HashMap<>();
        newBath.put("id_banio", lastID);
        newBath.put("direccion", direccion);
        newBath.put("latitud", locationClickMap.latitude);
        newBath.put("longitud", locationClickMap.longitude);
        newBath.put("puntuacion", 0);
//        database.collection("banios").document(lastID).set(newBath);
        database.collection("banios").document(lastID).set(newBath).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("", "DocumentSnapshot successfully written!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("", "Error writing document", e);
            }
        });


        Bundle arguments = new Bundle();
        arguments.putString("new_bath", lastID);
        navController.navigate(R.id.action_mapFragment_to_opinionFragment, arguments);
    }

    @Override
    public void onNegativeButtonClick(DialogInterface dialog) {
        dialog.dismiss();
        if(marker != null) {
            marker.remove();
        }
        btnAddNewBath.setVisibility(View.INVISIBLE);
        map_lblDireccion.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
            mGoogleMap.setMyLocationEnabled(true); //NO FUNCIONA!!!!

            //Obtener la ubicación actual
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(getContext().LOCATION_SERVICE);
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 500, locationListener);
//            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); Ha dejado de funcionar del 15 noche al 16 de junio mediodía
            Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
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
        //Comprueba si hay conexión !!!!!!!!!!
        if (connected) {
            recoverBaths();
            setupMarkersListener();
            //readDatabase();
        } else {
            //Mostrar diálogo de reintentar conexión !!!!!!!!!!!!!!!!!!!!!!!!

        }

        //Para agregar los botones de zoom y ubicación
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
//        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true); NO FUNCIONA

        //Listener en el mapa
        setupListenerMap();
    }

    private void setupListenerMap() {
        //Recoger las coordenadas de donde clicas en el mapa
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
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

                marker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)).title(direccion));
                marker.showInfoWindow();
                btnAddNewBath.setVisibility(View.VISIBLE);
                map_lblDireccion.setText(direccion);
                map_lblDireccion.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupMarkersListener() {
        mGoogleMap.setOnMarkerClickListener(marker -> {
            marker.getTag();
            if (this.marker != null) {
                this.marker.remove();
            }
            map_lblDireccion.setVisibility(View.INVISIBLE);
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
                    Toast.makeText(getContext(), "Ha habido un error al recuperar las localizaiones.", Toast.LENGTH_LONG);
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
            navController.navigate(R.id.action_mapFragment_to_detailFragment, arguments); //CAMBIAR EL DESTINO Y AÑADIR EL PARÁMETRO DE ID DEL BAÑO A MOSTRAR
        });
    }

    private void recoverBaths() {
        database.collection("banios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    LatLng latLng = null;
                    int last = -1;
                    int actualID = -1;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        actualID = Integer.parseInt(document.getId());
                        Banio banio = document.toObject(Banio.class);
                        latLng = new LatLng(banio.getLatitud(), banio.getLongitud());
                        mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(banio.getDireccion())).setTag(banio.getId_banio());
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
            }
        });
    }

/*    private void setupMap() {
        database.collection("banios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    LatLng latLng = null;

                    for (QueryDocumentSnapshot document: task.getResult()) {
                        //Log.d("", document.getId() + " => " + document.getData());
                        Banio banio = document.toObject(Banio.class);
                        latLng = new LatLng(banio.getLatitud(), banio.getLongitud());
                        mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(banio.getDireccion())).setTag(banio.getId_banio());
                    }
                    if(permissionLocation == PackageManager.PERMISSION_GRANTED) {
                        getCurrentLocation();
                    } else {
                        //Para hacer zoom automáticamente a la ubicación del último marcador.
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15).build();
                        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                } else {
                    Log.d("", "Error getting documents.", task.getException());
                    Toast.makeText(getContext(), "Ha habido un error al recuperar las localizaiones.", Toast.LENGTH_LONG);
                }
            }
        });
    }*/

/*
    private void setupMarkers(List<Banio> banios) {
        LatLng latLng = null;

        for (Banio banio: banios) {
            //Log.d("", document.getId() + " => " + document.getData());
            latLng = new LatLng(banio.getLatitud(), banio.getLongitud());
            mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(banio.getDireccion())).setTag(banio.getId_banio());
        }
        //Para hacer zoom automáticamente a la ubicación del marcador.
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
*/



/*    private boolean isConnected() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // Si hay conexión a Internet en este momento
            connected = true;
        }
        return connected;
    }*/

    /*private void readDatabase() {
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
                        *//*marker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(banio.getDireccion()));
                        markers.put(banio.getId_banio(), marker);*//*
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
*/


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

        TextView mkl = ViewCompat.requireViewById(requireView(), R.id.lblDirection);
        ConstraintLayout cl = ViewCompat.requireViewById(requireView(), R.id.cl_window_map);

        cl.setVisibility(View.VISIBLE);
        mkl.setText(cadena);

        RatingBar ratBath = ViewCompat.requireViewById(requireView(), R.id.cal_bath);
        ratBath.setRating(3.5f);*/
}