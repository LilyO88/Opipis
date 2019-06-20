package com.lidorttol.opipis.ui.profile.change;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class ChangePasswordFragmentViewModel extends AndroidViewModel {

    private boolean stateOldPassword = true;
    private boolean stateNewPassword = true;
    private boolean stateConfirmPassword = true;

    public ChangePasswordFragmentViewModel(@NonNull Application application) {
        super(application);
    }

    public boolean isStateOldPassword() {
        return stateOldPassword;
    }

    public void setStateOldPassword(boolean stateOldPassword) {
        this.stateOldPassword = stateOldPassword;
    }

    public boolean isStateNewPassword() {
        return stateNewPassword;
    }

    public void setStateNewPassword(boolean stateNewPassword) {
        this.stateNewPassword = stateNewPassword;
    }

    public boolean isStateConfirmPassword() {
        return stateConfirmPassword;
    }

    public void setStateConfirmPassword(boolean stateConfirmPassword) {
        this.stateConfirmPassword = stateConfirmPassword;
    }
}
