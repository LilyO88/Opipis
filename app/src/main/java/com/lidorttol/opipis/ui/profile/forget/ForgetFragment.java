package com.lidorttol.opipis.ui.profile.forget;

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
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.lidorttol.opipis.R;
import com.lidorttol.opipis.ui.main.MainActivity;
import com.lidorttol.opipis.utils.KeyboardUtils;
import com.lidorttol.opipis.utils.ValidationUtils;


/**
 * A simple {@link Fragment} subclass.
 */
public class ForgetFragment extends Fragment {

    private NavController navController;
    private FirebaseAuth fAuth;
    private TextView lblEmail;
    private EditText txtEmail;
    private Button btnForget;
    private ForgetFragmentViewModel viewModelForget;
    private ConstraintLayout cl_forget;

    public ForgetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forget, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = NavHostFragment.findNavController(ForgetFragment.this);
        viewModelForget = ViewModelProviders.of(this).get(ForgetFragmentViewModel.class);
        fAuth = FirebaseAuth.getInstance();

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
        lblEmail = ViewCompat.requireViewById(getView(), R.id.for_lblEmail);
        txtEmail = ViewCompat.requireViewById(getView(), R.id.for_txtEmail);
        btnForget = ViewCompat.requireViewById(getView(), R.id.for_btnForget);

        cl_forget = ViewCompat.requireViewById(getView(), R.id.cl_forget);

        GestorTextWatcher gestorTextWatcher = new GestorTextWatcher();
        txtEmail.addTextChangedListener(gestorTextWatcher);

        //Teclado acción
        txtEmail.setOnEditorActionListener((v, actionId, event) -> {
            KeyboardUtils.hideSoftKeyboard(requireActivity());
            getNewPassword();
            return false;
        });

        btnForget.setOnClickListener(v -> {
            if (validateAll()) {
                getNewPassword();
            } else {
                Snackbar.make(cl_forget, "El email introducido no es válido", Snackbar.LENGTH_LONG).show();
            }
        });

        setFocusListeners();
    }

    private void validateFields() {
        lblEmail.setEnabled(viewModelForget.isStateEmail());
    }

    public void getNewPassword() {
        fAuth.sendPasswordResetEmail(txtEmail.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("", "Email sent.");
/*                            Snackbar.make(cl_forget, "Se ha enviado un email de " +
                                    "reestablecimiento de contraseña a su correo", Snackbar.LENGTH_LONG).show();*/  //No funciona
                        }
                    }
                });
        navController.popBackStack();
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

    private void checkEmail(TextView textView, EditText editText) {
        enabledDisabledField(textView, ValidationUtils.isValidEmail(editText.getText().toString().trim()));
    }

    private void checkCurrentView() {
        if (getActivity().getCurrentFocus() != null) {
            if (getActivity().getCurrentFocus().getId() == txtEmail.getId()) {
                checkEmail(lblEmail, txtEmail);
            }
        }
    }

    private void checkAll() {
        checkEmail(lblEmail, txtEmail);
    }

    private boolean validateAll() {
        checkAll();
        View[] array = new View[]{lblEmail};
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
            viewModelForget.setStateEmail(state);
        }
    }


    private void setFocusListeners() {
        txtEmail.setOnFocusChangeListener((v, hasFocus) -> setBold(lblEmail, txtEmail));
    }

    private void setBold(TextView label, EditText editText) {
        if (editText.hasFocus() || editText == null) {
            label.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else {
            label.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }
    }
}
