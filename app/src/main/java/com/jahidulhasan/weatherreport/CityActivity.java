package com.jahidulhasan.weatherreport;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class CityActivity extends AppCompatActivity {

    ImageButton backButton;
    TextView cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_city_layout);

        backButton = (ImageButton)findViewById(R.id.backButton);
       final EditText enterCity = (EditText)findViewById(R.id.queryET);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        enterCity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                String newCity = enterCity.getText().toString();
                Intent cityIntent = new Intent(CityActivity.this,WeatherController.class);
                cityIntent.putExtra("City",newCity);
                startActivity(cityIntent);
                return false;
            }
        });

    }
}
