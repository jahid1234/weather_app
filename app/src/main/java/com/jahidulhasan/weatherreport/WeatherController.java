package com.jahidulhasan.weatherreport;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class WeatherController extends AppCompatActivity {

    // Constants:
    final int REQUEST_CODE = 123;
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    // App ID to use OpenWeather data
    final String APP_ID = "bdf83c0e10fba585572270e7823a4752";
    // Time between location updates (5000 milliseconds or 5 seconds)
    final long MIN_TIME = 5000;
    // Distance between location updates (1000m or 1km)
    final float MIN_DISTANCE = 1000;

    // TODO: Set LOCATION_PROVIDER here:

    String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER; // physical device hole Network_provider


    // Member Variables:
    TextView mCityLabel;
    ImageView mWeatherImage;
    TextView mTemperatureLabel;

    // TODO: Declare a LocationManager and a LocationListener here:
    LocationManager locationManager;      // start or stop requesting location update
    LocationListener locationListener;     // this component will be notified if the location has actually changed


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_controller_layout);

        // Linking the elements in the layout to Java code
        mCityLabel = (TextView) findViewById(R.id.locationTV);
        mWeatherImage = (ImageView) findViewById(R.id.weatherSymbolIV);
        mTemperatureLabel = (TextView) findViewById(R.id.tempTV);
        ImageButton changeCityButton = (ImageButton) findViewById(R.id.changeCityButton);


        // TODO: Add an OnClickListener to the changeCityButton here:

        changeCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  intent = new Intent(WeatherController.this,CityActivity.class);
                startActivity(intent);
            }
        });

    }


    // TODO: Add onResume() here:
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Clima", "onResume: Called ");

        Intent myintent = getIntent();
        String city = myintent.getStringExtra("City");
        if(!TextUtils.isEmpty(city)){
            getWeatherForNewCity(city);

        }
        else {
            Log.d("Clima", "weather information process starting ");
            getWeatherForCurrentLocation();
        }
    }


    // TODO: Add getWeatherForNewCity(String city) here:
    private void getWeatherForNewCity(String city){

        Log.d("Clima", "getWeatherForNewCity: new city called !! ");
        RequestParams requestParams = new RequestParams();
        requestParams.put("q",city);
        requestParams.put("appid",APP_ID);
        letsDoSomeNetworking(requestParams);

    }


    // TODO: Add getWeatherForCurrentLocation() here:

    private void getWeatherForCurrentLocation() {
        Log.d("Clima", "entered ");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("Clima", "onLocationChanged: Callback received");
                String longitude = String.valueOf(location.getLongitude());
                String latitude = String.valueOf(location.getLatitude());

                Log.d("Clima", "Longitude: "+ longitude);
                Log.d("Clima", "Latitude: "+ latitude);

                RequestParams requestParams = new RequestParams();
                requestParams.put("lat",latitude);
                requestParams.put("lon",longitude);
                requestParams.put("appid",APP_ID);
                letsDoSomeNetworking(requestParams);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("Clima", "onProviderDisabled: Callback!! ");

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        Log.d("Clima", " request location Updates");
        locationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("Clima", "onRequestPermissionsResult: permission Granted");
                getWeatherForCurrentLocation();
            }else{
                Log.d("Clima", "onRequestPermissionsResult: permission DEnied ");
            }
        }
    }

    // TODO: Add letsDoSomeNetworking(RequestParams params) here:

    private void letsDoSomeNetworking(RequestParams requestParams ){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEATHER_URL,requestParams,new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode , Header[] headers, JSONObject response){
                Log.d("Clima", "onSuccess: "+response.toString());
                WeatherDataModel weatherDataModel = WeatherDataModel.fromJson(response);
                updateUI(weatherDataModel);
            }
            @Override
            public void onFailure(int statusCode ,Header[] headers,Throwable e,JSONObject response){

                Log.e("Clima", "onFailure: "+ e.toString() );
                Log.d("Clima", "onFailure: "+statusCode);
            }
        });
    }


    // TODO: Add updateUI() here:
    private void updateUI(WeatherDataModel weather){

        Log.d("Clima", "updateUI:  update ui called !!!");
        mTemperatureLabel.setText(weather.getTemperature());
        mCityLabel.setText(weather.getCity());

        int resourceId = getResources().getIdentifier(weather.getIconName(),"drawable",getPackageName());
        mWeatherImage.setImageResource(resourceId);
        //getWeatherForCurrentLocation();
    }



    // TODO: Add onPause() here:


    @Override
    protected void onPause() {
        super.onPause();
        if(locationManager != null){
            locationManager.removeUpdates(locationListener);
        }
    }
}
