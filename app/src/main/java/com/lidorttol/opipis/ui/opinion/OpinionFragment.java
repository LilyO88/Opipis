package com.lidorttol.opipis.ui.opinion;

import android.app.DatePickerDialog;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lidorttol.opipis.R;
import com.lidorttol.opipis.data.Banio;
import com.lidorttol.opipis.data.Opinion;
import com.lidorttol.opipis.utils.KeyboardUtils;
import com.lidorttol.opipis.utils.ValidationUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class OpinionFragment extends Fragment {

    private TextView title;
    private TextView lblCleaning;
    private RatingBar ratCleaning;
    private TextView lblSize;
    private RatingBar ratSize;
    private TextView lblLatch;
    private RadioButton yesLatch;
    private RadioButton noLatch;
    private TextView lblPaper;
    private RadioButton yesPaper;
    private RadioButton noPaper;
    private TextView lblDisabled;
    private RadioButton yesDisabled;
    private RadioButton noDisabled;
    private TextView lblUnisex;
    private RadioButton yesUnisex;
    private RadioButton noUnisex;
    private TextView lblDate;
    private EditText txtDate;
    private TextView lblComment;
    private EditText txtComment;
    private Button btnCancel;
    private Button btnSave;

    private FirebaseFirestore database;
    private NavController navController;
    private OpinionFragmentViewModel viewModelOpinion;

    private boolean btnSaveClicked;
    private String new_bath;
    private Banio banio;
    private ConstraintLayout cl_opinion;
    private String lastOpinionID;
    private Map<String, Object> new_opinion;
    private List<Opinion> listOpinions;


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
                recoverBath(new_bath);
            }
            getLastOpinionID();
        }
    }

    private void recoverBath(String new_bath) {
        database.collection("banios").document(new_bath)
                .get().addOnSuccessListener(documentSnapshot -> {
                    banio = documentSnapshot.toObject(Banio.class);
                    viewModelOpinion.setBanioParam(banio);
                }).addOnFailureListener(e -> {});
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
        observeLastOpinionID();
        setupViews();
        validateFields();
    }

    private void observeLastOpinionID() {
        viewModelOpinion.getLastOpinionIdLD().observe(getViewLifecycleOwner(), lastIdOpinion -> {
            lastOpinionID = lastIdOpinion;
        });
    }

    private void observeBanio() {
        viewModelOpinion.getBanioParam().observe(getViewLifecycleOwner(), banio -> {
            title.setText(banio.getDireccion());
        });
    }

    private void setupViews() {
        title = ViewCompat.requireViewById(getView(), R.id.no_lblDirection);
        lblCleaning = ViewCompat.requireViewById(getView(), R.id.no_lblCleaning);
        ratCleaning = ViewCompat.requireViewById(getView(), R.id.no_ratCleaning);
        lblSize = ViewCompat.requireViewById(getView(), R.id.no_lblSize);
        ratSize = ViewCompat.requireViewById(getView(), R.id.no_ratSize);
        lblLatch = ViewCompat.requireViewById(getView(), R.id.no_lblLatch);
        yesLatch = ViewCompat.requireViewById(getView(), R.id.no_rbSi_latch);
        noLatch = ViewCompat.requireViewById(getView(), R.id.no_rbNo_latch);
        lblPaper = ViewCompat.requireViewById(getView(), R.id.no_lblPaper);
        yesPaper = ViewCompat.requireViewById(getView(), R.id.no_rbSi_paper);
        noPaper = ViewCompat.requireViewById(getView(), R.id.no_rbNo_paper);
        lblDisabled = ViewCompat.requireViewById(getView(), R.id.no_lblDisabled);
        yesDisabled = ViewCompat.requireViewById(getView(), R.id.no_rbSi_disabled);
        noDisabled = ViewCompat.requireViewById(getView(), R.id.no_rbNo_disabled);
        lblUnisex = ViewCompat.requireViewById(getView(), R.id.no_lblUnisex);
        yesUnisex = ViewCompat.requireViewById(getView(), R.id.no_rbSi_unisex);
        noUnisex = ViewCompat.requireViewById(getView(), R.id.no_rbNo_unisex);
        lblDate = ViewCompat.requireViewById(getView(), R.id.no_lblDate);
        txtDate = ViewCompat.requireViewById(getView(), R.id.no_txtDate);
        lblComment = ViewCompat.requireViewById(getView(), R.id.no_lblComment);
        txtComment = ViewCompat.requireViewById(getView(), R.id.no_txtComment);
        btnCancel = ViewCompat.requireViewById(getView(), R.id.no_btnCancel);
        btnSave = ViewCompat.requireViewById(getView(), R.id.no_btnSave);

        cl_opinion = ViewCompat.requireViewById(getView(), R.id.cl_opinion);

        navController = NavHostFragment.findNavController(OpinionFragment.this);
        btnSaveClicked = false;

        //Teclado acción
        txtComment.setOnEditorActionListener((v, actionId, event) -> {
            btnSaveClicked = true;
            KeyboardUtils.hideSoftKeyboard(requireActivity());
            save();
            return false;
        });

        setListeners();
        setFocusListeners();
        setRadiobuttonsRestrictions();

        GestorTextWatcher gestorTextWatcher = new GestorTextWatcher();

        txtComment.addTextChangedListener(gestorTextWatcher);
    }

    private void setRadiobuttonsRestrictions() {
        //Latch
        setRadioButtonsListeners(yesLatch, noLatch);
        //Paper
        setRadioButtonsListeners(yesPaper, noPaper);
        //Disabled
        setRadioButtonsListeners(yesDisabled, noDisabled);
        //Unisex
        setRadioButtonsListeners(yesUnisex, noUnisex);
    }

    private void setRadioButtonsListeners(RadioButton yes, RadioButton no) {
        yes.setOnClickListener(v -> {
            if (yes.isChecked()) {
                no.setChecked(false);
            }
        });
        no.setOnClickListener(v -> {
            if (no.isChecked()) {
                yes.setChecked(false);
            }
        });
    }

    private void setListeners() {
        //Botón SAVE
        btnSave.setOnClickListener(v -> {
            btnSaveClicked = true;
            save();
        });
        //Botón CANCEL
        btnCancel.setOnClickListener(v -> {
            deleteBath();
            navController.popBackStack();

        });
        //EditText de DATE
        txtDate.setOnClickListener(v -> showDateDialogPicker(txtDate));
    }

    private void save() {
        if (!validateAll()) {
            Snackbar.make(cl_opinion, "Revise los campos erróneos", Snackbar.LENGTH_LONG).show();
        } else {
            //Guardar la opinión
            saveOpinion();
            navController.popBackStack();
        }
        KeyboardUtils.hideSoftKeyboard(requireActivity());
    }

    private void saveOpinion() {
        getLastOpinionID();
        buildNewOpinion();
        //        database.collection("banios").document(lastID).set(newBath);
        database.collection("opiniones").document(lastOpinionID).set(new_opinion)
                .addOnSuccessListener(aVoid -> Log.d("", "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.d("", "Error writing document", e));
    }

    private void buildNewOpinion() {
        new_opinion = new HashMap<>();
        new_opinion.put("id_banio", new_bath);
        new_opinion.put("id_opinion", lastOpinionID);
        //Falta iniciar sesión y recuperar usuario
        new_opinion.put("usuario", "QKwTmxCMGLfb6n43sCf9SIxkh052");
        new_opinion.put("comentario", txtComment.getText().toString());
        new_opinion.put("limpieza", ratCleaning.getRating());
        new_opinion.put("tamanio", ratSize.getRating());
        new_opinion.put("pestillo", defineYesNo(yesLatch, noLatch));
        new_opinion.put("papel", defineYesNo(yesPaper, noPaper));
        new_opinion.put("minusvalido", defineYesNo(yesDisabled, noDisabled));
        new_opinion.put("unisex", defineYesNo(yesUnisex, noUnisex));
        new_opinion.put("global", setGlobal());
        new_opinion.put("fecha", getDate(txtDate));
    }

    private Date getDate(EditText txtDate) {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = formatter.parse(txtDate.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    private double setGlobal() {
        return ((((ratCleaning.getRating()*0.4) + (ratSize.getRating()*0.4) + yesNoValue(yesLatch, noLatch)
                + yesNoValue(yesLatch, noLatch) + yesNoValue(yesPaper, noPaper) + yesNoValue(yesDisabled, noDisabled)
                + yesNoValue(noUnisex, yesUnisex)) * 5) / 8);
    }

    private double yesNoValue(RadioButton yes, RadioButton no) {
        double result = 0;
        if (yes.isChecked()) {
            result = 1;
        } else if (no.isChecked()) {
            result = 0;
        }
        return result;
    }

    private boolean defineYesNo(RadioButton yes, RadioButton no) {
        boolean result = false;
        if (yes.isChecked()) {
            result = true;
        } else if (no.isChecked()) {
            result = false;
        }
        return result;
    }

    private void getLastOpinionID() {
        database.collection("opiniones").get().addOnCompleteListener(task -> {
            int last = -1;
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (Integer.parseInt(document.getId()) > last) {
                        last = Integer.parseInt(document.getId());
                    }
                }
//                lastOpinionID = String.valueOf(last + 1);
                    viewModelOpinion.setLastOpinionIdLD(String.valueOf(last + 1));
            } else {
                Log.d("", "Error getting documents.", task.getException());
            }
        });
    }

    private void validateFields() {
        lblCleaning.setEnabled(viewModelOpinion.isStateCleaning());
        lblSize.setEnabled(viewModelOpinion.isStateSize());
        lblLatch.setEnabled(viewModelOpinion.isStateLatch());
        lblPaper.setEnabled(viewModelOpinion.isStatePaper());
        lblDisabled.setEnabled(viewModelOpinion.isStateDisabled());
        lblUnisex.setEnabled(viewModelOpinion.isStateUnisex());
        lblDate.setEnabled(viewModelOpinion.isStateDate());
        lblComment.setEnabled(viewModelOpinion.isStateComment());
    }

    private class GestorTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkCurrentView();
        }

        @Override
        public void afterTextChanged(Editable s) {
            checkCurrentView();
        }
    }

    private void checkRating(TextView textView, RatingBar ratingBar) {
        enabledDisabledField(textView, ValidationUtils.isValidRatingBar(ratingBar, 0, 5));
    }

    private void checkRadioButton(TextView textView, RadioButton yes, RadioButton no) {
        enabledDisabledField(textView, ValidationUtils.isValidRadiobutton(yes, no));
    }

    private void checkText(TextView textView, EditText editText) {
        enabledDisabledField(textView, ValidationUtils.isValidString(editText.getText().toString()));
    }

    private void checkCurrentView() {
        if (getActivity().getCurrentFocus() != null) {
            if (getActivity().getCurrentFocus().getId() == txtComment.getId()) {
                checkText(lblComment, txtComment);
            }
        }
    }

    private void checkAll() {
        checkRating(lblCleaning, ratCleaning);
        checkRating(lblSize, ratSize);
        checkRadioButton(lblLatch, yesLatch, noLatch);
        checkRadioButton(lblPaper, yesPaper, noPaper);
        checkRadioButton(lblDisabled, yesDisabled, noDisabled);
        checkRadioButton(lblUnisex, yesUnisex, noUnisex);
        checkText(lblDate, txtDate);
        checkText(lblComment, txtComment);
    }

    private boolean validateAll() {
        checkAll();
        View[] array = new View[]{lblCleaning, lblSize, lblLatch, lblPaper, lblDisabled, lblUnisex, lblDate, lblComment};
        for (View view : array) {
            if (!view.isEnabled()) {
                return false;
            }
        }
        return true;
    }

    private void enabledDisabledField(TextView textView, boolean valid) {
        if (valid) {
            textView.setError(null);
        } else {
            textView.setError("Dato inválido");
        }
        textView.setEnabled(valid);
        selectStateView(textView, valid);
    }

    private void selectStateView(View view, boolean state) {
        if (view == lblCleaning) {
            viewModelOpinion.setStateCleaning(state);
        } else if (view == lblSize) {
            viewModelOpinion.setStateSize(state);
        } else if (view == lblLatch) {
            viewModelOpinion.setStateLatch(state);
        } else if (view == lblPaper) {
            viewModelOpinion.setStatePaper(state);
        } else if (view == lblDisabled) {
            viewModelOpinion.setStateDisabled(state);
        } else if (view == lblUnisex) {
            viewModelOpinion.setStateUnisex(state);
        } else if (view == lblDate) {
            viewModelOpinion.setStateDate(state);
        } else if (view == lblComment) {
            viewModelOpinion.setStateComment(state);
        }
    }

    private void showDateDialogPicker(EditText editText) {
        Calendar mcurrentDate = Calendar.getInstance();
        int day = mcurrentDate.get(Calendar.DAY_OF_MONTH);
        int month = mcurrentDate.get(Calendar.MONTH);
        int year = mcurrentDate.get(Calendar.YEAR);
        DatePickerDialog mDatePicker;

        mDatePicker = new DatePickerDialog(requireActivity(), (view, year1, month1, dayOfMonth) -> editText.setText(String.format("%02d/%02d/%d", dayOfMonth, (month1 + 1), year1)), year, month, day);

        mDatePicker.getDatePicker().setMaxDate(mcurrentDate.getTimeInMillis()); //Limita la fecha a hoy
        mDatePicker.show();
    }

    private void setFocusListeners() {
        txtComment.setOnFocusChangeListener((v, hasFocus) -> setBold(lblComment, txtComment));
    }

    private void setBold(TextView label, View focus) {
        if (focus.hasFocus() || focus == null) {
            label.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else {
            label.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }
    }

    //Borrar baño en el caso de no querer dejar una opinión
    private void deleteBath() {
        if (new_bath != null) {
            database.collection("banios").document(new_bath)
                    .delete()
                    .addOnSuccessListener(aVoid -> Log.d("", "DocumentSnapshot successfully deleted!"))
                    .addOnFailureListener(e -> Log.d("", "Error deleting document", e));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (!btnSaveClicked) {
            deleteBath();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (!btnSaveClicked) {
            deleteBath();
        }
    }
}
