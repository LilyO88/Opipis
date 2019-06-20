package com.lidorttol.opipis.ui.profile.change;


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
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lidorttol.opipis.R;
import com.lidorttol.opipis.ui.main.MainActivity;
import com.lidorttol.opipis.utils.KeyboardUtils;
import com.lidorttol.opipis.utils.ValidationUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePasswordFragment extends Fragment {


    private NavController navController;
    private ChangePasswordFragmentViewModel viewModelChange;
    private FirebaseAuth fAuth;
    private FirebaseFirestore database;
    private TextView lblOldPassword;
    private EditText txtOldPassword;
    private TextView lblNewPassword;
    private EditText txtNewPassword;
    private TextView lblConfirmPassword;
    private EditText txtConfirmPassword;
    private Button btnUpdate;
    private ConstraintLayout cl_change;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = NavHostFragment.findNavController(ChangePasswordFragment.this);
        viewModelChange = ViewModelProviders.of(this).get(ChangePasswordFragmentViewModel.class);
        fAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        if (!MainActivity.isConnected()) {
            navController.navigate(R.id.noInternetFragment);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupViews();
        validateFields();
    }

    private void setupViews() {
        lblOldPassword = ViewCompat.requireViewById(requireView(), R.id.ch_lblOldPassword);
        txtOldPassword = ViewCompat.requireViewById(requireView(), R.id.ch_txtOldPassword);

        lblNewPassword = ViewCompat.requireViewById(requireView(), R.id.ch_lblNewPassword);
        txtNewPassword = ViewCompat.requireViewById(requireView(), R.id.ch_txtNewPassword);

        lblConfirmPassword = ViewCompat.requireViewById(requireView(), R.id.ch_lblConfirmPassword);
        txtConfirmPassword = ViewCompat.requireViewById(requireView(), R.id.ch_txtConfirmPassword);

        btnUpdate = ViewCompat.requireViewById(getView(),R.id.ch_btnUpdate);

        cl_change = ViewCompat.requireViewById(getView(), R.id.cl_change);

        GestorTextWatcher gestorTextWatcher = new GestorTextWatcher();

        txtOldPassword.addTextChangedListener(gestorTextWatcher);
        txtNewPassword.addTextChangedListener(gestorTextWatcher);
        txtConfirmPassword.addTextChangedListener(gestorTextWatcher);

        //Teclado acción
        txtConfirmPassword.setOnEditorActionListener((v, actionId, event) -> {
            KeyboardUtils.hideSoftKeyboard(requireActivity());
            doUpdate();
            return false;
        });

        setupListeners();
        setFocusListeners();
    }

    private void setupListeners() {
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateAll()) {
                    doUpdate();
                } else {
                    if(!txtNewPassword.getText().toString().equals(txtConfirmPassword.getText().toString())) { //Contraseñas distintas
                        Snackbar.make(cl_change, "Las contraseñas no coinciden", Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(cl_change, "Revise los campos erróneos", Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void doUpdate() {
        //Autenticar de nuevo para comprobar antigua contraseña
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail().trim(), txtOldPassword.getText().toString().trim());

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Log.d("", "User re-authenticated.");
                            updatePassword(user);
                            Snackbar.make(cl_change, "¡Contraseña actualizada correctamente!", Snackbar.LENGTH_LONG).show();
                            navController.popBackStack();
                        } else {
                            Snackbar.make(cl_change, "La contraseña actual no es correcta", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void updatePassword(FirebaseUser user) {
        user.updatePassword(txtNewPassword.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("", "User password updated.");
                        }
                    }
                });
    }

    private void validateFields() {
        lblOldPassword.setEnabled(viewModelChange.isStateOldPassword());
        lblNewPassword.setEnabled(viewModelChange.isStateNewPassword());
        lblConfirmPassword.setEnabled(viewModelChange.isStateConfirmPassword());
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

    private void checkText(TextView textView, EditText editText) {
        enabledDisabledField(textView, ValidationUtils.isValidString(editText.getText().toString().trim()));
    }

    private void checkCurrentView() {
        if (getActivity().getCurrentFocus() != null) {
            if (getActivity().getCurrentFocus().getId() == txtOldPassword.getId()) {
                checkText(lblOldPassword, txtOldPassword);
            } else if (getActivity().getCurrentFocus().getId() == txtNewPassword.getId()) {
                checkText(lblNewPassword, txtNewPassword);
            } else if (getActivity().getCurrentFocus().getId() == txtConfirmPassword.getId()) {
                checkText(lblConfirmPassword, txtConfirmPassword);
            }
        }
    }

    private void checkAll() {
        checkText(lblOldPassword, txtOldPassword);
        checkText(lblNewPassword, txtNewPassword);
        checkText(lblConfirmPassword, txtConfirmPassword);
    }

    private boolean validateAll() {
        checkAll();
        View[] array = new View[]{lblOldPassword, lblNewPassword, lblConfirmPassword};
        for (View view : array) {
            if (!view.isEnabled()) {
                return false;
            }
        }
        if(!txtNewPassword.getText().toString().equals(txtConfirmPassword.getText().toString())) { //Contraseñas distintas
            return false;
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
        if (view == lblOldPassword) {
            viewModelChange.setStateOldPassword(state);
        } else if (view == lblNewPassword) {
            viewModelChange.setStateNewPassword(state);
        } else if (view == lblConfirmPassword) {
            viewModelChange.setStateConfirmPassword(state);
        }
    }


    private void setFocusListeners() {
        txtOldPassword.setOnFocusChangeListener((v, hasFocus) -> setBold(lblOldPassword, txtOldPassword));
        txtNewPassword.setOnFocusChangeListener((v, hasFocus) -> setBold(lblNewPassword, txtNewPassword));
        txtConfirmPassword.setOnFocusChangeListener((v, hasFocus) -> setBold(lblConfirmPassword, txtConfirmPassword));
    }

    private void setBold(TextView label, EditText editText) {
        if (editText.hasFocus() || editText == null) {
            label.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else {
            label.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }
    }

}
