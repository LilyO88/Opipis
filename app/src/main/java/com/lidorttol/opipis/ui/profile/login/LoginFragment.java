package com.lidorttol.opipis.ui.profile.login;


import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.lidorttol.opipis.R;
import com.lidorttol.opipis.base.YesNoDialogFragment;
import com.lidorttol.opipis.ui.main.MainActivity;
import com.lidorttol.opipis.utils.ValidationUtils;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private static final String TAG_DIALOG_FRAGMENT = "TAG_DIALOG_FRAGMENT2";
    private static final int RC_DIALOG_FRAGMENT = 2;

    private FirebaseAuth fAuth;
    private EditText txtEmail;
    private EditText txtPassword;
    private Button btnLogin;
    private TextView lblForget;
    private TextView lblRegister;
    private TextView lblEmail;
    private TextView lblPassword;
    private LoginFragmentViewModel viewModelLogin;
    private ConstraintLayout cl_login;
    private NavController navController;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fAuth = FirebaseAuth.getInstance();
        viewModelLogin = ViewModelProviders.of(this).get(LoginFragmentViewModel.class);
        navController = NavHostFragment.findNavController(LoginFragment.this);

        setupViews();
        validateFields();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!MainActivity.isConnected()) {
            navController.navigate(R.id.noInternetFragment);
        }
    }

    private void setupViews() {
        lblEmail = ViewCompat.requireViewById(requireView(), R.id.login_lblEmail);
        txtEmail = ViewCompat.requireViewById(requireView(), R.id.login_txtEmail);
        lblPassword = ViewCompat.requireViewById(requireView(), R.id.login_lblPassword);
        txtPassword = ViewCompat.requireViewById(requireView(), R.id.login_txtPassword);
        btnLogin = ViewCompat.requireViewById(requireView(), R.id.login_btnLogin);
        lblForget = ViewCompat.requireViewById(requireView(), R.id.login_lblForget);
        lblRegister = ViewCompat.requireViewById(requireView(), R.id.login_lblRegister);
        cl_login = ViewCompat.requireViewById(requireView(), R.id.cl_login);

        GestorTextWatcher gestorTextWatcher = new GestorTextWatcher();

        txtEmail.addTextChangedListener(gestorTextWatcher);
        txtPassword.addTextChangedListener(gestorTextWatcher);

        setupListeners();
        setFocusListeners();
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> {
            if (validateAll()) {
                doLogin();
            } else {
                Snackbar.make(cl_login, "Revise los campos erróneos", Snackbar.LENGTH_LONG).show();
            }
        });
        lblForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.forgetFragment);
            }
        });
        lblRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /*private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                navController.navigate(R.id.forgetFragment);
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        }).setTitle("Confirmación").setMessage("Está ");
        AlertDialog dialog = builder.create();
        dialog.show();
    }*/

    private void doLogin() {
        fAuth.signInWithEmailAndPassword(txtEmail.getText().toString().trim(), txtPassword.getText().toString().trim())
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        /*Log.d("", "signInWithEmail:success");

                        FirebaseUser user = fAuth.getCurrentUser();*/
                        navController.popBackStack();
                    } else {
                        // If sign in fails, display a message to the user.
//                            Log.w("", "signInWithEmail:failure", task.getException());
                        Snackbar.make(cl_login, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    private void validateFields() {
        lblEmail.setEnabled(viewModelLogin.isStateEmail());
        lblPassword.setEnabled(viewModelLogin.isStatePassword());
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
            } else if (getActivity().getCurrentFocus().getId() == txtPassword.getId()) {
                checkText(lblPassword, txtPassword);
            }
        }
    }

    private void checkAll() {
        checkEmail(lblEmail, txtEmail);
        checkText(lblPassword, txtPassword);
    }

    private boolean validateAll() {
        checkAll();
        View[] array = new View[]{lblEmail, lblPassword};
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
            viewModelLogin.setStateEmail(state);
        } else if (view == lblPassword) {
            viewModelLogin.setStatePassword(state);
        }
    }


    private void setFocusListeners() {
        txtEmail.setOnFocusChangeListener((v, hasFocus) -> setBold(lblEmail, txtEmail));
        txtPassword.setOnFocusChangeListener((v, hasFocus) -> setBold(lblPassword, txtPassword));
    }

    private void setBold(TextView label, EditText editText) {
        if (editText.hasFocus() || editText == null) {
            label.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else {
            label.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }
    }
}
