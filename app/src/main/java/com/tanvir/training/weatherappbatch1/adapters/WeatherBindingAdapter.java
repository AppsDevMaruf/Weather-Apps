package com.tanvir.training.weatherappbatch1.adapters;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.squareup.picasso.Picasso;
import com.tanvir.training.weatherappbatch1.utils.Constants;
import com.tanvir.training.weatherappbatch1.utils.WeatherHelperFunctions;

public class WeatherBindingAdapter {

    @BindingAdapter(value = "app:setDateTime")
    public static void setDateTime(TextView tv, long dt) {
        final String dateTime =
                WeatherHelperFunctions.getFormattedDateTime(dt,
                        "EEE HH:mm");
        tv.setText(dateTime);
    }

    @BindingAdapter(value = "app:setIcon")
    public static void setIcon(ImageView imageView, String icon) {
        final String iconUrl = Constants.ICON_PREFIX+
                icon+Constants.ICON_SUFFIX;
        Picasso.get().load(iconUrl).into(imageView);
    }

    @BindingAdapter(value = {"app:tempMax", "app:tempMin"})
    public static void setMaxMinTemp(TextView tv, double max, double min) {
        tv.setText(String.format("%.0f/%.0f\u00B0", max, min));
    }
}
