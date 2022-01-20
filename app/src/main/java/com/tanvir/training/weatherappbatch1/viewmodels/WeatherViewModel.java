package com.tanvir.training.weatherappbatch1.viewmodels;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tanvir.training.weatherappbatch1.models.current.CurrentResponseModel;
import com.tanvir.training.weatherappbatch1.models.forecast.ForecastResponseModel;
import com.tanvir.training.weatherappbatch1.network.WeatherService;
import com.tanvir.training.weatherappbatch1.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherViewModel extends ViewModel {
    private final String TAG = WeatherViewModel.class.getSimpleName();
    private Location location;
    private MutableLiveData<CurrentResponseModel> currentLiveData =
            new MutableLiveData<>();
    private MutableLiveData<ForecastResponseModel> forecastLiveData =
            new MutableLiveData<>();

    public MutableLiveData<String> getErrMsgLiveData() {
        return errMsgLiveData;
    }

    private MutableLiveData<String> errMsgLiveData = new MutableLiveData<>();

    private String unit = Constants.TempUnit.CELSIUS;

    private String city = null;

    public void setCity(String city) {
        this.city = city;
    }
    public void setUnit(boolean status) {
        unit = status ? Constants.TempUnit.FAHRENHEIT :
                Constants.TempUnit.CELSIUS;
    }

    public LiveData<CurrentResponseModel> getCurrentLiveData() {
        return currentLiveData;
    }

    public LiveData<ForecastResponseModel> getForecastLiveData() {
        return forecastLiveData;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void loadData() {
        loadCurrentData();
        loadForecastData();
    }

    private void loadForecastData() {
        final String endUrl = city == null ?
                String.format("forecast?lat=%f&lon=%f&units=%s&appid=%s",
                location.getLatitude(), location.getLongitude(),
                unit, Constants.WEATHER_API_KEY) :
                String.format("forecast?q=%s&units=%s&appid=%s",
                        city, unit, Constants.WEATHER_API_KEY);
        WeatherService.getService().getForecastData(endUrl)
                .enqueue(new Callback<ForecastResponseModel>() {
                    @Override
                    public void onResponse(Call<ForecastResponseModel> call, Response<ForecastResponseModel> response) {
                        if (response.code() == 200) {
                            forecastLiveData.postValue(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<ForecastResponseModel> call, Throwable t) {
                        Log.e(TAG, "onFailure: "+t.getLocalizedMessage());
                    }


                });
    }

    private void loadCurrentData() {
        final String endUrl = city == null ?
                String.format("weather?lat=%f&lon=%f&units=%s&appid=%s",
                location.getLatitude(), location.getLongitude(), unit,
                Constants.WEATHER_API_KEY) :
                String.format("weather?q=%s&units=%s&appid=%s",
                        city, unit,
                        Constants.WEATHER_API_KEY);
        WeatherService.getService().getCurrentData(endUrl)
                .enqueue(new Callback<CurrentResponseModel>() {
                    @Override
                    public void onResponse(Call<CurrentResponseModel> call, Response<CurrentResponseModel> response) {
                        if (response.code() == 200) {
                            currentLiveData.postValue(response.body());
                        }else if (response.code() == 404) {
                            errMsgLiveData.postValue(response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<CurrentResponseModel> call, Throwable t) {
                        Log.e(TAG, "onFailure: "+t.getLocalizedMessage());
                    }
                });
    }
}
