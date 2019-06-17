package com.lidorttol.opipis.ui.opinion;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lidorttol.opipis.R;
import com.lidorttol.opipis.data.Banio;


/**
 * A simple {@link Fragment} subclass.
 */
public class OpinionFragment extends Fragment {

    private String new_bath;
    private Button btnCancel;
    private Button btnSave;
    private FirebaseFirestore database;
    private boolean btnSaveClicked;
    private TextView title;
    private Banio banio;
    private NavController navController;
    private OpinionFragmentViewModel viewModelOpinion;


    public OpinionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            new_bath = getArguments().getString("new_bath");
            database = FirebaseFirestore.getInstance();
            banio = new Banio();

            if (new_bath != null) {
                database.collection("banios").document(new_bath)
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        banio = documentSnapshot.toObject(Banio.class);
                        viewModelOpinion.setBanioParam(banio);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_opinion, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModelOpinion = ViewModelProviders.of(this).get(OpinionFragmentViewModel.class);
        observeBanio();
        setupViews();
    }

    private void observeBanio() {
        viewModelOpinion.getBanioParam().observe(getViewLifecycleOwner(), banio -> {
            title.setText(banio.getDireccion());
        });
    }

    private void setupViews() {
        title = ViewCompat.requireViewById(getView(), R.id.no_lblDirection);
        btnCancel = ViewCompat.requireViewById(getView(), R.id.no_btnCancel);
        btnSave = ViewCompat.requireViewById(getView(), R.id.no_btnSave);
        navController = NavHostFragment.findNavController(OpinionFragment.this);

        btnSaveClicked = false;

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSaveClicked = true;
                navController.popBackStack();
            }
        });

        btnCancel.setOnClickListener(v -> {
            deleteBath();
            navController.popBackStack();

        });
    }

    private void deleteBath() {
        if (new_bath != null) {
            database.collection("banios").document(new_bath)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("", "DocumentSnapshot successfully deleted!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("", "Error deleting document", e);
                        }
                    });

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (!btnSaveClicked) {
            deleteBath();
        }
    }
}
