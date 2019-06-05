package edu.skku.team10;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ortiz.touchview.TouchImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GroundActivity extends AppCompatActivity {

    TouchImageView touchImageView;
    double lat, lon;
    OpenWeatherMapJSON parsedData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ground);

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getApplicationContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( GroundActivity.this, new String[]
                            { android.Manifest.permission.ACCESS_FINE_LOCATION },
                    0 );
        }

        lon = 145.77;
        lat = -16.92;
        final LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                360000,
                0,
                gpsLocationListener);

        touchImageView = findViewById(R.id.madang);

        try {
            WeatherAsyncTask mProcessTask = new WeatherAsyncTask();
            mProcessTask.execute().get();
        } catch (Exception e){
            e.printStackTrace();
        }
        changeGroundImg();

    }
    public class WeatherAsyncTask extends AsyncTask<String, Void, OpenWeatherMapJSON> {
        OkHttpClient client = new OkHttpClient();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected OpenWeatherMapJSON doInBackground(String... params) {
            HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.openweathermap.org/data/2.5/weather").newBuilder();
            urlBuilder.addQueryParameter("lat", Double.toString(lat) );
            urlBuilder.addQueryParameter("lon",Double.toString(lon));
            urlBuilder.addQueryParameter("appid","e96040891f0e33f1412341c876b446a5");
            String requestUrl = urlBuilder.build().toString();
            Log.d("requestURL", requestUrl);
            Request request = new Request.Builder().url(requestUrl).build();
            try
            {
                Response response = client.newCall(request).execute();
                Gson gson = new GsonBuilder().create();
                JsonParser parser = new JsonParser();
                JsonElement rootObject = parser.parse(response.body().charStream()).getAsJsonObject();
                parsedData = gson.fromJson(rootObject,OpenWeatherMapJSON.class);
                return parsedData;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(OpenWeatherMapJSON result) {
            super.onPostExecute(result);
        }
    }

    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            String provider = location.getProvider();
            lon = location.getLongitude();
            lat = location.getLatitude();
            Log.d("Update GPS", "" + provider + " " + lon + " " + lat);
            try {
                WeatherAsyncTask myProcessTask = new WeatherAsyncTask();
                myProcessTask.execute().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            changeGroundImg();
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String provider) {}
        public void onProviderDisabled(String provider) {}
    };

    private void changeGroundImg(){
        if(parsedData == null)
            return;
        Log.d("weather", parsedData.weather.get(0).main);
        switch (parsedData.weather.get(0).main){
            case "Rain":
                touchImageView.setImageResource(R.drawable.profile);
                break;
            case "Clouds":
                touchImageView.setImageResource(R.drawable.testimage);
                break;
            default:
                touchImageView.setImageResource(R.drawable.profile);
                break;
        }
    }
}
