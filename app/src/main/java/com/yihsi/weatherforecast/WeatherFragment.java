package com.yihsi.weatherforecast;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by yihsi on 4/26/16.
 */
public class WeatherFragment extends Fragment {

    private Location mLastLocation;
    private String mAreaName;

    private DatabaseManager mManager;

    private String[] mWeatherConditions = { "晴", "多云", "阴", "阵雨", "雷阵雨", "雷阵雨伴有冰雹",
            "雨夹雪", "小雨", "中雨", "大雨", "暴雨", "大暴雨", "特大暴雨", "阵雪", "小雪",
            "中雪", "大雪", "暴雪", "雾", "冻雨", "沙尘暴", "小到中雨", "中到大雨",
            "大到暴雨", "暴雨到大暴雨", "大暴雨到特大暴雨", "小到中雪", "中到大雪", "大到暴雪", "浮尘",
            "扬沙", "强沙尘暴", "霾" };
    private String[] mWindDirects = { "无持续风向", "东北风", "东风", "东南风", "南风", "西南风", "西风",
            "西北风", "北风", "旋转风" };
    private String[] mWindPowers = { "微风", "3-4级", "4-5级", "5-6级", "6-7级", "7-8级",
            "8-9级", "9-10级", "10-11级", "11-12级" };

    int[] mDayWeatherIconsArray = { R.drawable.d00, R.drawable.d01, R.drawable.d02,
            R.drawable.d03, R.drawable.d04, R.drawable.d05, R.drawable.d06,
            R.drawable.d07, R.drawable.d08, R.drawable.d09, R.drawable.d10,
            R.drawable.d11, R.drawable.d12, R.drawable.d13, R.drawable.d14,
            R.drawable.d15, R.drawable.d16, R.drawable.d17, R.drawable.d18,
            R.drawable.d19, R.drawable.d20, R.drawable.d21, R.drawable.d22,
            R.drawable.d23, R.drawable.d24, R.drawable.d25, R.drawable.d26,
            R.drawable.d27, R.drawable.d28, R.drawable.d29, R.drawable.d30,
            R.drawable.d31, R.drawable.d53 };

    int[] mNightWeatherIconsArray = { R.drawable.n00, R.drawable.n01, R.drawable.n02,
            R.drawable.n03, R.drawable.n04, R.drawable.n05, R.drawable.n06,
            R.drawable.n07, R.drawable.n08, R.drawable.n09, R.drawable.n10,
            R.drawable.n11, R.drawable.n12, R.drawable.n13, R.drawable.n14,
            R.drawable.n15, R.drawable.n16, R.drawable.n17, R.drawable.n18,
            R.drawable.n19, R.drawable.n20, R.drawable.n21, R.drawable.n22,
            R.drawable.n23, R.drawable.n24, R.drawable.n25, R.drawable.n26,
            R.drawable.n27, R.drawable.n28, R.drawable.n29, R.drawable.n30,
            R.drawable.n31, R.drawable.n53 };

    private TextView mCurrentWeather;
    private TextView mDayTemperature;
    private TextView mNightTemperature;
    private TextView mCurrentTemperature;
    private ImageView mCurrentWeatherImage;

    private TextView mTomorrowWeekDay;
    private ImageView mTomorrowIcon;
    private TextView mTomorrowTemperature;
    private TextView mThirdDayWeekDay;
    private ImageView mThirdDayIcon;
    private TextView mThirdDayTemperature;

    private TextView mWindDirect;
    private TextView mWindPower;
    private TextView mSunriseAndSunsetTime;
    private TextView mSuggestion;

    private static final String ARG_SEARCH_LOCATION = "SEARCH_LOCATION";

    public static WeatherFragment newInstance(String location) {
        Bundle args = new Bundle();
        args.putString(ARG_SEARCH_LOCATION, location);
        WeatherFragment fragment = new WeatherFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        setRetainInstance(true);

        Bundle args = getArguments();
        Log.i("args", String.valueOf(args));
        if (args == null) {

            LocationManager locationManager = (LocationManager) getActivity().getSystemService(
                    Context.LOCATION_SERVICE);

            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLastLocation = locationManager.getLastKnownLocation(LocationManager
                        .NETWORK_PROVIDER);

                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 10000,
                        new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                mLastLocation = location;
                            }

                            @Override
                            public void onStatusChanged(String provider, int status, Bundle extras) {

                            }

                            @Override
                            public void onProviderEnabled(String provider) {

                            }

                            @Override
                            public void onProviderDisabled(String provider) {

                            }
                        });
            }
        }
        else {
            mAreaName = args.getString(ARG_SEARCH_LOCATION, null);
            Log.i("mAreaName", mAreaName);
        }

        new GetAreaName().execute();

        // Copy weather.db in assets to phone
        mManager = DatabaseManager.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, parent, false);

        mCurrentWeather = (TextView)view.findViewById(R.id.current_weather_description);
        mDayTemperature = (TextView)view.findViewById(R.id.day_temperature);
        mNightTemperature = (TextView)view.findViewById(R.id.night_temperature);
        mCurrentTemperature = (TextView)view.findViewById(R.id.current_temperature);
        mCurrentWeatherImage = (ImageView)view.findViewById(R.id.current_weather_icon);

        mTomorrowWeekDay = (TextView)view.findViewById(R.id.tomorrow_weekday);
        mTomorrowIcon = (ImageView) view.findViewById(R.id.tomorrow_icon);
        mTomorrowTemperature = (TextView)view.findViewById(R.id.tomorrow_temperature);
        mThirdDayWeekDay = (TextView)view.findViewById(R.id.third_day_weekday);
        mThirdDayIcon = (ImageView)view.findViewById(R.id.third_day_icon);
        mThirdDayTemperature = (TextView)view.findViewById(R.id.third_day_temperature);

        mWindDirect = (TextView)view.findViewById(R.id.wind_direct);
        mWindPower = (TextView)view.findViewById(R.id.wind_power);
        mSunriseAndSunsetTime = (TextView)view.findViewById(R.id.sunrise_and_sunset_time);
        mSuggestion = (TextView)view.findViewById(R.id.suggestion);

        return view;
    }

    public void updateUI(Weather weather) {
        Calendar calendar = Calendar.getInstance();
        boolean isNight = (calendar.get(Calendar.HOUR_OF_DAY) >= 18) ||
                (calendar.get(Calendar.HOUR_OF_DAY) < 8);

        if (isNight) {
            mCurrentWeather.setText(mWeatherConditions[Integer.parseInt(
                    weather.getNightWeatherPhenomenon(0))]);
            mCurrentTemperature.setText(weather.getNightTemperature(0) + "°");
            mCurrentWeatherImage.setImageResource(mNightWeatherIconsArray[
                    Integer.parseInt(weather.getNightWeatherPhenomenon(0))]);
        }
        else {
            mCurrentWeather.setText(mWeatherConditions[Integer.parseInt(
                    weather.getDayWeatherPhenomenon(0))]);
            mCurrentTemperature.setText(weather.getDayTemperature(0) + "°");
            mCurrentWeatherImage.setImageResource(mDayWeatherIconsArray[
                    Integer.parseInt(weather.getDayWeatherPhenomenon(0))]);
        }
        if (weather.getDayTemperature(0).equals("")) {
            mDayTemperature.setText("白天: " + null);
        }
        else {
            mDayTemperature.setText("白天: " + weather.getDayTemperature(0) + "°");
        }
        mNightTemperature.setText("    夜晚: " + weather.getNightTemperature(0) + "°");

        int field = calendar.get(Calendar.DAY_OF_WEEK);
        mTomorrowWeekDay.setText(getWeekDay((field + 1) % 7));
        mTomorrowTemperature.setText(weather.getDayTemperature(1) + "°   "  +
                weather.getNightTemperature(1) + "°");
        mThirdDayWeekDay.setText(getWeekDay((field + 2) % 7));
        mThirdDayTemperature.setText(weather.getDayTemperature(2) + "°   "  +
                weather.getNightTemperature(2) + "°");
        if (isNight) {
            mTomorrowIcon.setImageResource(mNightWeatherIconsArray[
                Integer.parseInt(weather.getNightWeatherPhenomenon(1))]);
            mThirdDayIcon.setImageResource(mNightWeatherIconsArray[
                    Integer.parseInt(weather.getNightWeatherPhenomenon(2))]);
        }
        else {
            mTomorrowIcon.setImageResource(mDayWeatherIconsArray[
                    Integer.parseInt(weather.getDayWeatherPhenomenon(1))]);
            mThirdDayIcon.setImageResource(mDayWeatherIconsArray[
                    Integer.parseInt(weather.getDayWeatherPhenomenon(2))]);
        }

        String suggestion = null;
        int index1 = 0;
        int index2 = 0;
        if (isNight) {
            index1 = Integer.parseInt(weather.getNightWindPower(0));
            index2 = Integer.parseInt(weather.getNightWeatherPhenomenon(0));
            mWindDirect.setText(mWindDirects[Integer.parseInt(weather.getNightWindDirection(0))]);
            mWindPower.setText(mWindPowers[index1]);
            suggestion = getSuggestion(index1, index2);
            mSuggestion.setText(suggestion);
        }
        else {
            index1 = Integer.parseInt(weather.getDayWindPower(0));
            index2 = Integer.parseInt(weather.getDayWeatherPhenomenon(0));
            mWindDirect.setText(mWindDirects[Integer.parseInt(weather.getDayWindDirection(0))]);
            mWindPower.setText(mWindPowers[index1]);
            suggestion = getSuggestion(index1, index2);
            mSuggestion.setText(suggestion);
        }
        mSunriseAndSunsetTime.setText(weather.getSunriseAndSunsetTime(0));
    }

    private String getWeekDay(int field) {
        String weekDay = null;

        switch (field) {
            case Calendar.SUNDAY:
                weekDay = "星期天";
                break;
            case Calendar.MONDAY:
                weekDay = "星期一";
                break;
            case Calendar.TUESDAY:
                weekDay = "星期二";
                break;
            case Calendar.WEDNESDAY:
                weekDay = "星期三";
                break;
            case Calendar.THURSDAY:
                weekDay = "星期四";
                break;
            case Calendar.FRIDAY:
                weekDay = "星期五";
                break;
            default:
                weekDay = "星期六";
                break;
        }
        return weekDay;
    }

    // index1 is mWindPowers' index, index2 is mWeatherPhenomena's index
    private String getSuggestion(int index1, int index2) {
        if (index1 > 7) {
            return "宅";
        }

        Calendar calendar = Calendar.getInstance();
        boolean isWeekend = (calendar.get(Calendar.DAY_OF_WEEK) == 7) ||
                (calendar.get(Calendar.DAY_OF_WEEK) == 1);
        String suggestion = null;

        switch (index2) {
            case 0:case 1:case 2:
                if (!isWeekend) {
                    suggestion = "上课, 自习";
                }
                else {
                    suggestion = "自习, 运动, 购物";
                }
                break;
            case 10:case 11:case 12:case 17:case 20:case 24:case 25:case 30:case 31:
                suggestion = "宅";
                break;
            default:
                if (!isWeekend) {
                    suggestion = "上课";
                }
                else {
                    suggestion = "宅";
                }
                break;
        }

        return suggestion;
    }

    private class GetAreaName extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            Log.i("GetAreaName", "GetAreaName start");
            if (getActivity() == null) {
                return null;
            }

            String name = null;

            if (mAreaName == null) {
                if (mLastLocation != null) {
                    ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(
                            Context.CONNECTIVITY_SERVICE);

                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    boolean isConnected = activeNetwork != null &&
                            activeNetwork.isConnectedOrConnecting();
                    if (!isConnected) {
                        return null;
                    }

                    double latitude = mLastLocation.getLatitude();
                    double longitude = mLastLocation.getLongitude();
                    Geocoder geocoder = new Geocoder(getActivity(), Locale.CHINESE);
                    StringBuilder builder = new StringBuilder();
                    try {
                        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        int maxLines = addresses.get(0).getMaxAddressLineIndex();
                        for (int i = 0; i < maxLines; i++) {
                            String address = addresses.get(0).getAddressLine(i);
                            builder.append(address);
                        }

                        if (builder.toString().contains("神农架林区")) {
                            name = "神农架";
                        } else {
                            String[] results = builder.toString().split("[省市区县]");
                            name = results[1];
                        }
                    } catch (IOException ioe) {
                        Log.e("Geocoder", "IOException", ioe);
                    }
                }
                return name;
            }
            else {
                return mAreaName;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (mLastLocation == null && mAreaName == null) {
                Toast.makeText(getActivity(), R.string.location_is_off, Toast.LENGTH_SHORT).show();

                return;
            }

            if (s == null) {
                Toast.makeText(getActivity(), R.string.not_connect_network,
                        Toast.LENGTH_SHORT).show();

                return;
            }

            getActivity().setTitle(s);

            new GetWeather(s).execute();
        }
    }

    private class GetWeather extends AsyncTask<Void, Void, Weather> {
        private String mName;

        public GetWeather(String name) {
            mName = name;
        }


        @Override
        protected Weather doInBackground(Void... params) {
            Log.i("GetWeather", "GetWeather start");
            if (getActivity() == null) {
                return Weather.getInstance(getActivity());
            }

            String areaId = mManager.queryAreaId(mName);

            String json = null;
            try {
                json = WeatherFetcher.getWeather(areaId, "forecast_v");
            } catch (IOException ioe) {
                Log.e("getWeather", "Unable to get json weather information", ioe);
            }

            if (json == null) {
                return null;
            }
            Weather weather = Weather.getInstance(getActivity());
            try {
                JSONObject jsonResult = new JSONObject(json);
                JSONObject forecast = jsonResult.getJSONObject("f");
                JSONArray forecasts = forecast.getJSONArray("f1");

                for (int i = 0; i < forecasts.length(); i++) {
                    JSONObject object = forecasts.getJSONObject(i);
                    weather.setDayWeatherPhenomena(i, object.getString("fa"));
                    if ((!weather.getDayWeatherPhenomenon(i).equals("")) &&
                            (Integer.parseInt(weather.getDayWeatherPhenomenon(i)) >= 53)) {
                        weather.setDayWeatherPhenomena(i, "32");
                    }

                    weather.setNightWeatherPhenomena(i, object.getString("fb"));
                    if ((!weather.getNightWeatherPhenomenon(i).equals("")) &&
                            (Integer.parseInt(weather.getNightWeatherPhenomenon(i)) >= 53)) {
                        weather.setNightWeatherPhenomena(i, "32");
                    }

                    weather.setDayTemperatures(i, object.getString("fc"));
                    weather.setNightTemperatures(i, object.getString("fd"));
                    weather.setDayWindDirections(i, object.getString("fe"));
                    weather.setNightWindDirections(i, object.getString("ff"));
                    weather.setDayWindPowers(i, object.getString("fg"));
                    weather.setNightWindPowers(i, object.getString("fh"));
                    weather.setSunriseAndSunsetTimes(i, object.getString("fi"));

                    Log.i(String.valueOf(i), object.toString());
                }
            } catch (JSONException jse) {
                Log.e("GetWeather", "Unable to convert json", jse);
            }

            return weather;
        }

        @Override
        protected void onPostExecute (Weather weather){
            if (weather == null) {
                Toast.makeText(getActivity(), R.string.not_connect_network,
                        Toast.LENGTH_SHORT).show();

                return;
            }
            updateUI(weather);
        }
    }
}