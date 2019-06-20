package com.lidorttol.opipis.ui.profile.register;


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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lidorttol.opipis.R;
import com.lidorttol.opipis.data.Usuario;
import com.lidorttol.opipis.ui.main.MainActivity;
import com.lidorttol.opipis.utils.KeyboardUtils;
import com.lidorttol.opipis.utils.ValidationUtils;

import java.util.HashMap;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {


    private NavController navController;
    private RegisterFragmentViewModel viewModelRegister;
    private FirebaseAuth fAuth;
    private FirebaseFirestore database;
    private TextView lblName;
    private EditText txtName;
    private TextView lblEmail;
    private EditText txtEmail;
    private TextView lblPassword;
    private EditText txtPassword;
    private TextView lblConfirmPassword;
    private EditText txtConfirmPassword;
    private Button btnRegister;
    private ConstraintLayout cl_register;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = NavHostFragment.findNavController(RegisterFragment.this);
        viewModelRegister = ViewModelProviders.of(this).get(RegisterFragmentViewModel.class);
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
        lblName = ViewCompat.requireViewById(requireView(), R.id.reg_lblName);
        txtName = ViewCompat.requireViewById(requireView(), R.id.reg_txtName);

        lblEmail = ViewCompat.requireViewById(requireView(), R.id.reg_lblEmail);
        txtEmail = ViewCompat.requireViewById(requireView(), R.id.reg_txtEmail);

        lblPassword = ViewCompat.requireViewById(requireView(), R.id.reg_lblPassword);
        txtPassword = ViewCompat.requireViewById(requireView(), R.id.reg_txtPassword);

        lblConfirmPassword = ViewCompat.requireViewById(requireView(), R.id.reg_lblPasswordRep);
        txtConfirmPassword = ViewCompat.requireViewById(requireView(), R.id.reg_txtPasswordRep);

        btnRegister = ViewCompat.requireViewById(getView(),R.id.reg_btnRegister);

        cl_register = ViewCompat.requireViewById(getView(), R.id.cl_register);

        GestorTextWatcher gestorTextWatcher = new GestorTextWatcher();

        txtName.addTextChangedListener(gestorTextWatcher);
        txtEmail.addTextChangedListener(gestorTextWatcher);
        txtPassword.addTextChangedListener(gestorTextWatcher);
        txtConfirmPassword.addTextChangedListener(gestorTextWatcher);

        //Teclado acción
        txtConfirmPassword.setOnEditorActionListener((v, actionId, event) -> {
            KeyboardUtils.hideSoftKeyboard(requireActivity());
            doRegister();
            return false;
        });

        setupListeners();
        setFocusListeners();
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateAll()) {
                    doRegister();
                } else {
                    if(!txtPassword.getText().toString().equals(txtConfirmPassword.getText().toString())) { //Contraseñas distintas
                        Snackbar.make(cl_register, "Las contraseñas no coinciden", Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(cl_register, "Revise los campos erróneos", Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void doRegister() {
        fAuth.createUserWithEmailAndPassword(txtEmail.getText().toString(), txtPassword.getText().toString())
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("", "createUserWithEmail:success");
                            FirebaseUser user = fAuth.getCurrentUser();
                            //Agregar nombre antes en el usuario
                            addNameUser(user);
                            saveUser(user); //Guardar también en la base de datos como usuario
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("", "createUserWithEmail:failure", task.getException());
                            Snackbar.make(cl_register, "No se ha podido crear el usuario", Snackbar.LENGTH_LONG).show();
                        }

                    }
                });
        Snackbar.make(cl_register, "¡Perfil creado correctamente!", Snackbar.LENGTH_LONG).show();
        navController.popBackStack();
    }

    private void addNameUser(FirebaseUser user) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(txtName.getText().toString().trim())  //Nuevo nombre
                .build();
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("", "User profile updated.");
                        }
                    }
                });
    }

    private void saveUser(FirebaseUser user) {
        Map<String, Object> usuario = new HashMap<>();
        usuario.put("nombre", txtName.getText().toString().trim());
        usuario.put("id_usuario", user.getUid().trim());
        usuario.put("email", txtEmail.getText().toString().trim());

        database.collection("usuarios").document(user.getUid()).set(usuario)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
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
    }

    private void validateFields() {
        lblEmail.setEnabled(viewModelRegister.isStateEmail());
        lblName.setEnabled(viewModelRegister.isStateName());
        lblPassword.setEnabled(viewModelRegister.isStatePassword());
        lblConfirmPassword.setEnabled(viewModelRegister.isStateConfirmPassword());
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

    private void checkCurrentView() {
        if (getActivity().getCurrentFocus() != null) {
            if (getActivity().getCurrentFocus().getId() == txtEmail.getId()) {
                checkEmail(lblEmail, txtEmail);
            } else if (getActivity().getCurrentFocus().getId() == txtName.getId()) {
                checkText(lblName, txtName);
            } else if (getActivity().getCurrentFocus().getId() == txtPassword.getId()) {
                checkText(lblPassword, txtPassword);
            } else if (getActivity().getCurrentFocus().getId() == txtConfirmPassword.getId()) {
                checkText(lblConfirmPassword, txtConfirmPassword);
            }
        }
    }

    private void checkAll() {
        checkEmail(lblEmail, txtEmail);
        checkText(lblName, txtName);
        checkText(lblPassword, txtPassword);
        checkText(lblConfirmPassword, txtConfirmPassword);
    }

    private boolean validateAll() {
        checkAll();
        View[] array = new View[]{lblEmail, lblName, lblPassword, lblConfirmPassword};
        for (View view : array) {
            if (!view.isEnabled()) {
                return false;
            }
        }
        if(!txtPassword.getText().toString().equals(txtConfirmPassword.getText().toString())) { //Contraseñas distintas
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
        if (view == lblEmail) {
            viewModelRegister.setStateEmail(state);
        } else if (view == lblName) {
            viewModelRegister.setStateName(state);
        } else if (view == lblPassword) {
            viewModelRegister.setStatePassword(state);
        } else if (view == lblConfirmPassword) {
            viewModelRegister.setStateConfirmPassword(state);
        }
    }


    private void setFocusListeners() {
        txtName.setOnFocusChangeListener((v, hasFocus) -> setBold(lblName, txtName));
        txtEmail.setOnFocusChangeListener((v, hasFocus) -> setBold(lblEmail, txtEmail));
        txtPassword.setOnFocusChangeListener((v, hasFocus) -> setBold(lblPassword, txtPassword));
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
