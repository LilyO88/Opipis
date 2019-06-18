package com.lidorttol.opipis.ui.opinionList;


import android.graphics.Path;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lidorttol.opipis.R;
import com.lidorttol.opipis.data.Banio;
import com.lidorttol.opipis.data.Opinion;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class OpinionDetailFragment extends Fragment {


    private final String ID_BANIO = "id_banio";
    private final String NUMOPINIONS = "num_opinions";

    private String id_banio;

    private Banio banioParam;
    private FirebaseFirestore database;
    private OpinionDetailFragmentViewModel viewModelOpinionDetail;
    private TextView lblTitle;
    private RatingBar ratGlobal;
    private TextView lblNumOpinions;
    private List<Opinion> listOpinions;
    private RatingBar ratCleaning;
    private RatingBar ratSize;
    private TextView yesLatch;
    private TextView noLatch;
    private TextView yesPaper;
    private TextView noPaper;
    private TextView yesDisabled;
    private TextView noDisabled;
    private TextView yesUnisex;
    private TextView noUnisex;

    public OpinionDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_opinion_detail, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Objects.requireNonNull(getArguments());
        id_banio = getArguments().getString(ID_BANIO);

        database = FirebaseFirestore.getInstance();

        banioParam = new Banio();
        listOpinions = new ArrayList<>();

        if (id_banio != null) {
            recoverBath(id_banio);
            recoverOpinions(id_banio);
        }
    }

    private void recoverOpinions(String id_banio) {
        database.collection("opiniones").whereEqualTo("id_banio", id_banio).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        viewModelOpinionDetail.setNumOpinionsParam(task.getResult().size());
                        viewModelOpinionDetail.setListOpinionsLiveData(task.getResult().toObjects(Opinion.class));
                    } else {
                        Log.d("", "Error getting documents: ", task.getException());
                    }
                });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModelOpinionDetail = ViewModelProviders.of(this).get(OpinionDetailFragmentViewModel.class);
        observeBanio();
        observeNumOpinions();
        observeListOpinions();
        setupViews();
    }

    private void observeListOpinions() {
        viewModelOpinionDetail.getListOpinionsLiveData().observe(getViewLifecycleOwner(), listOpiniones -> {
            listOpinions = listOpiniones;
            makeAverage(listOpiniones);
        });
    }

    private void makeAverage(List<Opinion> listOpiniones) {
        averageCleaning(listOpiniones);
        averageSize(listOpiniones);
        averageLatch(listOpiniones);
        averageDisabled(listOpiniones);
        averagePaper(listOpiniones);
        averageUnisex(listOpiniones);
    }

    private void averageUnisex(List<Opinion> listOpiniones) {
        int yes = 0;
        int no = 0;
        for (Opinion opinion: listOpiniones) {
            if(opinion.isUnisex()) {
                yes++;
            } else {
                no++;
            }
        }
        yesUnisex.setText(yes + " Sí");
        noUnisex.setText(no + " No");
    }

    private void averagePaper(List<Opinion> listOpiniones) {
        int yes = 0;
        int no = 0;
        for (Opinion opinion: listOpiniones) {
            if(opinion.isPapel()) {
                yes++;
            } else {
                no++;
            }
        }
        yesPaper.setText(yes + " Sí");
        noPaper.setText(no + " No");
    }

    private void averageDisabled(List<Opinion> listOpiniones) {
        int yes = 0;
        int no = 0;
        for (Opinion opinion: listOpiniones) {
            if(opinion.isMinusvalido()) {
                yes++;
            } else {
                no++;
            }
        }
        yesDisabled.setText(yes + " Sí");
        noDisabled.setText(no + " No");
    }

    private void averageLatch(List<Opinion> listOpiniones) {
        int yes = 0;
        int no = 0;
        for (Opinion opinion: listOpiniones) {
            if(opinion.isPestillo()) {
                yes++;
            } else {
                no++;
            }
        }
        yesLatch.setText(yes + " Sí");
        noLatch.setText(no + " No");
    }

    private void averageSize(List<Opinion> listOpiniones) {
        double total = 0;
        double size = 0;
        for (Opinion opinion: listOpiniones) {
            size += opinion.getTamanio();
        }
        total = size / listOpiniones.size();
        ratSize.setRating((float) total);
    }


    private void averageCleaning(List<Opinion> listOpiniones) {
        double total = 0;
        double cleaning = 0;
        for (Opinion opinion: listOpiniones) {
            cleaning += opinion.getLimpieza();
        }
        total = cleaning / listOpiniones.size();
        ratCleaning.setRating((float) total);
    }

    private void observeNumOpinions() {
        viewModelOpinionDetail.getNumOpinionsParam().observe(getViewLifecycleOwner(), numOpinions -> {
            if (numOpinions == 1) {
                lblNumOpinions.setText(numOpinions + " opinión");
            } else {
                lblNumOpinions.setText(numOpinions + " opiniones");
            }
        });
    }

    private void setupViews() {
        //Cabecera
        lblTitle = ViewCompat.requireViewById(getView(), R.id.data_lblDirection);
        ratGlobal = ViewCompat.requireViewById(getView(), R.id.data_ratOpinion);
        lblNumOpinions = ViewCompat.requireViewById(getView(), R.id.data_lblNumOpinion);
        //General
        ratCleaning = ViewCompat.requireViewById(getView(), R.id.data_ratCleaning);
        ratSize = ViewCompat.requireViewById(getView(), R.id.data_ratSize);
        yesLatch = ViewCompat.requireViewById(getView(), R.id.data_lblSiLatch);
        noLatch = ViewCompat.requireViewById(getView(), R.id.data_lblNoLatch);
        yesPaper = ViewCompat.requireViewById(getView(), R.id.data_lblSiPaper);
        noPaper = ViewCompat.requireViewById(getView(), R.id.data_lblNoPaper);
        yesDisabled = ViewCompat.requireViewById(getView(), R.id.data_lblSiDisabled);
        noDisabled = ViewCompat.requireViewById(getView(), R.id.data_lblNoDisabled);
        yesUnisex = ViewCompat.requireViewById(getView(), R.id.data_lblSiUnisex);
        noUnisex = ViewCompat.requireViewById(getView(), R.id.data_lblNoUnisex);

    }


    private void observeBanio() {
        viewModelOpinionDetail.getBanioParam().observe(getViewLifecycleOwner(), banio -> {
            lblTitle.setText(banio.getDireccion());
            ratGlobal.setRating((float) banio.getPuntuacion());
        });
    }

    private void recoverBath(String new_bath) {
        database.collection("banios").document(new_bath)
                .get().addOnSuccessListener(documentSnapshot -> {
            banioParam = documentSnapshot.toObject(Banio.class);
            viewModelOpinionDetail.setBanioParam(banioParam);
            viewModelOpinionDetail.setGlobalParam(banioParam.getPuntuacion());
        }).addOnFailureListener(e -> {
        });
    }
    
}
