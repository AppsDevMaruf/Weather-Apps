package com.tanvir.training.weatherappbatch1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;
import com.tanvir.training.weatherappbatch1.adapters.ForecastAdapter;
import com.tanvir.training.weatherappbatch1.databinding.FragmentWeatherBinding;
import com.tanvir.training.weatherappbatch1.models.current.CurrentResponseModel;
import com.tanvir.training.weatherappbatch1.prefs.WeatherPreference;
import com.tanvir.training.weatherappbatch1.utils.Constants;
import com.tanvir.training.weatherappbatch1.utils.LocationPermissionService;
import com.tanvir.training.weatherappbatch1.utils.WeatherHelperFunctions;
import com.tanvir.training.weatherappbatch1.viewmodels.WeatherViewModel;

public class WeatherFragment extends Fragment {
    private final String TAG = WeatherFragment.class.getSimpleName();
    private WeatherPreference preference;
    private WeatherViewModel viewModel;
    private FragmentWeatherBinding binding;
    private FusedLocationProviderClient providerClient;
    private ActivityResultLauncher<String> launcher =
            registerForActivityResult(new ActivityResultContracts
                            .RequestPermission(),
                    result -> {
                if (result) {
                    detectUserLocation();
                }else {
                    //show dialog and explain why you need this permission
                }
            });
    public WeatherFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.weather_menu, menu);
        final SearchView searchView =
                (SearchView) menu.findItem(R.id.item_search)
                .getActionView();
        searchView.setQueryHint("Search your city");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.setCity(query);
                viewModel.loadData();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_search) {

        }else if (item.getItemId() == R.id.item_mylocation) {
            viewModel.setCity(null);
            viewModel.loadData();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        preference = new WeatherPreference(getActivity());
        binding = FragmentWeatherBinding.inflate(inflater);
        providerClient = LocationServices
                .getFusedLocationProviderClient(getActivity());
        viewModel = new ViewModelProvider(requireActivity()).get(WeatherViewModel.class);
        binding.tempUnitSwitch.setChecked(preference.getTempStatus());
        viewModel.setUnit(preference.getTempStatus());
        final ForecastAdapter adapter = new ForecastAdapter();
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(RecyclerView.HORIZONTAL);
        binding.forecastRV.setLayoutManager(llm);
        binding.forecastRV.setAdapter(adapter);

        viewModel.getCurrentLiveData().observe(getViewLifecycleOwner(),
                currentResponseModel -> {
            setData(currentResponseModel);
                    Log.e(TAG, "current: "+currentResponseModel.getMain().getTemp());
        });
        viewModel.getForecastLiveData().observe(getViewLifecycleOwner(),
                forecastResponseModel -> {
                    Log.e(TAG, "forecast: "+forecastResponseModel.getList().size());
                    adapter.submitList(forecastResponseModel.getList());
        });
        viewModel.getErrMsgLiveData().observe(getViewLifecycleOwner(), msg -> {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        });

        binding.tempUnitSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preference.setTempStatus(isChecked);
                viewModel.setUnit(isChecked);
                viewModel.loadData();

            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkLocationPermission();
    }

    private void setData(CurrentResponseModel currentResponseModel) {
        binding.currentDateTV.setText(WeatherHelperFunctions
        .getFormattedDateTime(currentResponseModel.getDt(), "MMM dd, yyyy"));
        binding.currentAddressTV.setText(
                currentResponseModel.getName()+","+currentResponseModel.getSys().getCountry()
        );
        binding.currentTempTV.setText(
                String.format("%.0f\u00B0", currentResponseModel.getMain().getTemp())
        );

        binding.currentFeelsLikeTV.setText(
                String.format("feels like %.0f\u00B0", currentResponseModel.getMain().getFeelsLike())
        );

        binding.currentMaxMinTV.setText(
                String.format("Max %.0f\u00B0 Min %.0f\u00B0", currentResponseModel.getMain().getTempMax(),
                        currentResponseModel.getMain().getTempMin())
        );

        final String iconUrl = Constants.ICON_PREFIX+
                currentResponseModel.getWeather().get(0).getIcon()+
                Constants.ICON_SUFFIX;
        Picasso.get().load(iconUrl).into(binding.currentIconIV);
        binding.currentConditionTV.setText(
                currentResponseModel.getWeather().get(0).getDescription()
        );

        binding.currentHumidityTV.setText("Humidity "+
                currentResponseModel.getMain().getHumidity()+"%");
        binding.currentPressureTV.setText("Pressure "+
                currentResponseModel.getMain().getPressure()+"hPa");
    }

    private void checkLocationPermission() {
        if (LocationPermissionService.isLocationPermissionGranted(getActivity())) {
            detectUserLocation();
        } else {
            LocationPermissionService.requestLocationPermission(launcher);
        }
    }

    @SuppressLint("MissingPermission")
    private void detectUserLocation() {
        providerClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location == null) return;
                    //double lat = location.getLatitude();
                    //double lng = location.getLongitude();
                    viewModel.setLocation(location);
                    viewModel.loadData();
                    //Log.e("WeatherApp", "Lat:"+lat+",lon:"+lng);
                });
    }
}