package com.lidorttol.opipis.ui.opinion;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.lidorttol.opipis.data.Banio;

public class OpinionFragmentViewModel extends AndroidViewModel {

    private MutableLiveData<Banio> banioParam ;

    public OpinionFragmentViewModel(@NonNull Application application) {
        super(application);
        banioParam = new MutableLiveData<>();
    }

    public MutableLiveData<Banio> getBanioParam() {
        return banioParam;
    }

    public void setBanioParam(Banio banio) {
        this.banioParam.postValue(banio);
    }
}
