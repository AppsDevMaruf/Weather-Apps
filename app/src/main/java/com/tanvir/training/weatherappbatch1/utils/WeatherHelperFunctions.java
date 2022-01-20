package com.tanvir.training.weatherappbatch1.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WeatherHelperFunctions {
    public static String getFormattedDateTime(long date, String format) {
        return new SimpleDateFormat(format)
                .format(new Date(date * 1000L));
    }
}
