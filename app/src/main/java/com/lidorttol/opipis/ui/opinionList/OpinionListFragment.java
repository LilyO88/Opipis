package com.lidorttol.opipis.ui.opinionList;


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
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.lidorttol.opipis.R;
import com.lidorttol.opipis.data.Banio;
import com.lidorttol.opipis.data.Opinion;
import com.lidorttol.opipis.ui.opinionDetail.OpinionDetailFragment;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class OpinionListFragment extends Fragment {

    private final String ID_BANIO = "id_banio";

    private OpinionListFragmentViewModel viewModelOpinionList;
    private NavController navController;
    private FirebaseFirestore database;

    private String id_banio;
    private Banio banioParam;
    private OpinionListFragmentAdapter listAdapter;
    private RecyclerView rvOpinions;
    private TextView lblDirection;


    public OpinionListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_opinion_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModelOpinionList = ViewModelProviders.of(this).get(OpinionListFragmentViewModel.class);
        navController = NavHostFragment.findNavController(OpinionListFragment.this);


        Objects.requireNonNull(getArguments());
        id_banio = getArguments().getString(ID_BANIO);

        database = FirebaseFirestore.getInstance();

        banioParam = new Banio();
        setupViews();

        if (id_banio != null) {
            recoverBath(id_banio);
            recoverOpinions(id_banio);
        }

        observeListOpinions();
        observeBanio();
    }

    private void setupViews() {
        rvOpinions = ViewCompat.requireViewById(getView(), R.id.list_rvOpinions);
        lblDirection = ViewCompat.requireViewById(getView(), R.id.list_lblDirection);

        setupRecyclerView();
    }

    private void recoverOpinions(String id_banio) {
        database.collection("opiniones").whereEqualTo("id_banio", id_banio).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        viewModelOpinionList.setListOpinionsLiveData(task.getResult().toObjects(Opinion.class));
                    } else {
                        Log.d("", "Error getting documents: ", task.getException());
                    }
                });
    }


    private void observeListOpinions() {
        viewModelOpinionList.getListOpinionsLiveData().observe(getViewLifecycleOwner(), listOpiniones -> {
            listAdapter.submitList(listOpiniones);
        });
    }

    private void setupRecyclerView() {
        listAdapter = new OpinionListFragmentAdapter();

        rvOpinions.setHasFixedSize(true);
        rvOpinions.setLayoutManager(new GridLayoutManager(requireContext(), 1));
        rvOpinions.setNestedScrollingEnabled(false); //!!!!!!!!!!!!!!!!!!!!!!!!!!!
        rvOpinions.setAdapter(listAdapter);
    }


    private void observeBanio() {
        viewModelOpinionList.getBanioParam().observe(getViewLifecycleOwner(), banio -> {
            lblDirection.setText(banio.getDireccion());
        });
    }
    private void recoverBath(String new_bath) {
        database.collection("banios").document(new_bath)
                .get().addOnSuccessListener(documentSnapshot -> {
            banioParam = documentSnapshot.toObject(Banio.class);
            viewModelOpinionList.setBanioParam(documentSnapshot.toObject(Banio.class));
        }).addOnFailureListener(e -> {
        });
    }
}
