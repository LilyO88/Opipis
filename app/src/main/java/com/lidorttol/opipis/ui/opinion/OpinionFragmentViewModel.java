package com.lidorttol.opipis.ui.opinion;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.lidorttol.opipis.data.Banio;

public class OpinionFragmentViewModel extends AndroidViewModel {

    private MutableLiveData<Banio> banioParam ;
    private MutableLiveData<String> lastOpinionIdLD;

    private boolean stateCleaning = true;
    private boolean stateSize = true;
    private boolean stateLatch = true;
    private boolean statePaper = true;
    private boolean stateDisabled = true;
    private boolean stateUnisex = true;
    private boolean stateDate = true;
    private boolean stateComment = true;

    public OpinionFragmentViewModel(@NonNull Application application) {
        super(application);
        banioParam = new MutableLiveData<>();
        lastOpinionIdLD = new MutableLiveData<>();
    }

    public LiveData<Banio> getBanioParam() {
        return banioParam;
    }

    public void setBanioParam(Banio banio) {
        this.banioParam.postValue(banio);
    }


    public boolean isStateCleaning() {
        return stateCleaning;
    }

    public void setStateCleaning(boolean stateCleaning) {
        this.stateCleaning = stateCleaning;
    }

    public boolean isStateSize() {
        return stateSize;
    }

    public void setStateSize(boolean stateSize) {
        this.stateSize = stateSize;
    }

    public boolean isStateLatch() {
        return stateLatch;
    }

    public void setStateLatch(boolean stateLatch) {
        this.stateLatch = stateLatch;
    }

    public boolean isStatePaper() {
        return statePaper;
    }

    public void setStatePaper(boolean statePaper) {
        this.statePaper = statePaper;
    }

    public boolean isStateDisabled() {
        return stateDisabled;
    }

    public void setStateDisabled(boolean stateDisabled) {
        this.stateDisabled = stateDisabled;
    }

    public boolean isStateUnisex() {
        return stateUnisex;
    }

    public void setStateUnisex(boolean stateUnisex) {
        this.stateUnisex = stateUnisex;
    }

    public boolean isStateDate() {
        return stateDate;
    }

    public void setStateDate(boolean stateDate) {
        this.stateDate = stateDate;
    }

    public boolean isStateComment() {
        return stateComment;
    }

    public void setStateComment(boolean stateComment) {
        this.stateComment = stateComment;
    }

    public LiveData<String> getLastOpinionIdLD() {
        return lastOpinionIdLD;
    }

    public void setLastOpinionIdLD(String lastOpinionId) {
        this.lastOpinionIdLD.postValue(lastOpinionId);
    }
}
