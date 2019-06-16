package com.lidorttol.opipis.ui;


import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

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
                        title.setText(banio.getDireccion());
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

        setupViews();
    }

    private void setupViews() {
        title = ViewCompat.requireViewById(getView(), R.id.no_lblDirection);
        btnCancel = ViewCompat.requireViewById(getView(), R.id.no_btnCancel);
        btnSave = ViewCompat.requireViewById(getView(), R.id.no_btnSave);
        btnSaveClicked = false;

//            Banio banio = database.collection("banios").document(new_bath).get().getResult().toObject(Banio.class);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSaveClicked = true;
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        btnCancel.setOnClickListener(v -> {
            deleteBath();
            getActivity().getSupportFragmentManager().popBackStack();
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
