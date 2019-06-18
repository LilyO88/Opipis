package com.lidorttol.opipis.ui.opinionList;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.lidorttol.opipis.data.Banio;
import com.lidorttol.opipis.data.Opinion;

import java.util.List;

public class OpinionDetailFragmentViewModel extends AndroidViewModel {

    private MutableLiveData<Banio> banioParam;
    private MutableLiveData<Double> globalParam;
    private MutableLiveData<Integer> numOpinionsParam;
    private MutableLiveData<List<Opinion>> listOpinionsLiveData;

    public OpinionDetailFragmentViewModel(@NonNull Application application) {
        super(application);
        banioParam = new MutableLiveData<>();
        globalParam = new MutableLiveData<>();
        numOpinionsParam = new MutableLiveData<>();
        listOpinionsLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<Banio> getBanioParam() {
        return banioParam;
    }

    public void setBanioParam(Banio banio) {
        this.banioParam.postValue(banio);
    }

    public MutableLiveData<Double> getGlobalParam() {
        return globalParam;
    }

    public void setGlobalParam(double global) {
        this.globalParam.postValue(global);
    }

    public MutableLiveData<Integer> getNumOpinionsParam() {
        return numOpinionsParam;
    }

    public void setNumOpinionsParam(int numOpinions) {
        this.numOpinionsParam.postValue(numOpinions);
    }

    public MutableLiveData<List<Opinion>> getListOpinionsLiveData() {
        return listOpinionsLiveData;
    }

    public void setListOpinionsLiveData(List<Opinion> listOpinions) {
        this.listOpinionsLiveData.postValue(listOpinions);
    }
}
