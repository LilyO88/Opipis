package com.lidorttol.opipis.ui.profile.edit;


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
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lidorttol.opipis.R;
import com.lidorttol.opipis.data.Usuario;
import com.lidorttol.opipis.ui.main.MainActivity;
import com.lidorttol.opipis.utils.KeyboardUtils;
import com.lidorttol.opipis.utils.ValidationUtils;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends Fragment {

    NavController navController;
    private EditProfileFragmentViewModel viewModelEdit;
    private FirebaseAuth fAuth;
//    private Usuario currentUser;
    private TextView lblName;
    private EditText txtName;
    private TextView lblEmail;
    private EditText txtEmail;
    private TextView lblOldPassword;
    private EditText txtOldPassword;
    private Button btnSave;
    private ConstraintLayout cl_edit;
    private FirebaseUser userFirebase;
    private FirebaseFirestore database;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = NavHostFragment.findNavController(EditProfileFragment.this);
        viewModelEdit = ViewModelProviders.of(this).get(EditProfileFragmentViewModel.class);
        fAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        if (!MainActivity.isConnected()) {
            navController.navigate(R.id.noInternetFragment);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getCurrentUser();

        setupViews();
        validateFields();
        setUser();
    }

    private void setUser() {
        txtName.setText(userFirebase.getDisplayName().trim());
        txtEmail.setText(userFirebase.getEmail().trim());
    }

    private void getCurrentUser() {
        userFirebase = fAuth.getCurrentUser();
//        currentUser = new Usuario(userFirebase.getUid(), userFirebase.getDisplayName(), userFirebase.getEmail());
    }

    private void setupViews() {
        lblName = ViewCompat.requireViewById(requireView(), R.id.prof_lblName);
        txtName = ViewCompat.requireViewById(requireView(), R.id.prof_txtName);

        lblEmail = ViewCompat.requireViewById(requireView(), R.id.prof_lblEmail);
        txtEmail = ViewCompat.requireViewById(requireView(), R.id.prof_txtEmail);

        lblOldPassword = ViewCompat.requireViewById(requireView(), R.id.prof_lblOldPassword);
        txtOldPassword = ViewCompat.requireViewById(requireView(), R.id.prof_txtOldPassword);

        btnSave = ViewCompat.requireViewById(getView(), R.id.prof_btnSave);

        cl_edit = ViewCompat.requireViewById(getView(), R.id.cl_editProfile);

        GestorTextWatcher gestorTextWatcher = new GestorTextWatcher();

        txtName.addTextChangedListener(gestorTextWatcher);
        txtEmail.addTextChangedListener(gestorTextWatcher);
        txtOldPassword.addTextChangedListener(gestorTextWatcher);

        //Teclado acción
        txtOldPassword.setOnEditorActionListener((v, actionId, event) -> {
            KeyboardUtils.hideSoftKeyboard(requireActivity());
            doUpdate();
            return false;
        });

        setupListeners();
        setFocusListeners();
    }

    private void setupListeners() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateAll()) {
                    doUpdate();
                } else {
                    Snackbar.make(cl_edit, "Revise los campos erróneos", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    /*private void doUpdate() {
        AuthCredential credential = EmailAuthProvider.getCredential(txtEmail.getText().toString().trim(), txtOldPassword.getText().toString().trim());
// Prompt the user to re-provide their sign-in credentials
        userFirebase.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) { //Si el re-login es correto se actualiza el usuario de authentication y en la base de datos
                        Log.d("", "User re-authenticated.");
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(txtName.getText().toString().trim())  //Nuevo nombre
                                .build();
                        userFirebase.updateProfile(profileUpdates)  //Actualización de la base de datos con los nuevos datos
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("", "User profile updated.");
                                            database.collection("usuarios").document(userFirebase.getUid())
                                                    .update("nombre", txtName.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("", "DocumentSnapshot successfully updated!");
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("", "Error updating document", e);
                                                }
                                            });
                                            Snackbar.make(cl_edit, "Nombre actualizado", Snackbar.LENGTH_LONG).show();
                                        }
                                    }
                                });
//                        navController.popBackStack();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(cl_edit, "Contraseña incorrecta", Snackbar.LENGTH_LONG).show();
            }
        });
    }*/

    private void doUpdate() {
        AuthCredential credential = EmailAuthProvider.getCredential(txtEmail.getText().toString().trim(), txtOldPassword.getText().toString().trim());
        userFirebase.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) { //Si el re-login es correto se actualiza el usuario de authentication y en la base de datos
                        if (task.isSuccessful()) {
//                            Log.d("", "User re-authenticated.");
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(txtName.getText().toString().trim()).build();
                            userFirebase.updateProfile(profileUpdates)  //Actualización de la base de datos con los nuevos datos
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("", "User profile updated.");
                                            database.collection("usuarios").document(userFirebase.getUid())
                                                    .update("nombre", txtName.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("", "DocumentSnapshot successfully updated!");
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("", "Error updating document", e);
                                                }
                                            });
//                                                Snackbar.make(cl_login, "Nombre actualizado", Snackbar.LENGTH_LONG).show();
                                        }
                                    });
                            Snackbar.make(cl_edit, "¡Perfil actualizado correctamente!", Snackbar.LENGTH_LONG).show();
                            navController.popBackStack();
                        } else {
//                            ConstraintLayout cl_login = ViewCompat.requireViewById(getView(), R.id.cl_login);
                            Snackbar.make(cl_edit, "Contraseña incorrecta", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void validateFields() {
        lblEmail.setEnabled(viewModelEdit.isStateEmail());
        lblName.setEnabled(viewModelEdit.isStateName());
        lblOldPassword.setEnabled(viewModelEdit.isStateOldPassword());
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

    private void checkEmail(TextView textView, EditText editText) {
        enabledDisabledField(textView, ValidationUtils.isValidEmail(editText.getText().toString().trim()));
    }

    private void checkPassword(TextView textView, EditText editText) {
        enabledDisabledField(textView, ValidationUtils.isValidPassword(editText.getText().toString().trim()));
    }

    private void checkCurrentView() {
        if (getActivity().getCurrentFocus() != null) {
            if (getActivity().getCurrentFocus().getId() == txtEmail.getId()) {
                checkEmail(lblEmail, txtEmail);
            } else if (getActivity().getCurrentFocus().getId() == txtName.getId()) {
                checkText(lblName, txtName);
            } else if (getActivity().getCurrentFocus().getId() == txtOldPassword.getId()) {
                checkPassword(lblOldPassword, txtOldPassword);
            }
        }
    }

    private void checkAll() {
        checkEmail(lblEmail, txtEmail);
        checkText(lblName, txtName);
        checkPassword(lblOldPassword, txtOldPassword);
    }

    private boolean validateAll() {
        checkAll();
        View[] array = new View[]{lblEmail, lblName, lblOldPassword};
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
        if (view == lblEmail) {
            viewModelEdit.setStateEmail(state);
        } else if (view == lblName) {
            viewModelEdit.setStateName(state);
        } else if (view == lblOldPassword) {
            viewModelEdit.setStateOldPassword(state);
        }
    }

    private void setFocusListeners() {
        txtName.setOnFocusChangeListener((v, hasFocus) -> setBold(lblName, txtName));
        txtOldPassword.setOnFocusChangeListener((v, hasFocus) -> setBold(lblOldPassword, txtOldPassword));
    }

    private void setBold(TextView label, EditText editText) {
        if (editText.hasFocus() || editText == null) {
            label.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else {
            label.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }
    }

}
