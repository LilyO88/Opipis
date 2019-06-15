package com.lidorttol.opipis.ui.main;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.lidorttol.opipis.data.Banio;

import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {

    private MutableLiveData<List<Banio>> baniosLiveData;
    private MutableLiveData<Boolean> connectedLiveData;
    private boolean location_ok;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        baniosLiveData = new MutableLiveData<>();
        connectedLiveData = new MutableLiveData<>();
        location_ok = false;
    }

    public LiveData<List<Banio>> getBaniosLiveData() {
        return baniosLiveData;
    }

    public void setBaniosLiveData(List<Banio> banios) {
        this.baniosLiveData.postValue(banios);
    }

    public LiveData<Boolean> getConnected() throws InterruptedException {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            connected = true;
        }
        this.connectedLiveData.postValue(connected);

        return this.connectedLiveData;
    }

    public boolean isLocation_ok() {
        return location_ok;
    }

    public void setLocation_ok(boolean locat) {
        this.location_ok = locat;
    }
}

