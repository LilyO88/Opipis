package com.lidorttol.opipis.ui.profile.login;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class LoginFragmentViewModel extends AndroidViewModel {

    private boolean stateEmail = true;
    private boolean statePassword = true;

    public LoginFragmentViewModel(@NonNull Application application) {
        super(application);
    }

    public boolean isStateEmail() {
        return stateEmail;
    }

    public void setStateEmail(boolean stateEmail) {
        this.stateEmail = stateEmail;
    }

    public boolean isStatePassword() {
        return statePassword;
    }

    public void setStatePassword(boolean statePassword) {
        this.statePassword = statePassword;
    }
}
