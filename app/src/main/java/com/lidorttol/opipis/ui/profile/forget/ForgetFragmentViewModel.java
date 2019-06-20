package com.lidorttol.opipis.ui.profile.forget;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class ForgetFragmentViewModel extends AndroidViewModel {

    private boolean stateEmail = true;

    public ForgetFragmentViewModel(@NonNull Application application) {
        super(application);
    }


    public boolean isStateEmail() {
        return stateEmail;
    }

    public void setStateEmail(boolean stateEmail) {
        this.stateEmail = stateEmail;
    }
}
