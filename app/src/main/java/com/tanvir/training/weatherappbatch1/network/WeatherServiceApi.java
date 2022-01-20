package com.tanvir.training.weatherappbatch1.network;

import com.tanvir.training.weatherappbatch1.models.current.CurrentResponseModel;
import com.tanvir.training.weatherappbatch1.models.forecast.ForecastResponseModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface WeatherServiceApi {

    @GET()
    Call<CurrentResponseModel> getCurrentData(@Url String endUrl);

    @GET()
    Call<ForecastResponseModel> getForecastData(@Url String endUrl);
}
