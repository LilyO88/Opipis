package com.lidorttol.opipis.ui.map;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.lidorttol.opipis.data.Banio;
import com.lidorttol.opipis.data.Opinion;

import java.util.List;

public class MapFragmentViewModel extends AndroidViewModel {

    private MutableLiveData<List<Opinion>> opinionsLiveData;
    private MutableLiveData<List<Banio>> baniosLiveData;

    public MapFragmentViewModel(@NonNull Application application) {
        super(application);
        opinionsLiveData = new MutableLiveData<>();
        baniosLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<List<Opinion>> getOpinionsLiveData() {
        return opinionsLiveData;
    }

    public void setOpinionsLiveData(List<Opinion> opinions) {
        this.opinionsLiveData.postValue(opinions);
    }

    public MutableLiveData<List<Banio>> getBaniosLiveData() {
        return baniosLiveData;
    }

    public void setBaniosLiveData(List<Banio> banios) {
        this.baniosLiveData .postValue(banios);
    }
}
