package com.lidorttol.opipis.ui.opinionDetail;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.lidorttol.opipis.R;
import com.lidorttol.opipis.data.Banio;
import com.lidorttol.opipis.data.Opinion;
import com.lidorttol.opipis.ui.opinionList.OpinionListFragmentAdapter;

import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class OpinionDetailFragment extends Fragment {


    private final String ID_BANIO = "id_banio";

    private String id_banio;
//    private OpinionListFragmentAdapter listAdapter;
    private NavController navController;


    private Banio banioParam;
    private FirebaseFirestore database;
    private OpinionDetailFragmentViewModel viewModelOpinionDetail;
    private TextView lblTitle;
    private RatingBar ratGlobal;
    private TextView lblNumOpinions;
//    private List<Opinion> listOpinions;
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
    private RecyclerView rvOpinions;
    private TextView txtAddOpinion;
    private ImageView imgAddOpinion;
    private TextView lblLookOpinions;

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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModelOpinionDetail = ViewModelProviders.of(this).get(OpinionDetailFragmentViewModel.class);
        navController = NavHostFragment.findNavController(OpinionDetailFragment.this);


        Objects.requireNonNull(getArguments());
        id_banio = getArguments().getString(ID_BANIO);

        database = FirebaseFirestore.getInstance();

        banioParam = new Banio();
//        listOpinions = new ArrayList<>();
        setupViews();

        if (id_banio != null) {
            recoverBath(id_banio);
            recoverOpinions(id_banio);
        }

        observeBanio();
        observeNumOpinions();
        observeListOpinions();
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
    private void observeListOpinions() {
        viewModelOpinionDetail.getListOpinionsLiveData().observe(getViewLifecycleOwner(), listOpiniones -> {
//            listAdapter.submitList(listOpiniones);
//            listAdapter.notifyDataSetChanged();
            makeAverage(listOpiniones);
        });
    }



    private void setupViews() {
        //Cabecera
        lblTitle = ViewCompat.requireViewById(requireView(), R.id.list_lblDirection);
        ratGlobal = ViewCompat.requireViewById(requireView(), R.id.data_ratOpinion);
        lblNumOpinions = ViewCompat.requireViewById(requireView(), R.id.data_lblNumOpinion);
        //General
        ratCleaning = ViewCompat.requireViewById(requireView(), R.id.data_ratCleaning);
        ratSize = ViewCompat.requireViewById(requireView(), R.id.data_ratSize);
        yesLatch = ViewCompat.requireViewById(requireView(), R.id.data_lblSiLatch);
        noLatch = ViewCompat.requireViewById(requireView(), R.id.data_lblNoLatch);
        yesPaper = ViewCompat.requireViewById(requireView(), R.id.data_lblSiPaper);
        noPaper = ViewCompat.requireViewById(requireView(), R.id.data_lblNoPaper);
        yesDisabled = ViewCompat.requireViewById(requireView(), R.id.data_lblSiDisabled);
        noDisabled = ViewCompat.requireViewById(requireView(), R.id.data_lblNoDisabled);
        yesUnisex = ViewCompat.requireViewById(requireView(), R.id.data_lblSiUnisex);
        noUnisex = ViewCompat.requireViewById(requireView(), R.id.data_lblNoUnisex);
//        rvOpinions = ViewCompat.requireViewById(requireView(), R.id.list_rvOpinions);

        lblLookOpinions = ViewCompat.requireViewById(getView(), R.id.op_goOpinions);

        txtAddOpinion = ViewCompat.requireViewById(requireView(), R.id.add_lblAddOpinion);
        imgAddOpinion = ViewCompat.requireViewById(requireView(), R.id.add_imgAddOpinion);

        setupListeners();
//        setupRecyclerView();
    }

    private void setupListeners() {
        txtAddOpinion.setOnClickListener(v -> {
            Bundle arguments = new Bundle();
            arguments.putString("id_banio", id_banio);
            arguments.putBoolean("new_bath", false);
            navController.navigate(R.id.action_detailFragment_to_opinionFragment, arguments);
        });

        lblLookOpinions .setOnClickListener(v -> {
            Bundle arguments = new Bundle();
            arguments.putString("id_banio", id_banio);
            navController.navigate(R.id.action_detailFragment_to_opinionListFragment, arguments);
        });
    }

   /* private void setupRecyclerView() {
        listAdapter = new OpinionListFragmentAdapter();

        rvOpinions.setHasFixedSize(true);
        rvOpinions.setLayoutManager(new GridLayoutManager(requireContext(), 1));
        rvOpinions.setNestedScrollingEnabled(false); //!!!!!!!!!!!!!!!!!!!!!!!!!!!
        rvOpinions.setAdapter(listAdapter);
    }*/

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
            viewModelOpinionDetail.setBanioParam(documentSnapshot.toObject(Banio.class));
            viewModelOpinionDetail.setGlobalParam(documentSnapshot.toObject(Banio.class).getPuntuacion());
        }).addOnFailureListener(e -> {
        });
    }

}
