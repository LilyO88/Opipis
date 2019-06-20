package com.lidorttol.opipis.ui.profile.register;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class RegisterFragmentViewModel extends AndroidViewModel {

    private boolean stateName = true;
    private boolean stateEmail = true;
    private boolean statePassword = true;
    private boolean stateConfirmPassword = true;

    public RegisterFragmentViewModel(@NonNull Application application) {
        super(application);
    }

    public boolean isStateName() {
        return stateName;
    }

    public void setStateName(boolean stateName) {
        this.stateName = stateName;
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

    public boolean isStateConfirmPassword() {
        return stateConfirmPassword;
    }

    public void setStateConfirmPassword(boolean stateConfirmPassword) {
        this.stateConfirmPassword = stateConfirmPassword;
    }
}