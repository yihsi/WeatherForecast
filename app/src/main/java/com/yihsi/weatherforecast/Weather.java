package com.yihsi.weatherforecast;

import android.content.Context;

/**
 * Created by yihsi on 5/3/16.
 */
public class Weather {
    // Letters in brackets are keys of json format weather data

    // Store the serial number daytime weather phenomena(fa)
    private String[] mDayWeatherPhenomena = new String[3];
    // Store the serial number of night weather phenomena(fb)
    private String[] mNightWeatherPhenomena = new String[3];
    // Store daytime temperatures(Celsius,fc)
    private String[] mDayTemperatures = new String[3];
    // Store night temperatures(Celsius,fd)
    private String[] mNightTemperatures = new String[3];
    // Store the serial number of daytime wind direction(fe)
    private String[] mDayWindDirections = new String[3];
    // Store the serial number night wind direction(ff)
    private String[] mNightWindDirections = new String[3];
    // Store the serial number of daytime wind power(fg)
    private String[] mDayWindPowers = new String[3];
    // Store the serial number of night wind power(fh)
    private String[] mNightWindPowers = new String[3];
    // Store the time of sunrise and sunset
    private String[] mSunriseAndSunsetTimes = new String[3];

    private static Weather sWeather;
    private Context mContext;

    private Weather(Context context) {
        mContext = context;
    }

    public static Weather getInstance(Context context) {
        if (sWeather == null) {
            sWeather = new Weather(context.getApplicationContext());
        }
        return sWeather;
    }

    public String getDayTemperature(int i) {
        return mDayTemperatures[i];
    }

    public void setDayTemperatures(int i, String s) {
        mDayTemperatures[i] = s;
    }

    public String getDayWeatherPhenomenon(int i) {
        return mDayWeatherPhenomena[i];
    }

    public void setDayWeatherPhenomena(int i, String s) {
        mDayWeatherPhenomena[i] = s;
    }

    public String getDayWindDirection(int i) {
        return mDayWindDirections[i];
    }

    public void setDayWindDirections(int i, String s) {
        mDayWindDirections[i] = s;
    }

    public String getDayWindPower(int i) {
        return mDayWindPowers[i];
    }

    public void setDayWindPowers(int i, String s) {
        mDayWindPowers[i] = s;
    }

    public String getNightTemperature(int i) {
        return mNightTemperatures[i];
    }

    public void setNightTemperatures(int i, String s) {
        mNightTemperatures[i] = s;
    }

    public String getNightWeatherPhenomenon(int i) {
        return mNightWeatherPhenomena[i];
    }

    public void setNightWeatherPhenomena(int i, String s) {
        mNightWeatherPhenomena[i] = s;
    }

    public String getNightWindDirection(int i) {
        return mNightWindDirections[i];
    }

    public void setNightWindDirections(int i, String s) {
        mNightWindDirections[i] = s;
    }

    public String getNightWindPower(int i) {
        return mNightWindPowers[i];
    }

    public void setNightWindPowers(int i, String s) {
        mNightWindPowers[i] = s;
    }

    public String getSunriseAndSunsetTime(int i) {
        return mSunriseAndSunsetTimes[i];
    }

    public void setSunriseAndSunsetTimes(int i, String s) {
        mSunriseAndSunsetTimes[i] = s;
    }
}