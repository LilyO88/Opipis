package com.lidorttol.opipis.ui.profile.edit;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.lidorttol.opipis.data.Usuario;

public class EditProfileFragmentViewModel extends AndroidViewModel {

    private boolean stateName = true;
    private boolean stateEmail = true;
    private boolean stateOldPassword = true;

    public EditProfileFragmentViewModel(@NonNull Application application) {
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

    public boolean isStateOldPassword() {
        return stateOldPassword;
    }

    public void setStateOldPassword(boolean stateOldPassword) {
        this.stateOldPassword = stateOldPassword;
    }
}
